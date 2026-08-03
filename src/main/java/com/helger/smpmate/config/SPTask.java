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
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.smpmate.args.ESPArgOption;
import com.helger.smpmate.args.ESPOperation;
import com.helger.smpmate.args.SPArgProxy;
import com.helger.smpmate.args.SPArgSMP;
import com.helger.smpmate.args.SPArgTask;

/**
 * Represents a task configuration from business point of view.
 *
 * @author Philip Helger
 * @see com.helger.smpmate.args.SPArgTask
 */
public final class SPTask
{
  private final SPPaths m_aPaths;
  private final SPArgSMP m_aSmp;
  private final SPArgProxy m_aProxy;
  private final EnumSet <ESPArgOption> m_aOptions;
  private final ESPOperation m_eOperation;

  /*
   * Initializes a task configuration.
   */
  public SPTask (@Nullable final Path aRel, @Nonnull final SPArgTask aOrigin) throws ValidationException
  {
    m_aPaths = new SPPaths (aRel, aOrigin.getPaths ());
    m_aSmp = aOrigin.getSmp ();
    m_aProxy = aOrigin.getProxy ();
    m_aOptions = aOrigin.getOptions () == null ? EnumSet.noneOf (ESPArgOption.class) : EnumSet.copyOf (aOrigin
                                                                                                              .getOptions ());
    m_eOperation = aOrigin.getOperation () == null ? ESPOperation.ADD : aOrigin.getOperation ();
  }

  /**
   * @return the paths for this task
   */
  @Nonnull
  public SPPaths getPaths ()
  {
    return m_aPaths;
  }

  /**
   * @return the SMP configuration for this task
   */
  public SPArgSMP getSmp ()
  {
    return m_aSmp;
  }

  /**
   * @return the proxy configuration for this task
   */
  @Nullable
  public SPArgProxy getProxy ()
  {
    return m_aProxy;
  }

  /**
   * @return the options for this task
   */
  @Nonnull
  public Set <ESPArgOption> getOptions ()
  {
    return Collections.unmodifiableSet (m_aOptions);
  }

  /**
   * @return the operation for this task. Never <code>null</code>, defaults to
   *         {@link ESPOperation#ADD}.
   */
  @Nonnull
  public ESPOperation getOperation ()
  {
    return m_eOperation;
  }
}
