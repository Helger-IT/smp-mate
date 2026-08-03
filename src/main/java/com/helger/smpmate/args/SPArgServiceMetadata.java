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
package com.helger.smpmate.args;

import javax.annotation.Nullable;

public final class SPArgServiceMetadata
{
  private final String m_sTemplate;
  private final String m_sDocumentIdentifierScheme;
  private final String m_sDocumentIdentifier;
  private final String m_sProcessIdentifierScheme;
  private final String m_sProcessIdentifier;

  public SPArgServiceMetadata (@Nullable final String sTemplate,
                               @Nullable final String sDocumentTypeIdentifier,
                               @Nullable final String sProcessIdentifier)
  {
    this (sTemplate, null, sDocumentTypeIdentifier, null, sProcessIdentifier);
  }

  public SPArgServiceMetadata (@Nullable final String sTemplate,
                               @Nullable final String sDocumentIdentifierScheme,
                               @Nullable final String sDocumentTypeIdentifier,
                               @Nullable final String sProcessIdentifierScheme,
                               @Nullable final String sProcessIdentifier)
  {
    m_sTemplate = sTemplate;
    m_sDocumentIdentifier = sDocumentTypeIdentifier;
    m_sDocumentIdentifierScheme = sDocumentIdentifierScheme;
    m_sProcessIdentifier = sProcessIdentifier;
    m_sProcessIdentifierScheme = sProcessIdentifierScheme;
  }

  @Nullable
  public String getTemplate ()
  {
    return m_sTemplate;
  }

  /**
   * @return the document identifier scheme or {@code null} if the default should be used.
   */
  @Nullable
  public String getDocumentIdentifierScheme ()
  {
    return m_sDocumentIdentifierScheme;
  }

  @Nullable
  public String getDocumentIdentifier ()
  {
    return m_sDocumentIdentifier;
  }

  /**
   * @return the process identifier scheme or {@code null} if the default should be used.
   */
  @Nullable
  public String getProcessIdentifierScheme ()
  {
    return m_sProcessIdentifierScheme;
  }

  @Nullable
  public String getProcessIdentifier ()
  {
    return m_sProcessIdentifier;
  }
}
