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
package com.helger.smpmate.business;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.helger.smpmate.args.SPArgAuthority;
import com.helger.smpmate.args.SPArgPaths;
import com.helger.smpmate.args.SPArgSMP;
import com.helger.smpmate.args.SPArgServiceMetadata;
import com.helger.smpmate.args.SPArgTask;
import com.helger.smpmate.config.SPTask;
import com.helger.smpmate.testing.MockResources;

public final class SmpServiceTest
{
  private static final Path TEST_PATH = Paths.get ("target", "testing", SmpServiceTest.class.getCanonicalName ());
  private static final String SERVICE_GROUP_TMPLT_RSRC = "/ServiceGroup.xml";
  private static final String SERVICE_META_TMPLT_RSRC = "/ServiceMetadata.xml";
  private static final Path SERVICE_GROUP_PATH;
  private static final Path SERVICE_META_PATH;
  private static final int HTTP_OK = 200;

  private static SPTask TASK;

  static
  {
    SERVICE_GROUP_PATH = TEST_PATH.resolve ("ServiceGroup.xml");
    SERVICE_META_PATH = TEST_PATH.resolve ("ServiceMetadata.xml");
  }

  @BeforeClass
  public static void beforeClass () throws Exception
  {
    final SPArgAuthority auth = new SPArgAuthority ("smp@example.org", "myPassword");
    final SPArgSMP smp = new SPArgSMP ("http://peppol-smp.example.org", auth);
    final SPArgPaths paths = new SPArgPaths ("",
                                             "",
                                             SERVICE_GROUP_PATH.toString (),
                                             Collections.singletonList (new SPArgServiceMetadata (SERVICE_META_PATH.toString (),
                                                                                                  "urn:oasis:names:specification:ubl:schema:xsd:Tender-2::Tender##urn:www.cenbii.eu:transaction:biitrdm090:ver3.0::2.1",
                                                                                                  "urn:www.cenbii.eu:profile:bii54:ver3.0")));
    TASK = new SPTask (null, new SPArgTask (paths, smp, null, null));
    Files.createDirectories (TEST_PATH);
    MockResources.using (SmpServiceTest.class)
                 .copy (SERVICE_GROUP_TMPLT_RSRC, SERVICE_GROUP_PATH)
                 .copy (SERVICE_META_TMPLT_RSRC, SERVICE_META_PATH);
  }

  @Test
  @Ignore ("This test fails, except a real SMP is configured")
  public void registerUser () throws Exception
  {
    final String userId = "9930:de888111129";
    final SmpService service = new SmpService (TASK);
    assertEquals (HTTP_OK, service.registerParticipant (userId));
    assertEquals (Collections.singletonList (Integer.valueOf (HTTP_OK)), service.addDocumentTypeIDs (userId));
    assertEquals (HTTP_OK, service.deleteParticipant (userId));
  }

  @Test
  @Ignore ("This test fails, except a real SMP is configured")
  public void deleteProcess () throws Exception
  {
    final String userId = "9930:de888111129";
    final SmpService service = new SmpService (TASK);
    // Delete only the configured process(es) - phoss SMP #491 API
    assertEquals (Collections.singletonList (Integer.valueOf (HTTP_OK)), service.deleteProcesses (userId));
    // Delete the whole document type(s)
    assertEquals (Collections.singletonList (Integer.valueOf (HTTP_OK)), service.deleteDocumentTypeIDs (userId));
  }
}
