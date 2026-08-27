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

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

/**
 * Thrown if a task configuration is syntactically readable but semantically invalid.
 *
 * @author Philip Helger
 */
public class ValidationException extends Exception
{
  /**
   * Initializes a new instance with all found problems.
   *
   * @param aProblems
   *        The list of all problems found in the task configuration. May not be <code>null</code>.
   */
  ValidationException (@Nonnull final List <String> aProblems)
  {
    super ("The task configuration is invalid:" +
           aProblems.stream ().map (topic -> "\n- " + topic).collect (Collectors.joining ()));
  }
}
