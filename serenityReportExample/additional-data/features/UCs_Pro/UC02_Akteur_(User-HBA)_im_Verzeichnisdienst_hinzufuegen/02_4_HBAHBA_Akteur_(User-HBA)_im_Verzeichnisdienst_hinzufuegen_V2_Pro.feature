# language: de
@File:FeatureFile_02_04_V2_Pro @Ctl:UseCaseV2_02_Pro @PRODUKT:TI-M_Client_Pro @PRODUKT:VZD_FHIR @Zul:Pro @AF-ID:AF_10058-02 @NB:JA
Funktionalität: 5.1.2 (4) Akteur (User-HBA) im Verzeichnisdienst hinzufügen (Pro Spec)
  Mit diesem Anwendungsfall wird ein Akteur in der Rolle "User-HBA" für Akteure anderer Messenger-Services auffindbar
  und erreichbar gemacht. Dafür werden FHIR-Ressourcen mit ihrer jeweiligen MXID des Akteurs im Personenverzeichnis
  (PractitionerRole) des VZD-FHIR-Directory hinterlegt.

  COMMENT: Pro
  AF_10058-02 - Akteur (User-HBA) im Verzeichnisdienst hinzufügen

  Inhalt
  TF 1 - 2 User im VZD hinzufügen & löschen (Suche innerhalb & ausserhalb der Organisation)

  @Ctl:AllowAll @Ctl:VZD @TCID:TIM_V2_PRO_AF_020401 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 02.04.01 Akteur im Verzeichnisdienst - Hinzufügen/Löschen - HBA User sucht HBA User gleicher Organisation
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_PRACTITIONER | <ApiName1A> |
      | B | PRO_PRACTITIONER | <ApiName1B> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A", "B" hinterlegen ihre MXIDs im Verzeichnis Dienst
    Und "B" findet "A" in FHIR
    Und "A" findet "B" in FHIR
    Und "A" findet "A" in FHIR
    Und "B" findet "B" in FHIR
    Wenn "A" löscht seine MXID im Verzeichnis Dienst
    Dann "B" kann "A" nicht mehr finden in FHIR [Retry 6 - 1]
    Wenn "B" löscht seine MXID im Verzeichnis Dienst
    Dann "A" kann "B" nicht mehr finden in FHIR [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient")) @Plugin:Filter(ApiName1A.hasTag("practitioner")) @Plugin:Filter(ApiName1B.hasTag("practitioner"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:VZD @TCID:TIM_V2_PRO_AF_020402 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 02.04.02 Akteur im Verzeichnisdienst - Hinzufügen/Löschen - HBA User sucht HBA User anderer Organisation
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_PRACTITIONER | <ApiName1> |
      | B | PRO_PRACTITIONER | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A", "B" hinterlegen ihre MXIDs im Verzeichnis Dienst
    Und "B" findet "A" in FHIR
    Und "A" findet "B" in FHIR
    Wenn "A" löscht seine MXID im Verzeichnis Dienst
    Dann "B" kann "A" nicht mehr finden in FHIR [Retry 6 - 1]
    Wenn "B" löscht seine MXID im Verzeichnis Dienst
    Dann "A" kann "B" nicht mehr finden in FHIR [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient")) @Plugin:Filter(ApiName1.hasTag("practitioner")) @Plugin:Filter(ApiName2.hasTag("practitioner"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
