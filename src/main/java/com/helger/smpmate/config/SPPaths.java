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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.smpmate.args.SPArgPaths;

/**
 * Contains the paths of a task configuration.
 *
 * @author Philip Helger
 * @see com.helger.smpmate.config.SPPaths
 */
public final class SPPaths
{
  private final Path m_aCsvInput;
  private final Path m_aCsvFailOutput;
  private final Path m_aServiceGroupTemplate;
  private final List <SPServiceMetadata> m_aServiceMetadata;

  /**
   * Converts the provided path into an absolute and normalized path.
   *
   * @param aRel
   *        The base directory a relative path is resolved against. May be <code>null</code> in
   *        which case a relative path is resolved against the current working directory.
   * @param sPath
   *        The path to be converted. May not be <code>null</code>.
   * @return The absolute and normalized path. Never <code>null</code>.
   */
  @Nonnull
  public static Path toPath (@Nullable final Path aRel, @Nonnull final String sPath)
  {
    final Path aResult = Paths.get (sPath);
    return (aResult.isAbsolute () || aRel == null ? aResult : aRel.resolve (aResult)).toAbsolutePath ().normalize ();
  }

  @Nonnull
  private static Path _toPath (@Nullable final Path aRel,
                               @Nullable final String sPrimePath,
                               @Nonnull final String sAltPath)
  {
    return toPath (aRel, sPrimePath == null ? sAltPath : sPrimePath);
  }

  @Nonnull
  private static String _insert (final String sFilename, final String sFragment)
  {
    final int nPos = sFilename.lastIndexOf ('.');
    if (nPos < 0)
      return sFilename + sFragment;

    return sFilename.substring (0, nPos) + sFragment + sFilename.substring (nPos);
  }

  /**
   * Initializes a task configuration.
   *
   * @param aRel
   *        The base directory relative paths are resolved against. May be <code>null</code>.
   * @param aOrigin
   *        The paths from the JSON task configuration. May not be <code>null</code>.
   * @throws ValidationException
   *         If no ServiceMetadata is contained
   */
  public SPPaths (@Nullable final Path aRel, @Nonnull final SPArgPaths aOrigin) throws ValidationException
  {
    m_aCsvInput = toPath (aRel, aOrigin.getCsvInput ());
    m_aCsvFailOutput = _toPath (aRel, aOrigin.getCsvFailOutput (), _insert (aOrigin.getCsvInput (), ".fail"));
    m_aServiceGroupTemplate = toPath (aRel, aOrigin.getServiceGroupTemplate ());
    m_aServiceMetadata = aOrigin.getServiceMetadata ()
                                .stream ()
                                .map (meta -> new SPServiceMetadata (aRel, meta))
                                .toList ();
    // Validate
    final List <String> aProblems = new ArrayList <> ();
    if (m_aServiceMetadata.isEmpty ())
      aProblems.add ("\"serviceMetadata\" are empty");
    if (!aProblems.isEmpty ())
      throw new ValidationException (aProblems);
  }

  /**
   * Retrieves the path of the CSV input file
   *
   * @return The absolute path of the CSV input file. Never <code>null</code>.
   */
  @Nonnull
  public Path getCsvInput ()
  {
    return m_aCsvInput;
  }

  /**
   * Retrieves the path of the CSV fail output file
   *
   * @return The absolute path of the CSV fail output file. Never <code>null</code>.
   */
  @Nonnull
  public Path getCsvFailOutput ()
  {
    return m_aCsvFailOutput;
  }

  /**
   * Retrieves the path of the ServiceGroup template
   *
   * @return The absolute path of the ServiceGroup template file. Never <code>null</code>.
   */
  @Nonnull
  public Path getServiceGroupTemplate ()
  {
    return m_aServiceGroupTemplate;
  }

  /**
   * Retrieves the ServiceMetadata configurations
   *
   * @return An unmodifiable list of all contained ServiceMetadata configurations. Never
   *         <code>null</code> and never empty.
   */
  @Nonnull
  public List <SPServiceMetadata> getServiceMetadata ()
  {
    return m_aServiceMetadata;
  }
}
