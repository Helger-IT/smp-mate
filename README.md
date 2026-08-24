# SMP MAintenance TEchnology

Note: sorry to all you guys out there not speaking German - this repository is only available in German. This repository basically provides a maintenance tool that allows to bulk create

Dieser Leitfaden beschreibt SMP Mate (ehemals SMP Provisioning Tool), mit dessen Hilfe zusätzliche Participants im Peppol-Netzwerk provisioniert werden können.
SMP Mate wird als Archiv ausgeliefert, dessen Bestandteile und Verwendung im Folgenden erklärt werden.

# Einleitung

Für den Nachrichtenaustausch im Peppol-Netzwerk müssen die Teilnehmer (im Peppol-Kontext "Participants") in der Komponente SMP und bei den Access Points bekannt 
sein.

* **SMP**: Die Komponente SMP (SMP steht für Service Metadata Publisher) ist der Peppol-eigene Directory Service. In diesem Nutzerverzeichnis, sind **alle** teilnehmenden Participants hinterlegt.
* **Access Points**: Die Access Points kennen jeweils nur eine Untermenge aus der Menge der Participants.
    * **Sender Access Points**: Diese Access Points kennen die teilnehmenden Participants Ihres jeweiligen Landes.
    * **Receiver Access Points**: Diese Access Points kennen die jeweiligen Participants, für die Peppol-Nachrichten, die vom Empfangsprozess abgeholt werden sollen. Beispielsweise holt der Governikus MultiMessenger, per AS4 AccessPoint mit XTA-Connector, Nachrichten von Gruppen von Participants ab, die dem GMM bekannt sind.

Aus den jeweiligen Absendern (Sender, C1), Empfängern (Receiver, C4) und Access Points (C2 und C3) ergibt sich das 4-Corner-Modell des Peppol-Netzwerks.

# Peppol Participants Provisioning

Damit weitere Participants am Nachrichtenaustausch über das PEEPOL-Netzwerk teilnehmen können, müssen diese in den Access-Points und im SMP provisioniert werden.
Alle in einem Access Point bekannten Participants sind auch im SMP bekannt, da die dort provisionierten Participants eine Untermenge der Participants im SMP sind.

## Allgemeine Beschreibung des Verfahrens

Beim Provisionieren mit dem SMP Provisioning Tool werden die folgenden Schritte durchgeführt.

* **Feststellen weiterer Participants**: Das SMP Provisioning Tool übergibt dem System eine CSV-Datei, z.B. `newParticipants.csv`, die neue Participants enthält, die dem Access Point und dem SMP hinzugefügt werden sollen.

Das Provisionieren neuer Participants kann an den Access Points 2 (C2) und 3 (C3) des 4-Corner Modells erfolgen und aktualisiert dabei immer auch den SMP.

## Parameter von SMP Mate

Für die Provisionierung steht eine Kommandozeilen-Applikation in Form einer ausführbaren JAR-Datei zur Verfügung.
Dieser Applikation wird eine Konfigurationsdatei im JSON-Format als Parameter übergeben.
In der Konfigurationsdatei werden unter anderem weitere Dateien referenziert, die für die Durchführung der Provisionierung erforderlich sind.

### Konfigurationsdaten in der `sampleTask.json`-Datei

Ein Beispiel für die erforderlichen Konfigurationsdaten befindet sich in der Datei `sampleTask.json`, die auf oberster Ebene des ZIP-Archivs zu finden ist.

**Achtung**: Bitte beachten Sie, dass in JSON-Dateien normalerweise keine Kommentare enthalten sein dürfen.
Bitte löschen Sie die Kommentare aus dem JSON-Template, nachdem Sie die Parameter angepasst haben und bevor Sie das SMP Provisioning Tool ausführen.

In der Datei `sampleTask.json` werden die Pfade zu weiteren Eingabe- sowie auch zu Ausgabedateien, die SMP-Konfiguration und eine möglicherweise
benötigte Proxy-Konfiguration definiert.

### Der Abschnitt "Pfade" in der JSON-Datei

Pfade zu Input-Dateien definiert, die zur Ausführungszeit vorhanden sein müssen. Die folgenden Einträge müssen angepasst werden:

* **`csvInput`**: Der Name der Variable `csvInput` darf nicht geändert werden und definiert Pfad und Dateiname der Datei, in der die neuen Participants enthalten sind, beispielsweise `newParticipants.csv`.
    * **`newParticipants.csv`**: Diese Datei enthält alle neue Participants. Die Participants werden in der Datei als Liste der Participant-IDs notiert. Hinweis: die Verwendung von CSV ist für die Vorwärtskompatibilität.
        * `ParticipantId`: Diese ID ist im gesamten Peppol-Netzwerk eindeutig.
        * `BusinessCardPath` (seit v1.0.1): Pfad zur BusinessCard-XML-Datei für diesen Participant. Das passende XML Schema befindet sich unter https://docs.peppol.eu/edelivery/directory/peppol-directory-business-card-20180621.xsd
* **`csvFailOutput`**: Der Name der Variable `csvFailOutput` darf nicht geändert werden und definiert Pfad und Dateiname einer Datei, die geschrieben wird, wenn einzelne Participants nicht erfolgreich an den SMP-Server übermittelt werden können, beispielsweise `failedParticipants.csv`.
* **`serviceGroupTemplate`**: Der Name der Variable `serviceGroupTemplate` darf nicht geändert werden und definiert Pfad und Dateiname einer Datei die die Vorlage für eine SMP "ServiceGroup" enthält. Der Default-Wert sollte nicht angepasst werden.
* **`serviceMetadata`**: Der Name der Variable `serviceMetadata` darf nicht geändert werden und definiert Pfade und Dateinamen als Vorlage einer oder mehrerer XML-Strukturen sowie der Identifikatoren für Dokumenttyp und Prozess, so, wie sie an den SMP-Server übermittelt werden sollen, beispielsweise `[ { "template": "ServiceMetadata.xml", "documentIdentifier": "urn:oasis:names:specification:ubl:schema:xsd:Tender-2::Tender##urn:www.cenbii.eu:transaction:biitrdm090:ver3.0::2.1", "processIdentifier": "urn:www.cenbii.eu:profile:bii54:ver3.0" } ]`
    * **`template`**: Der Pfad und Dateiname zur verwendeten XML-Vorlage
    * **`documentIdentifier`**: Der zu verwendende Peppol Dokumenttypidentifikator (ohne Schema-Präfix). Das für die SMP-REST-URL verwendete Schema wird über `documentIdentifierScheme` bestimmt.
    * **`documentIdentifierScheme`** (seit v1.0.2, optional): Das für die SMP-REST-URL verwendete Schema des Dokumenttypidentifikators. Standardwert ist `busdox-docid-qns`; nur anzupassen, wenn ein abweichendes Schema benötigt wird. Ohne Angabe bleibt das bisherige Verhalten unverändert.
    * **`processIdentifier`**: Der zu verwendende Peppol Prozessidentifikator (ohne Schema-Präfix). Das für die SMP-REST-URL verwendete Schema wird über `processIdentifierScheme` bestimmt.
    * **`processIdentifierScheme`** (seit v1.0.2, optional): Das für die SMP-REST-URL verwendete Schema des Prozessidentifikators. Standardwert ist `cenbii-procid-ubl`; nur anzupassen, wenn ein abweichendes Schema benötigt wird. Ohne Angabe bleibt das bisherige Verhalten unverändert.

### Der Abschnitt "SMP-Konfiguration" in der JSON-Datei

Diese Konfiguration enthält die URL der SMP-REST-Schnittstelle, und Authentifizierungsdaten, wenn diese gefordert sind.
Diese Parameter müssen aus dem Template gelöscht werden, wenn keine Authentifizierungsdaten erforderlich sind.

### Der Abschnitt "Proxy-Konfiguration" in der JSON-Datei

Diese Konfigurationsdaten sind erforderlich, wenn der SMP-Server hinter einem Proxy betrieben wird.
Diese Daten müssen aus dem Template entfernt werden, wenn kein Proxy konfiguriert werden muss.

### Der Abschnitt "Options" in der JSON-Datei

Zurzeit steht genau eine Option zur Verfügung.
Die Option `DRY_RUN` sorgt dafür, dass ein Programdurchlauf ausgeführt werden kann, bei dem keine Daten an den SMP-Server übermittelt werden.
Damit kann die Ausführbarkeit des Programms mit seinen aktuellen Parametern getestet werden.
Wenn Ein- und Ausgabedatei mit dem gleichen Pfad/Dateinamen definiert sind, wird diese Datei auch im Modus `DRY_RUN` überschrieben.

### Der Abschnitt "Operation" in der JSON-Datei (seit v1.0.2)

Das optionale Feld `operation` legt fest, welche Aktion je Participant ausgeführt wird. Wird das Feld weggelassen, gilt `ADD` und damit das bisherige Verhalten.

* **`ADD`** (Standard): Legt Participants inkl. deren Dokumenttypen und Business Cards an bzw. aktualisiert sie.
* **`DELETE_PROCESS`**: Löscht je Participant den/die in `serviceMetadata` konfigurierten Prozess(e) des jeweiligen Dokumenttyps (phoss SMP REST-API `DELETE /{sg}/services/{doctype}/{process}`, benötigt phoss SMP v8.1.8 oder neuer).
* **`DELETE_DOCTYPE`**: Löscht je Participant die gesamten Service-Metadaten des/der konfigurierten Dokumenttyps/-typen (phoss SMP REST-API `DELETE /{sg}/services/{doctype}`).
* **`DELETE_PARTICIPANT`**: Löscht je Participant den kompletten Participant inkl. aller Dokumenttypen (phoss SMP REST-API `DELETE /{sg}`).

Hinweis: Bei den DELETE-Operationen werden die XML-Vorlagen (`template`) nicht benötigt; verwendet werden nur die Identifikatoren und Schemata aus dem Abschnitt `serviceMetadata`.

# Ausführen von SMP Mate

Die JAR-Datei `smp-mate-<Versionsnummer>-app.jar` kann in jeder beliebigen Umgebung mit dem nachfolgend beschriebenen Aufruf ausgeführt werden.
Dabei muss sichergestellt werden, dass die Pfade und Dateien erreichbar sind, ebenso wie der SMP-Server.

Es muss mindestens Java 1.8 JRE installiert sein. 

## Aufruf der ausführbaren JAR-Datei

```shell
java – jar smp-mate-<Versionsnummer>-app.jar sampleTask.json
```

Falls SMP Mate ohne JSON-Datei aufgerufen wird, dann wird eine Hilfe angezeigt.

Falls SMP Mate mit dem Namen einer JSON-Datei aufgerufen wird, die nicht existiert, dann wird vom SMP Mate eine Beispiel-JSON-Datei erstellt die dann bearbeitet werden kann.

# Technisches

## Building

Voraussetzungen: Java 1.8 oder höher, Apache Maven 3.x

Befehl: `mvn clean install`

Das Ergebnis ist die Datei `target/smp-mate-x.y.z-SNAPSHOT-app.jar` wobei `x.y.z` für die Versionsnummer steht.

# News and noteworthy

v1.0.3 - work in progress
* Bei `documentIdentifier`, `documentIdentifierScheme`, `processIdentifier` und `processIdentifierScheme` werden führende und abschließende Leerzeichen jetzt automatisch entfernt.
* Bei den Participant-IDs aus der CSV-Datei werden führende und abschließende Leerzeichen jetzt automatisch entfernt.

v1.0.2 - 2026-08-03
* Das optionale Feld `operation` wurde zur Task-Datei hinzugefügt, um das Löschen zu unterstützen.
  Mögliche Werte sind `ADD` (Standard, bisheriges Verhalten), `DELETE_PROCESS`, `DELETE_DOCTYPE` und `DELETE_PARTICIPANT`. `DELETE_PROCESS` verwendet die phoss SMP REST-API `DELETE /{sg}/services/{doctype}/{process}` (siehe phoss SMP #491, benötigt phoss SMP v8.1.8 oder neuer).
  Die Lösch-Operationen benötigen die XML-Template-Dateien nicht - es werden nur `documentIdentifier` und `processIdentifier` verwendet.
  Siehe [#7](https://github.com/Helger-IT/smp-mate/issues/7).
* Die Identifier-Schemes sind jetzt je `serviceMetadata`-Eintrag über die optionalen Felder `documentIdentifierScheme` (Standard `busdox-docid-qns`) und `processIdentifierScheme` (Standard `cenbii-procid-ubl`) konfigurierbar.
  Ohne Angabe gelten die bisherigen Standardwerte, sodass bestehende Konfigurationen unverändert weiterlaufen.
  Siehe [#8](https://github.com/Helger-IT/smp-mate/issues/8).

v1.0.1 - 2024-02-15
* Unterstützung für Business Cards je Participant wurde hinzugefügt. Über das optionale Feld `BusinessCardPath` in der CSV-Datei kann je Participant eine BusinessCard-XML-Datei referenziert werden.
  Das passende XML Schema befindet sich unter https://docs.peppol.eu/edelivery/directory/peppol-directory-business-card-20180621.xsd

v1.0.0 - 2022-10-12
* Erste Veröffentlichung von SMP Mate (ehemals SMP Provisioning Tool).
