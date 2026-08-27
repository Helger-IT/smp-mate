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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nonnull;

import com.helger.smpmate.args.SPParser;

/**
 * All encodings: UTF-8
 *
 * @author Philip Helger
 */
public final class SPReader
{
  private SPReader ()
  {}

  /**
   * Retrieves a new task instance by reading a json file specified by a given
   * path
   *
   * @param aPath
   *        The path of the JSON task file to be read. May not be <code>null</code>.
   * @return The read task configuration. Never <code>null</code>.
   * @throws IOException
   *         In case of read errors
   * @throws ValidationException
   *         If the read task configuration is invalid
   */
  public static SPTask readTask (@Nonnull final Path aPath) throws IOException, ValidationException
  {
    try (final InputStream aIS = Files.newInputStream (aPath))
    {
      return new SPTask (aPath.getParent (), SPParser.readTask (aIS));
    }
  }
}
