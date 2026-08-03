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
package com.helger.smpmate.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.nio.file.Files;
import java.util.EnumSet;

import javax.annotation.Nonnull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.helger.smpmate.TestFiles;
import com.helger.smpmate.args.ESPArgOption;
import com.helger.smpmate.args.ESPOperation;
import com.helger.smpmate.args.SPArgAuthority;
import com.helger.smpmate.args.SPArgProxy;
import com.helger.smpmate.args.SPArgSMP;
import com.helger.smpmate.testing.MockResources;

public final class SPReaderTest
{
  @BeforeClass
  public static void beforeClass () throws Exception
  {
    Files.createDirectories (TestFiles.TEST_PATH);
    MockResources.using (SPReaderTest.class)
                 .copy (TestFiles.SAMPLE_TASK_RESOURCE, TestFiles.SAMPLE_TASK_PATH)
                 .copy (TestFiles.SAMPLE_TASK_DRY_RUN_RESOURCE, TestFiles.SAMPLE_TASK_DRY_RUN_PATH)
                 .copy (TestFiles.SAMPLE_TASK_NO_OPTIONS_RESOURCE, TestFiles.SAMPLE_TASK_NO_OPTIONS_PATH)
                 .copy (TestFiles.SAMPLE_TASK_NO_PROXY_RESOURCE, TestFiles.SAMPLE_TASK_NO_PROXY_PATH)
                 .copy (TestFiles.SAMPLE_TASK_WITH_AUTH_RESOURCE, TestFiles.SAMPLE_TASK_WITH_AUTH_PATH)
                 .copy (TestFiles.SAMPLE_TASK_DELETE_PROCESS_RESOURCE, TestFiles.SAMPLE_TASK_DELETE_PROCESS_PATH)
                 .copy (TestFiles.SAMPLE_TASK_CUSTOM_SCHEME_RESOURCE, TestFiles.SAMPLE_TASK_CUSTOM_SCHEME_PATH);
  }

  private static void _assertTask (@Nonnull final SPTask aTask)
  {
    assertNotNull (aTask);

    final SPPaths paths = aTask.getPaths ();
    assertEquals (paths.getCsvInput (), TestFiles.TEST_PATH.resolve ("input.csv"));
    assertEquals (paths.getCsvFailOutput (), TestFiles.TEST_PATH.resolve ("output.csv"));
    assertEquals (paths.getServiceGroupTemplate (), TestFiles.TEST_PATH.resolve ("ServiceGroup.xml"));
    assertEquals (1, paths.getServiceMetadata ().size ());
    assertEquals (paths.getServiceMetadata ().get (0).getTemplate (), TestFiles.TEST_PATH.resolve ("ServiceMetadata.xml"));
    // No schemes in the sample JSON -> defaults apply
    assertEquals ("busdox-docid-qns", paths.getServiceMetadata ().get (0).getDocumentIdentifierScheme ());
    assertEquals ("cenbii-procid-ubl", paths.getServiceMetadata ().get (0).getProcessIdentifierScheme ());

    final SPArgSMP smp = aTask.getSmp ();
    assertNotNull (smp);
    assertEquals ("http://sample-smp.example.org/", smp.getUrl ());
    _assertAuthority (smp.getAuthority ());
  }

  private static void _assertProxy (final SPArgProxy proxy)
  {
    assertNotNull (proxy);
    assertEquals ("10.21.0.8", proxy.getHost ());
    assertEquals ("8080", proxy.getPort ());
  }

  private static void _assertAuthority (final SPArgAuthority authority)
  {
    assertNotNull (authority);
    assertEquals ("smp-user", authority.getName ());
    assertEquals ("smp-pw", authority.getPassword ());
  }

  @Test
  public void readTask () throws Exception
  {
    final SPTask task = SPReader.readTask (TestFiles.SAMPLE_TASK_PATH);
    _assertTask (task);
    assertEquals (task.getOptions (), EnumSet.noneOf (ESPArgOption.class));
    // No "operation" in JSON -> defaults to ADD
    assertEquals (ESPOperation.ADD, task.getOperation ());

    if (false)
    {
      final SPArgProxy proxy = task.getProxy ();
      _assertProxy (proxy);
      assertNull (proxy.getAuthority ());
    }
  }

  @Test
  public void readTaskDeleteProcess () throws Exception
  {
    final SPTask task = SPReader.readTask (TestFiles.SAMPLE_TASK_DELETE_PROCESS_PATH);
    _assertTask (task);
    assertEquals (ESPOperation.DELETE_PROCESS, task.getOperation ());
  }

  @Test
  public void readTaskCustomScheme () throws Exception
  {
    final SPTask task = SPReader.readTask (TestFiles.SAMPLE_TASK_CUSTOM_SCHEME_PATH);
    assertNotNull (task);
    assertEquals ("my-docid-scheme", task.getPaths ().getServiceMetadata ().get (0).getDocumentIdentifierScheme ());
    assertEquals ("my-procid-scheme", task.getPaths ().getServiceMetadata ().get (0).getProcessIdentifierScheme ());
  }

  @Test
  public void readTaskDryRun () throws Exception
  {
    final SPTask task = SPReader.readTask (TestFiles.SAMPLE_TASK_DRY_RUN_PATH);
    _assertTask (task);
    assertEquals (task.getOptions (), EnumSet.of (ESPArgOption.DRY_RUN));

    if (false)
    {
      final SPArgProxy proxy = task.getProxy ();
      _assertProxy (proxy);
      assertNull (proxy.getAuthority ());
    }
  }

  @Test
  public void readTaskNoOptions () throws Exception
  {
    final SPTask task = SPReader.readTask (TestFiles.SAMPLE_TASK_NO_OPTIONS_PATH);
    _assertTask (task);
    assertEquals (task.getOptions (), EnumSet.noneOf (ESPArgOption.class));
  }

  @Test
  public void readTaskNoProxy () throws Exception
  {
    final SPTask task = SPReader.readTask (TestFiles.SAMPLE_TASK_NO_PROXY_PATH);
    _assertTask (task);
    assertNull (task.getProxy ());
  }

  @Test
  public void readTaskWithAuth () throws Exception
  {
    final SPTask task = SPReader.readTask (TestFiles.SAMPLE_TASK_WITH_AUTH_PATH);
    _assertTask (task);

    final SPArgProxy proxy = task.getProxy ();
    _assertProxy (proxy);
    _assertAuthority (proxy.getAuthority ());
  }
}
