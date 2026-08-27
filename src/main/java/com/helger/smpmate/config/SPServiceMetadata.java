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

import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.smpmate.args.SPArgServiceMetadata;

/**
 * Represents a single ServiceMetadata configuration from business point of view.
 *
 * @author Philip Helger
 * @see com.helger.smpmate.args.SPArgServiceMetadata
 */
public final class SPServiceMetadata
{
  /** Default document identifier scheme, used if none is provided */
  public static final String DEFAULT_DOCUMENT_IDENTIFIER_SCHEME = "busdox-docid-qns";
  /** Default process identifier scheme, used if none is provided */
  public static final String DEFAULT_PROCESS_IDENTIFIER_SCHEME = "cenbii-procid-ubl";

  private final Path m_aTemplate;
  private final String m_sDocumentIdentifierScheme;
  private final String m_sDocumentIdentifier;
  private final String m_sProcessIdentifierScheme;
  private final String m_sProcessIdentifier;

  /**
   * Removes all leading and trailing whitespaces from the provided value, as they are irrelevant
   * for identifiers but break the SMP URLs
   */
  @Nullable
  private static String _trimmed (@Nullable final String sValue)
  {
    return sValue == null ? null : sValue.trim ();
  }

  /**
   * Initializes a ServiceMetadata configuration.
   *
   * @param aRel
   *        The base directory relative paths are resolved against. May be <code>null</code>.
   * @param aOrigin
   *        The ServiceMetadata from the JSON task configuration. May not be <code>null</code>.
   */
  public SPServiceMetadata (@Nullable final Path aRel, @Nonnull final SPArgServiceMetadata aOrigin)
  {
    m_aTemplate = SPPaths.toPath (aRel, aOrigin.getTemplate ());
    final String sDocumentIdentifierScheme = _trimmed (aOrigin.getDocumentIdentifierScheme ());
    m_sDocumentIdentifierScheme = sDocumentIdentifierScheme == null ? DEFAULT_DOCUMENT_IDENTIFIER_SCHEME
                                                                    : sDocumentIdentifierScheme.trim ();
    m_sDocumentIdentifier = _trimmed (aOrigin.getDocumentIdentifier ());
    final String sProcessIdentifierScheme = _trimmed (aOrigin.getProcessIdentifierScheme ());
    m_sProcessIdentifierScheme = sProcessIdentifierScheme == null ? DEFAULT_PROCESS_IDENTIFIER_SCHEME
                                                                  : sProcessIdentifierScheme.trim ();
    m_sProcessIdentifier = _trimmed (aOrigin.getProcessIdentifier ());
  }

  /**
   * @return the absolute path of the ServiceMetadata template file. Never <code>null</code>.
   */
  @Nonnull
  public Path getTemplate ()
  {
    return m_aTemplate;
  }

  /**
   * @return the document identifier scheme. Never <code>null</code>, defaults to
   *         {@link #DEFAULT_DOCUMENT_IDENTIFIER_SCHEME}.
   */
  @Nonnull
  public String getDocumentIdentifierScheme ()
  {
    return m_sDocumentIdentifierScheme;
  }

  /**
   * @return the document type identifier value or <code>null</code> if none was configured.
   */
  @Nullable
  public String getDocumentIdentifier ()
  {
    return m_sDocumentIdentifier;
  }

  /**
   * @return the process identifier scheme. Never <code>null</code>, defaults to
   *         {@link #DEFAULT_PROCESS_IDENTIFIER_SCHEME}.
   */
  @Nonnull
  public String getProcessIdentifierScheme ()
  {
    return m_sProcessIdentifierScheme;
  }

  /**
   * @return the process identifier value or <code>null</code> if none was configured.
   */
  @Nullable
  public String getProcessIdentifier ()
  {
    return m_sProcessIdentifier;
  }
}
