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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Parse the provided JSON configuration. All encodings: UTF-8
 *
 * @author Philip Helger
 */
@Immutable
public final class SPParser
{
  private static final Gson GSON = new GsonBuilder ().setFieldNamingStrategy (aF -> {
    String sName = aF.getName ();
    if (sName.startsWith ("m_"))
    {
      // m_sName => name
      sName = Character.toLowerCase (sName.charAt (3)) + sName.substring (4);
    }
    return sName;
  }).create ();

  private SPParser ()
  {}

  /**
   * Retrieves a new task instance by reading the JSON content of the provided input stream.
   *
   * @param aIS
   *        The input stream to read the JSON content from. May not be <code>null</code>.
   * @return The parsed task configuration. Never <code>null</code>.
   * @throws IOException
   *         In case of read errors
   */
  @Nonnull
  public static SPArgTask readTask (@Nonnull @WillNotClose final InputStream aIS) throws IOException
  {
    try (final Reader aReader = new InputStreamReader (aIS, StandardCharsets.UTF_8))
    {
      final SPArgTask ret = GSON.fromJson (aReader, SPArgTask.class);
      if (ret == null)
        throw new IllegalStateException ("Failed to read task from JSON");
      return ret;
    }
  }
}
