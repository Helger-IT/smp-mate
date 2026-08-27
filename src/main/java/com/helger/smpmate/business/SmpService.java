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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.helger.smpmate.args.ESPOperation;
import com.helger.smpmate.args.SPArgAuthority;
import com.helger.smpmate.args.SPArgProxy;
import com.helger.smpmate.args.SPArgSMP;
import com.helger.smpmate.config.SPPaths;
import com.helger.smpmate.config.SPServiceMetadata;
import com.helger.smpmate.config.SPTask;
import com.helger.smpmate.log.MyLog;

/**
 * An SMP service client implementation
 *
 * @author Philip Helger
 */
public class SmpService
{
  private static final class Metadata
  {
    private final String m_sDocumentIdentifierScheme;
    private final String m_sDocumentIdentifier;
    private final String m_sProcessIdentifierScheme;
    private final String m_sProcessIdentifier;
    // May be null for deletion operations, where no template is needed
    private final String m_sXmlTemplateContent;

    private Metadata (final String sDocumentIdentifierScheme,
                      final String sDocumentIdentifier,
                      final String sProcessIdentifierScheme,
                      final String sProcessIdentifier,
                      final String sXmlTemplate)
    {
      m_sDocumentIdentifierScheme = sDocumentIdentifierScheme;
      m_sDocumentIdentifier = sDocumentIdentifier;
      m_sProcessIdentifierScheme = sProcessIdentifierScheme;
      m_sProcessIdentifier = sProcessIdentifier;
      m_sXmlTemplateContent = sXmlTemplate;
    }

    @Nonnull
    private String getDocumentTypeUrlPart ()
    {
      return m_sDocumentIdentifierScheme + SCHEME_SEPARATOR + m_sDocumentIdentifier;
    }

    @Nonnull
    private String getProcessUrlPart ()
    {
      return m_sProcessIdentifierScheme + SCHEME_SEPARATOR + m_sProcessIdentifier;
    }
  }

  private static final String AUTHORIZATION = "Authorization";
  private static final String BASIC = "Basic ";
  private static final String CONTENT_TYPE = "Content-Type";
  private static final String APPLICATION_XML = "application/xml";
  private static final String CHARSET = "charset";
  private static final String PARAM_PARTICIPANT_IDENTIFIER = "${ParticipantId}";
  private static final String PARAM_DOCUMENT_IDENTIFIER = "${DocumentIdentifier}";
  private static final String PARAM_PROCESS_IDENTIFIER = "${ProcessIdentifier}";
  private static final String ISO_6523_ACTORID_UPIS = "iso6523-actorid-upis::";
  private static final String SERVICES = "services";
  private static final String SCHEME_SEPARATOR = "::";

  private final String m_sServerUrl;
  private final String m_sAuthEncoded;
  private final SPArgProxy m_aProxy;
  private final String m_sServiceGroupTemplate;
  private final List <Metadata> m_aServiceMetadata;

  @Nonnull
  private static String _readFileUTF8 (@Nonnull final Path aPath) throws IOException
  {
    final byte [] aBytes = Files.readAllBytes (aPath);
    return new String (aBytes, StandardCharsets.UTF_8);
  }

  @Nonnull
  private static List <Metadata> _readAll (@Nonnull final Iterable <? extends SPServiceMetadata> aMetadatas) throws IOException
  {
    final List <Metadata> ret = new ArrayList <> ();
    for (final SPServiceMetadata aMD : aMetadatas)
    {
      ret.add (new Metadata (aMD.getDocumentIdentifierScheme (),
                             aMD.getDocumentIdentifier (),
                             aMD.getProcessIdentifierScheme (),
                             aMD.getProcessIdentifier (),
                             _readFileUTF8 (aMD.getTemplate ()).replace (PARAM_DOCUMENT_IDENTIFIER,
                                                                         aMD.getDocumentIdentifier ())
                                                               .replace (PARAM_PROCESS_IDENTIFIER,
                                                                         aMD.getProcessIdentifier ())));
    }
    return Collections.unmodifiableList (ret);
  }

  @Nonnull
  private static List <Metadata> _readIds (@Nonnull final Iterable <? extends SPServiceMetadata> aMetadatas)
  {
    // Deletion operations only need the document and process identifiers - no
    // XML template files need to be present on disk
    final List <Metadata> ret = new ArrayList <> ();
    for (final SPServiceMetadata aMD : aMetadatas)
      ret.add (new Metadata (aMD.getDocumentIdentifierScheme (),
                             aMD.getDocumentIdentifier (),
                             aMD.getProcessIdentifierScheme (),
                             aMD.getProcessIdentifier (),
                             null));
    return Collections.unmodifiableList (ret);
  }

  /**
   * Initializes a new instance.
   *
   * @param aTask
   *        The task configuration to be used. May not be <code>null</code>.
   * @throws IOException
   *         If one of the configured XML template files cannot be read
   */
  public SmpService (@Nonnull final SPTask aTask) throws IOException
  {
    m_aProxy = aTask.getProxy ();
    final SPArgSMP aSmp = aTask.getSmp ();
    m_sServerUrl = aSmp.getUrl ();
    m_sAuthEncoded = aSmp.getAuthority () == null ? null : _basicAuthEncoded (aSmp.getAuthority ());
    final SPPaths paths = aTask.getPaths ();
    if (aTask.getOperation () == ESPOperation.ADD)
    {
      // Only the "add" operation needs the XML templates on disk
      m_sServiceGroupTemplate = _readFileUTF8 (paths.getServiceGroupTemplate ());
      m_aServiceMetadata = _readAll (paths.getServiceMetadata ());
    }
    else
    {
      m_sServiceGroupTemplate = null;
      m_aServiceMetadata = _readIds (paths.getServiceMetadata ());
    }

    MyLog.info (() -> "Connecting to SMP on " + m_sServerUrl);
    if (m_sAuthEncoded == null)
      MyLog.warning (() -> "Connecting to SMP without authorization");
  }

  @Nonnull
  private static String _basicAuthEncoded (@Nonnull final SPArgAuthority aAuth)
  {
    final String sAuthString = aAuth.getName () + ":" + aAuth.getPassword ();
    final byte [] aAuthEncBytes = Base64.getEncoder ().encode (sAuthString.getBytes (StandardCharsets.UTF_8));
    return BASIC + new String (aAuthEncBytes, StandardCharsets.US_ASCII);
  }

  @Nonnull
  private HttpURLConnection _openConnection (@Nonnull final URL aUrl, @Nonnull final String sMethod) throws IOException
  {
    final HttpURLConnection aHttpCon = (HttpURLConnection) aUrl.openConnection ();
    if (null != m_sAuthEncoded)
      aHttpCon.setRequestProperty (AUTHORIZATION, m_sAuthEncoded);
    aHttpCon.setDoOutput (true);
    aHttpCon.setRequestMethod (sMethod);
    aHttpCon.setRequestProperty (CONTENT_TYPE, APPLICATION_XML);
    aHttpCon.setRequestProperty (CHARSET, StandardCharsets.UTF_8.name ());
    return aHttpCon;
  }

  private void _configureProxy ()
  {
    if (m_aProxy == null)
    {
      System.setProperty ("http.proxySet", "false");
    }
    else
    {
      System.setProperty ("http.proxySet", "true");
      System.setProperty ("http.proxyPort", m_aProxy.getPort ());
      System.setProperty ("http.proxyHost", m_aProxy.getHost ());
    }
  }

  @Nonnull
  private static String _urlEncoded (@Nonnull final String sValue)
  {
    return URLEncoder.encode (sValue, StandardCharsets.UTF_8);
  }

  @Nonnull
  private static URL _url (@Nonnull final String sHead, @Nonnull final String... aPath) throws MalformedURLException
  {
    final String sTail = Stream.of (aPath).map (SmpService::_urlEncoded).collect (Collectors.joining ("/"));
    final String sURL = sHead + (sHead.endsWith ("/") ? "" : "/") + sTail;
    try
    {
      return new URI (sURL).toURL ();
    }
    catch (final URISyntaxException | IllegalArgumentException ex)
    {
      final MalformedURLException ex2 = new MalformedURLException ("Invalid URL '" + sURL + "'");
      ex2.initCause (ex);
      throw ex2;
    }
  }

  /**
   * Registers a "user" ("participant") on the associated SMP server using a "ServiceGroup"
   * structure.
   *
   * @param sParticipantId
   *        Participant ID to handle
   * @return The API HTTP status code
   * @throws IOException
   *         in case of error
   */
  public final int registerParticipant (@Nonnull final String sParticipantId) throws IOException
  {
    _configureProxy ();
    final URL aUrl = _url (m_sServerUrl, ISO_6523_ACTORID_UPIS + sParticipantId);
    final String sBody = m_sServiceGroupTemplate.replace (PARAM_PARTICIPANT_IDENTIFIER, sParticipantId);
    MyLog.info (() -> "[SMP] Trying to register participant " + sParticipantId);

    final HttpURLConnection aHttpCon = _openConnection (aUrl, "PUT");
    try (final OutputStreamWriter aOS = new OutputStreamWriter (aHttpCon.getOutputStream (), StandardCharsets.UTF_8))
    {
      aOS.write (sBody);
    }
    return aHttpCon.getResponseCode ();
  }

  /**
   * Sets the Business Card of a registered "user" ("participant") on the associated SMP server.
   *
   * @param sParticipantID
   *        Participant ID to handle
   * @param content
   *        The XML representation of the Business Card to be set
   * @return The API HTTP status code
   * @throws IOException
   *         in case of error
   */
  public int putBusinessCard (@Nonnull final String sParticipantID, @Nonnull final byte [] content) throws IOException
  {
    _configureProxy ();

    MyLog.info (() -> "[SMP] Trying to set business card to participant " + sParticipantID);

    final URL url = _url (m_sServerUrl, "businesscard", ISO_6523_ACTORID_UPIS + sParticipantID);
    final HttpURLConnection connection = _openConnection (url, "PUT");

    try (final OutputStream aOS = connection.getOutputStream ())
    {
      aOS.write (content, 0, content.length);
    }

    return connection.getResponseCode ();
  }

  /**
   * Adds all "documentId"/"processId" to a registered "user" ("participant") on the associated SMP
   * server using a "ServiceMetadata" structure.
   *
   * @param sParticipantId
   *        Participant ID to handle
   * @return List of API HTTP status codes
   * @throws IOException
   *         in case of error
   */
  @Nonnull
  public final List <Integer> addDocumentTypeIDs (@Nonnull final String sParticipantId) throws IOException
  {
    // List of HTTP status codes
    final List <Integer> ret = new LinkedList <> ();
    for (final Metadata aMetadata : m_aServiceMetadata)
      ret.add (Integer.valueOf (_addDocumentTypeID (sParticipantId, aMetadata)));
    return ret;
  }

  private int _addDocumentTypeID (@Nonnull final String sParticipantID, @Nonnull final Metadata aMetadata)
                                                                                                           throws IOException
  {
    _configureProxy ();
    final URL aUrl = _url (m_sServerUrl,
                           ISO_6523_ACTORID_UPIS + sParticipantID,
                           SERVICES,
                           aMetadata.getDocumentTypeUrlPart ());

    final String sBody = aMetadata.m_sXmlTemplateContent.replace (PARAM_PARTICIPANT_IDENTIFIER, sParticipantID);
    MyLog.info (() -> "[SMP] Trying to add document type " +
                      aMetadata.m_sDocumentIdentifier +
                      " to participant " +
                      sParticipantID);

    final HttpURLConnection aHttpCon = _openConnection (aUrl, "PUT");
    try (final OutputStreamWriter aWriter = new OutputStreamWriter (aHttpCon.getOutputStream (),
                                                                    StandardCharsets.UTF_8))
    {
      aWriter.write (sBody);
    }
    return aHttpCon.getResponseCode ();
  }

  /**
   * Deletes a single process (incl. all its endpoints) of each configured document type from a
   * registered "user" ("participant"), without touching other processes of the same document type.
   * Uses the phoss SMP REST API
   * <code>DELETE /{ServiceGroupId}/services/{DocumentTypeId}/{ProcessId}</code> (see
   * https://github.com/phax/phoss-smp/discussions/491, since phoss SMP v8.1.8).
   * 
   * @param sParticipantId
   *        Participant ID to handle
   * @return List of API HTTP status codes
   * @throws IOException
   *         in case of error
   */
  @Nonnull
  public final List <Integer> deleteProcesses (@Nonnull final String sParticipantId) throws IOException
  {
    // List of HTTP status codes
    final List <Integer> ret = new LinkedList <> ();
    for (final Metadata aMetadata : m_aServiceMetadata)
      ret.add (Integer.valueOf (_deleteProcess (sParticipantId, aMetadata)));
    return ret;
  }

  private int _deleteProcess (@Nonnull final String sParticipantID, @Nonnull final Metadata aMetadata)
                                                                                                       throws IOException
  {
    _configureProxy ();
    final URL aUrl = _url (m_sServerUrl,
                           ISO_6523_ACTORID_UPIS + sParticipantID,
                           SERVICES,
                           aMetadata.getDocumentTypeUrlPart (),
                           aMetadata.getProcessUrlPart ());
    MyLog.info (() -> "[SMP] Trying to delete process " +
                      aMetadata.m_sProcessIdentifier +
                      " of document type " +
                      aMetadata.m_sDocumentIdentifier +
                      " from participant " +
                      sParticipantID);

    final HttpURLConnection aHttpCon = _openConnection (aUrl, "DELETE");
    return aHttpCon.getResponseCode ();
  }

  /**
   * Deletes the whole service metadata of each configured document type from a registered "user"
   * ("participant"). Uses the phoss SMP REST API
   * <code>DELETE /{ServiceGroupId}/services/{DocumentTypeId}</code>.
   * 
   * @param sParticipantId
   *        Participant ID to handle
   * @return List of API HTTP status codes
   * @throws IOException
   *         in case of error
   */
  @Nonnull
  public final List <Integer> deleteDocumentTypeIDs (@Nonnull final String sParticipantId) throws IOException
  {
    // List of HTTP status codes
    final List <Integer> ret = new LinkedList <> ();
    for (final Metadata aMetadata : m_aServiceMetadata)
      ret.add (Integer.valueOf (_deleteDocumentTypeID (sParticipantId, aMetadata)));
    return ret;
  }

  private int _deleteDocumentTypeID (@Nonnull final String sParticipantID, @Nonnull final Metadata aMetadata)
                                                                                                              throws IOException
  {
    _configureProxy ();
    final URL aUrl = _url (m_sServerUrl,
                           ISO_6523_ACTORID_UPIS + sParticipantID,
                           SERVICES,
                           aMetadata.getDocumentTypeUrlPart ());
    MyLog.info (() -> "[SMP] Trying to delete document type " +
                      aMetadata.m_sDocumentIdentifier +
                      " from participant " +
                      sParticipantID);

    final HttpURLConnection aHttpCon = _openConnection (aUrl, "DELETE");
    return aHttpCon.getResponseCode ();
  }

  /**
   * Removes a registered "user"/"participant" and all its metadata from the associated SMP server.
   *
   * @param sParticipantId
   *        Participant ID to handle
   * @return The API HTTP status code
   * @throws IOException
   *         in case of error
   */
  public final int deleteParticipant (@Nonnull final String sParticipantId) throws IOException
  {
    _configureProxy ();
    final URL aUrl = _url (m_sServerUrl, ISO_6523_ACTORID_UPIS + sParticipantId);
    MyLog.info (() -> "[SMP] Trying to delete participant " + sParticipantId);

    final HttpURLConnection aHttpCon = _openConnection (aUrl, "DELETE");
    return aHttpCon.getResponseCode ();
  }
}
