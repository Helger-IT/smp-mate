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
package com.helger.smpmate.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Diese Klasse ist eine Abwandlung von {@link StringTokenizer}, die mehr den Erwartungen
 * entspricht, wenn die Trennzeichen keine Whitespaces sind. Die Klasse {@link StringTokenizer}
 * bündelt benachbarte Trennzeichen und macht sie dadurch für CSV-Belange unbrauchbar. Diese Klasse
 * hingegen behandelt jedes Trennzeichen als LF (linefeed) und erfasst jedes Token quasi als Zeile
 * einer Testdatei.
 */
public class Tokenizer implements Iterable <String>
{
  private static final char LF = '\n';

  private final List <String> tokens = new ArrayList <> ();

  /**
   * Erzeugt einen neuen Tokenizer mit den angegebenen Trennzeichen.
   *
   * @param str
   *        Die zu zerlegende Zeichenkette.
   * @param sep
   *        Die Zeichenkette, deren einzelne Zeichen jeweils als Trennzeichen gelten.
   */
  public Tokenizer (final String str, final String sep)
  {
    final int n = str.length ();
    final StringBuilder builder = new StringBuilder (str);
    for (int i = 0; i < n; i++)
    {
      if (sep.indexOf (builder.charAt (i)) >= 0)
      {
        builder.setCharAt (i, LF);
      }
    }

    if (n > 0 && sep.indexOf (str.charAt (n - 1)) >= 0)
    {
      builder.append (LF);
    }

    try (BufferedReader reader = new BufferedReader (new StringReader (builder.toString ())))
    {
      for (String line; (line = reader.readLine ()) != null;)
      {
        tokens.add (line);
      }
    }
    catch (final IOException e)
    {
      throw new UncheckedIOException (e);
    }
  }

  /**
   * @return Alle ermittelten Tokens in der Reihenfolge ihres Auftretens. Nie <code>null</code>.
   */
  public String [] tokens ()
  {
    return tokens.toArray (new String [0]);
  }

  @Override
  public Iterator <String> iterator ()
  {
    return tokens.iterator ();
  }
}
