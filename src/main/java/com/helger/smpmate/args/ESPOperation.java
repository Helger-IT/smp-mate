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
 * Defines the operation to be performed by a task configuration against the SMP.
 *
 * @author Philip Helger
 * @since 1.0.2
 */
public enum ESPOperation
{
  /**
   * Add or update participants incl. their document types and business cards. This is the default
   * operation.
   */
  ADD,
  /**
   * Delete a single process (incl. all its endpoints) of the configured document type(s) from each
   * participant, without touching other processes of the same document type. Uses the phoss SMP
   * REST API <code>DELETE /{ServiceGroupId}/services/{DocumentTypeId}/{ProcessId}</code> (see
   * https://github.com/phax/phoss-smp/discussions/491, since phoss SMP v8.1.8).
   */
  DELETE_PROCESS,
  /**
   * Delete the whole service metadata of the configured document type(s) from each participant.
   * Uses the phoss SMP REST API <code>DELETE /{ServiceGroupId}/services/{DocumentTypeId}</code>.
   */
  DELETE_DOCTYPE,
  /**
   * Delete a whole participant incl. all its document types and endpoints. Uses the phoss SMP REST
   * API <code>DELETE /{ServiceGroupId}</code>.
   */
  DELETE_PARTICIPANT;
}
