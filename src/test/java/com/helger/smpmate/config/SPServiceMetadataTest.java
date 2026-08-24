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
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.smpmate.args.SPArgServiceMetadata;

public class SPServiceMetadataTest
{
  @Test
  public final void trimIdentifiers ()
  {
    final SPServiceMetadata result = new SPServiceMetadata (null,
                                                            new SPArgServiceMetadata ("metadataTemplate.xml",
                                                                                      " docScheme\t",
                                                                                      "\n doc ",
                                                                                      "  procScheme ",
                                                                                      " proc\r\n"));
    assertEquals ("docScheme", result.getDocumentIdentifierScheme ());
    assertEquals ("doc", result.getDocumentIdentifier ());
    assertEquals ("procScheme", result.getProcessIdentifierScheme ());
    assertEquals ("proc", result.getProcessIdentifier ());
  }

  @Test
  public final void keepNullIdentifiers ()
  {
    final SPServiceMetadata result = new SPServiceMetadata (null,
                                                            new SPArgServiceMetadata ("metadataTemplate.xml",
                                                                                      null,
                                                                                      null));
    assertEquals (SPServiceMetadata.DEFAULT_DOCUMENT_IDENTIFIER_SCHEME, result.getDocumentIdentifierScheme ());
    assertNull (result.getDocumentIdentifier ());
    assertEquals (SPServiceMetadata.DEFAULT_PROCESS_IDENTIFIER_SCHEME, result.getProcessIdentifierScheme ());
    assertNull (result.getProcessIdentifier ());
  }
}
