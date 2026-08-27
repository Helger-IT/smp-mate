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

import java.io.BufferedReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.annotation.Nonnull;

import com.helger.smpmate.business.UserConfigurator;
import com.helger.smpmate.config.SMPMateVersion;
import com.helger.smpmate.config.SPPaths;
import com.helger.smpmate.config.SPReader;
import com.helger.smpmate.config.SPTask;
import com.helger.smpmate.log.MyLog;
import com.helger.smpmate.util.Tokenizer;

/**
 * Provides the main entry point of the command line application.
 *
 * @author Philip Helger
 */
public final class Main
{
  /**
   * The main entry point of the command line application.
   *
   * @param aArgs
   *        The command line arguments. Exactly one argument is expected: the path to the task file
   *        to be executed. If the file does not exist, a task file template is created at that
   *        location instead.
   */
  public static void main (@Nonnull final String... aArgs)
  {
    try
    {
      MyLog.info (() -> "SMP Mate v" +
                        SMPMateVersion.BUILD_VERSION +
                        " (built " +
                        SMPMateVersion.BUILD_TIMESTAMP +
                        ")");

      if (1 == aArgs.length)
      {
        final Path aTaskPath = Paths.get (aArgs[0]).toAbsolutePath ().normalize ();
        if (aTaskPath.toFile ().exists ())
        {
          final SPTask aTask = SPReader.readTask (aTaskPath);
          final UserConfigurator aConfigurator = new UserConfigurator (aTask);

          // Read source "CSV"
          try (final BufferedReader aReader = Files.newBufferedReader (aTask.getPaths ().getCsvInput (),
                                                                       StandardCharsets.UTF_8))
          {
            String sCsvLine;
            while ((sCsvLine = aReader.readLine ()) != null)
            {
              // Use ';' as field separator
              final Tokenizer tokenizer = new Tokenizer (sCsvLine.trim (), ";");
              final String [] tokens = tokenizer.tokens ();

              // Trim to avoid whitespace in the participant ID
              final String participantId = tokens.length >= 1 ? tokens[0].trim () : null;
              final Path bcPath = tokens.length >= 2 ? SPPaths.toPath (aTaskPath.getParent (), tokens[1]) : null;

              aConfigurator.update (participantId, bcPath);
            }
          }

          // Log statistics
          MyLog.info (() -> aConfigurator.getStats ().getAsString ());

          // Write result CSV
          aConfigurator.saveAllFailedOnes ();
        }
        else
        {
          // Copy template to disk
          MyLog.info (() -> "Task file '" + aTaskPath + "' does not exist -> a new task file template will be created");
          try (final InputStream aIS = Main.class.getResourceAsStream ("/default.task.json"))
          {
            Files.createDirectories (aTaskPath.getParent ());
            Files.copy (aIS, aTaskPath);
          }
          MyLog.info (() -> "Successfully created task file template at '" + aTaskPath + "'");
        }
      }
      else
      {
        MyLog.warning (() -> "Requires a single argument:\n" +
                             "                      PATH: a path to the desired task file\n" +
                             "                   but was: " +
                             Arrays.toString (aArgs) +
                             "\n\n" +
                             "To create a new task file from a template, specify a path to a nonexistent file.");
      }
    }
    catch (final Exception ex)
    {
      MyLog.error (() -> "Error executing tasks", ex);
    }
  }
}
