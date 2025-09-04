# language: de
@File:FeatureFile_08X_04_V2_Basis @Ctl:UseCaseV2_08X_Basis @PRODUKT:TI-M_Client_Basis @Zul:Pro @AFO-ID:A_25479 @AFO-ID:A_25428 @NB:JA
Funktionalität: 08X. (4) Einträge im VZD-FHIR-Directory suchen (Basis Spec)
  Der Anwendungsfall beschreibt, wie ein Akteur im VZD-FHIR-Directory nach HealthcareService- undPractitionerRole-
  Ressourcen sucht. Dies setzt eine erfolgreiche Anmeldung des Akteurs an einem Messenger-Service voraus.

  COMMENT: Basis
  A_25479     Search Token
  A_25428     VZD-FHIR-Directory Inhalte

  Inhalt
  TF 1     HBA-User sucht HBA-User im VZD
  TF 2     HBA-User sucht HBA-User teilqualifiziert im VZD

  @Ctl:AllowAll @Ctl:VZD @TCID:TIM_V2_BASIS_AF_08X0401 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 08X.04.01 Einträge im VZD-FHIR-Directory suchen - AllowAll - HBA-User sucht anderen HBA-User im VZD
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_PRACTITIONER | <ApiName1> |
      | B | PRO_PRACTITIONER | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A", "B" hinterlegen ihre MXIDs im Verzeichnis Dienst
    Und "A" findet "B" in FHIR

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient")) @Plugin:Filter(ApiName1.hasTag("practitioner")) @Plugin:Filter(ApiName2.hasTag("practitioner"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:VZD @TCID:TIM_V2_BASIS_AF_08X0402 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 08X.04.02 Einträge im VZD-FHIR-Directory suchen - AllowAll - HBA-User sucht anderen HBA-User teilqualifiziert im VZD
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_PRACTITIONER | <ApiName1> |
      | B | PRO_PRACTITIONER | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A", "B" hinterlegen ihre MXIDs im Verzeichnis Dienst
    Und "A" findet TI-Messenger-Nutzer "B" bei Suche nach Namen minus 0-1 (Anzahl vorne-hinten) Char(s) abgeschnitten

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient")) @Plugin:Filter(ApiName1.hasTag("practitioner")) @Plugin:Filter(ApiName2.hasTag("practitioner"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
