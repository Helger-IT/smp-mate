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
package com.helger.smpmate.business;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Encapsulates statistic data for a provisioning cycle.
 *
 * @author Philip Helger
 */
public final class Statistics
{
  private final Set <String> m_aSmpFails = new LinkedHashSet <> ();
  private int m_nTotal;
  private int m_nAddedToSMP;
  private int m_nRegisterUserFailed;
  private int m_aAddDocumentIdFailed;
  private int m_nDeletedFromSMP;
  private int m_nDeleteFailed;

  private int businessCardSuccessCount;
  private int businessCardFailCount;

  /**
   * Retrieves the "smpFails" list.
   *
   * @return An unmodifiable set of all participant IDs that failed on the SMP, in the order they
   *         were added. Never <code>null</code>.
   */
  @Nonnull
  public Set <String> getSmpFails ()
  {
    return Collections.unmodifiableSet (m_aSmpFails);
  }

  /**
   * Increments the "total" counter.
   */
  public void incTotalParticipants ()
  {
    m_nTotal++;
  }

  /**
   * Increments the "newToSMP" counter.
   */
  public void incAddToSMP ()
  {
    m_nAddedToSMP++;
  }

  /**
   * Increments the "registerUserFailed" counter.
   */
  public void addRegisterUserFailed ()
  {
    m_nRegisterUserFailed++;
  }

  /**
   * Increments the "addDocumentIdFailed" counter.
   */
  public void addAddDocumentIdFailed ()
  {
    m_aAddDocumentIdFailed++;
  }

  /**
   * Increments the "deletedFromSMP" counter.
   */
  public void incDeleteFromSMP ()
  {
    m_nDeletedFromSMP++;
  }

  /**
   * Increments the "deleteFailed" counter.
   */
  public void addDeleteFailed ()
  {
    m_nDeleteFailed++;
  }

  /**
   * Adds an entry to the "smpFails" list.
   *
   * @param sParticipantID
   *        The participant ID that failed on the SMP. May not be <code>null</code>.
   */
  public void addSmpFail (@Nonnull final String sParticipantID)
  {
    m_aSmpFails.add (sParticipantID);
  }

  /**
   * Increments the "businessCardSuccessCount" counter.
   */
  public void incrementBusinessCardSuccessCount ()
  {
    businessCardSuccessCount++;
  }

  /**
   * Increments the "businessCardFailCount" counter.
   */
  public void incrementBusinessCardFailCount ()
  {
    businessCardFailCount++;
  }

  /**
   * @return A human readable, multi line representation of all collected statistics. Never
   *         <code>null</code>.
   */
  @Nonnull
  public String getAsString ()
  {
    return "\n" +
           "\n  total participants .........: " +
           m_nTotal +
           "\n  registered on SMP ..........: " +
           m_nAddedToSMP +
           "\n  registered business cards ..: " +
           businessCardSuccessCount +
           "\n  deleted from SMP ...........: " +
           m_nDeletedFromSMP +
           "\n  SMP: registerUser failed ...: " +
           m_nRegisterUserFailed +
           "\n  SMP: addDocumentId failed ..: " +
           m_aAddDocumentIdFailed +
           "\n  SMP: business card failed ..: " +
           businessCardFailCount +
           "\n  SMP: delete failed .........: " +
           m_nDeleteFailed +
           "\n  SMP: total failed ..........: " +
           (m_nRegisterUserFailed + m_aAddDocumentIdFailed + businessCardFailCount + m_nDeleteFailed) +
           "\n";
  }
}
