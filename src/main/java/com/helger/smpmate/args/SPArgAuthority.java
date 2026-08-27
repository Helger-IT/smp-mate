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

/**
 * Contains authorization parameters for usage within a task configuration.
 *
 * @author Philip Helger
 */
public final class SPArgAuthority
{
  private final String m_sName;
  private final String m_sPassword;

  /**
   * Initializes a new instance by its properties.
   *
   * @param sName
   *        The user name to be used.
   * @param sPassword
   *        The password to be used.
   */
  public SPArgAuthority (final String sName, final String sPassword)
  {
    m_sName = sName;
    m_sPassword = sPassword;
  }

  /**
   * Retrieves the name
   *
   * @return The user name as provided in the constructor.
   */
  public String getName ()
  {
    return m_sName;
  }

  /**
   * Retrieves the password
   *
   * @return The password as provided in the constructor.
   */
  public String getPassword ()
  {
    return m_sPassword;
  }
}
