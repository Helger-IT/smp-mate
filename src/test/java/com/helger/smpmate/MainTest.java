/*
 * Copyright (C) 2022-204 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.smpmate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import com.helger.smpmate.args.ESPArgOption;
import com.helger.smpmate.business.SmpService;
import com.helger.smpmate.config.SPReader;
import com.helger.smpmate.config.SPTask;
import com.helger.smpmate.testing.MockResources;

public final class MainTest
{
  private static final String SAMPLE_CSV_NAME = "/sample.csv";
  private static final String SAMPLE_BC_CSV_NAME = "/sample-bc.csv";
  private static final String SAMPLE_BC_XML_NAME = "/sample-bc.xml";
  private static final String SAMPLE_WHITESPACE_CSV_NAME = "/sample-whitespace.csv";
  private static final String SERVICE_GROUP_TMPLT_RSRC = "/sample/input/ServiceGroup.xml";
  private static final String SERVICE_META_TMPLT_RSRC = "/sample/input/ServiceMetadata-PEPPOL-cii_invoice.xml";
  private static final MockResources RESOURCES = MockResources.using (MainTest.class);

  private static final Path csvInputPath = TestFiles.TEST_PATH.resolve ("input.csv");
  private static final Path csvBcInputPath = TestFiles.TEST_PATH.resolve ("input-bc.csv");
  private static final Path xmlBcInputPath = TestFiles.TEST_PATH.resolve ("sample-bc.xml");
  private static final Path csvWhitespaceInputPath = TestFiles.TEST_PATH.resolve ("input-whitespace.csv");
  private static final Path csvOutputPath = TestFiles.TEST_PATH.resolve ("output.csv");
  private static final Path csvWhitespaceOutputPath = TestFiles.TEST_PATH.resolve ("output-whitespace.csv");
  private static final Path serviceGroupPath = TestFiles.TEST_PATH.resolve ("ServiceGroup.xml");
  private static final Path serviceMetaPath = TestFiles.TEST_PATH.resolve ("ServiceMetadata.xml");

  private static FileTime _prepareInput () throws IOException
  {
    RESOURCES.copy (SAMPLE_CSV_NAME, csvInputPath)
             .copy (SAMPLE_BC_CSV_NAME, csvBcInputPath)
             .copy (SAMPLE_BC_XML_NAME, xmlBcInputPath)
             .copy (TestFiles.SAMPLE_TASK_RESOURCE, TestFiles.SAMPLE_TASK_PATH)
             .copy (TestFiles.SAMPLE_TASK_BC_RESOURCE, TestFiles.SAMPLE_TASK_BC_PATH)
             .copy (TestFiles.SAMPLE_TASK_DRY_RUN_RESOURCE, TestFiles.SAMPLE_TASK_DRY_RUN_PATH)
             .copy (TestFiles.SAMPLE_TASK_FAIL_SMP_RESOURCE, TestFiles.SAMPLE_TASK_FAIL_SMP_PATH)
             .copy (SERVICE_GROUP_TMPLT_RSRC, serviceGroupPath)
             .copy (SERVICE_META_TMPLT_RSRC, serviceMetaPath);
    return Files.getLastModifiedTime (TestFiles.SAMPLE_TASK_PATH);
  }

  private static void _cleanupSMP (final SPTask task) throws IOException
  {
    if (!task.getOptions ().contains (ESPArgOption.DRY_RUN))
    {
      final SmpService service = new SmpService (task);
      for (final String userId : Arrays.asList ("9930:de999111111",
                                                "9930:de999111112",
                                                "9930:de999111114",
                                                "9930:de999111115",
                                                "9930:de999111116",
                                                "9930:de999111117",
                                                "9930:de999111118",
                                                "9930:de999111119",
                                                "9930:de999268497"))
      {
        service.deleteParticipant (userId);
      }
    }
  }

  @Test
  public void mainNoArgs () throws IOException
  {
    Files.deleteIfExists (csvOutputPath);

    // Not exactly 1 param
    Main.main ();

    assertFalse ("should not exist: " + csvOutputPath, Files.exists (csvOutputPath));
  }

  @Test
  public void main2args () throws IOException
  {
    Files.deleteIfExists (csvOutputPath);

    // Not exactly 1 param
    Main.main (csvInputPath.toString (), csvOutputPath.toString ());

    assertFalse ("should not exist: " + csvOutputPath, Files.exists (csvOutputPath));
  }

  @Test
  public void mainNoJsonFile () throws IOException
  {
    RESOURCES.copy (SAMPLE_CSV_NAME, TestFiles.SAMPLE_TASK_PATH);
    assertTrue ("should exist: " + TestFiles.SAMPLE_TASK_PATH, Files.exists (TestFiles.SAMPLE_TASK_PATH));

    // not a JSON -> error
    Main.main (TestFiles.SAMPLE_TASK_PATH.toString ());
  }

  @Test
  public void mainNoRegularFile () throws IOException
  {
    Files.createDirectories (TestFiles.TEST_PATH);

    // Directory and not a file
    Main.main (TestFiles.TEST_PATH.toString ());
    assertTrue ("should be a directory: " + TestFiles.TEST_PATH, Files.isDirectory (TestFiles.TEST_PATH));
  }

  @Test
  public void mainMissingTask ()
  {
    final Path uniqueTaskPath = TestFiles.TEST_PATH.resolve (UUID.randomUUID () + ".json");

    // Task file does not exist - create template to that name
    Main.main (uniqueTaskPath.toString ());

    assertTrue ("should exist: " + uniqueTaskPath, Files.exists (uniqueTaskPath));
  }

  @Test
  @Ignore ("Really calls SMP")
  public void mainExistingTask () throws Exception
  {
    final FileTime time0 = _prepareInput ();

    // Run valid
    Main.main (TestFiles.SAMPLE_TASK_PATH.toString ());

    final FileTime timeX = Files.getLastModifiedTime (TestFiles.SAMPLE_TASK_PATH);
    assertTrue ("expected: " + time0 + " <= " + timeX, time0.compareTo (timeX) <= 0);

    _cleanupSMP (SPReader.readTask (TestFiles.SAMPLE_TASK_PATH));
  }

  @Test
  @Ignore ("Really calls SMP")
  public void mainExistingBusinessCardTask () throws Exception
  {
    final FileTime time0 = _prepareInput ();

    // Run valid
    Main.main (TestFiles.SAMPLE_TASK_BC_PATH.toString ());

    final FileTime timeX = Files.getLastModifiedTime (TestFiles.SAMPLE_TASK_BC_PATH);
    assertTrue ("expected: " + time0 + " <= " + timeX, time0.compareTo (timeX) <= 0);

    _cleanupSMP (SPReader.readTask (TestFiles.SAMPLE_TASK_BC_PATH));
  }

  @Test
  public void mainExistingFailSMP () throws Exception
  {
    final FileTime time0 = _prepareInput ();

    // Invalid SMP path and credentials
    Main.main (TestFiles.SAMPLE_TASK_FAIL_SMP_PATH.toString ());

    assertTrue ("should exist: " + csvOutputPath, Files.exists (csvOutputPath));

    final FileTime timeX1 = Files.getLastModifiedTime (TestFiles.SAMPLE_TASK_PATH);
    assertTrue ("expected: " + time0 + " <= " + timeX1, time0.compareTo (timeX1) <= 0);

    final FileTime timeX2 = Files.getLastModifiedTime (csvOutputPath);
    assertTrue ("expected: " + time0 + " <= " + timeX2, time0.compareTo (timeX2) <= 0);
  }

  @Test
  public void mainExistingWhitespaceCsv () throws Exception
  {
    _prepareInput ();
    RESOURCES.copy (SAMPLE_WHITESPACE_CSV_NAME, csvWhitespaceInputPath)
             .copy (TestFiles.SAMPLE_TASK_WHITESPACE_CSV_RESOURCE, TestFiles.SAMPLE_TASK_WHITESPACE_CSV_PATH);
    Files.deleteIfExists (csvWhitespaceOutputPath);

    // The participant IDs in the CSV are surrounded by whitespaces.
    // Invalid SMP path -> all participants fail and are written to the fail CSV
    Main.main (TestFiles.SAMPLE_TASK_WHITESPACE_CSV_PATH.toString ());

    assertTrue ("should exist: " + csvWhitespaceOutputPath, Files.exists (csvWhitespaceOutputPath));
    assertEquals (Arrays.asList ("9930:de999111111", "9930:de999111112"),
                  Files.readAllLines (csvWhitespaceOutputPath, StandardCharsets.UTF_8));
  }

  @Test
  public void mainExistingDryRunTask () throws Exception
  {
    final FileTime time0 = _prepareInput ();

    // All okay, except that "dry run" is enabled
    Main.main (TestFiles.SAMPLE_TASK_DRY_RUN_PATH.toString ());

    final FileTime timeX = Files.getLastModifiedTime (TestFiles.SAMPLE_TASK_PATH);
    assertTrue ("expected: " + time0 + " <= " + timeX, time0.compareTo (timeX) <= 0);
  }
}
