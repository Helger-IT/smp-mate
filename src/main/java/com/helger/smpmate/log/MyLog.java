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
package com.helger.smpmate.log;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.logging.Level;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Simple logging class
 *
 * @author Philip Helger
 */
public final class MyLog
{
  private static final boolean SHOW_STACK_TRACE = false;

  private MyLog ()
  {}

  // Return n blanks in a row
  @Nonnull
  private static String _blanks (@Nonnegative final int n)
  {
    final char [] c = new char [n];
    Arrays.fill (c, ' ');
    return new String (c);
  }

  private static void _log (@Nonnull final Level eLevel,
                            @Nonnull final Supplier <String> aMsgSupplier,
                            @Nullable final Exception ex)
  {
    final PrintStream o = eLevel.intValue () >= Level.SEVERE.intValue () ? System.err : System.out;

    final String sPrefix = "[" + eLevel.getName () + "] ";
    o.println (sPrefix + aMsgSupplier.get ());
    if (ex != null)
    {
      if (SHOW_STACK_TRACE)
        ex.printStackTrace (o);
      else
        o.println (_blanks (sPrefix.length ()) + ex.getClass ().getName () + ": " + ex.getMessage ());
    }
  }

  /**
   * Logs a message on level "info".
   *
   * @param aMsgSupplier
   *        The supplier of the message to be logged. May not be <code>null</code>.
   */
  public static void info (@Nonnull final Supplier <String> aMsgSupplier)
  {
    _log (Level.INFO, aMsgSupplier, null);
  }

  /**
   * Logs a message on level "warning".
   *
   * @param aMsgSupplier
   *        The supplier of the message to be logged. May not be <code>null</code>.
   */
  public static void warning (@Nonnull final Supplier <String> aMsgSupplier)
  {
    _log (Level.WARNING, aMsgSupplier, null);
  }

  /**
   * Logs a message on level "error".
   *
   * @param aMsgSupplier
   *        The supplier of the message to be logged. May not be <code>null</code>.
   * @param ex
   *        The exception to be logged along with the message. May be <code>null</code>.
   */
  public static void error (@Nonnull final Supplier <String> aMsgSupplier, @Nullable final Exception ex)
  {
    _log (Level.SEVERE, aMsgSupplier, ex);
  }
}
