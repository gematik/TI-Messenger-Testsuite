# language: de
@File:FeatureFile_08X_02_V2_Basis @Ctl:UseCaseV2_08X_Basis @PRODUKT:TI-M_Client_Basis @Zul:Pro @Zul:ProKK @AFO-ID:A_25479 @AFO-ID:A_25428 @NB:JA
Funktionalität: 08X. (2) Einträge im VZD-FHIR-Directory suchen (Basis Spec)
  Der Anwendungsfall beschreibt, wie ein Akteur im VZD-FHIR-Directory nach HealthcareService- undPractitionerRole-
  Ressourcen sucht. Dies setzt eine erfolgreiche Anmeldung des Akteurs an einem Messenger-Service voraus.

  COMMENT: Basis
  A_25479     Search Token
  A_25428     VZD-FHIR-Directory Inhalte

  Inhalt
  TF 1     User sucht HBA-User im VZD
  TF 2     User sucht HBA-User teilqualifiziert im VZD

  @Ctl:AllowAll @Ctl:VZD @TCID:TIM_V2_BASIS_AF_08X0201 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 08X.02.01 Einträge im VZD-FHIR-Directory suchen - AllowAll - Org-User sucht anderen HBA-User im VZD
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT       | <ApiName1> |
      | B | PRO_PRACTITIONER | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "B" hinterlegt seine MXID im Verzeichnis Dienst
    Und "A" findet "B" in FHIR

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("practitioner"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:VZD @TCID:TIM_V2_BASIS_AF_08X0202 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 08X.02.02 Einträge im VZD-FHIR-Directory suchen - AllowAll - Org-User sucht anderen HBA-User teilqualifiziert im VZD
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT       | <ApiName1> |
      | B | PRO_PRACTITIONER | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "B" hinterlegt seine MXID im Verzeichnis Dienst
    Und "A" findet TI-Messenger-Nutzer "B" bei Suche nach Namen minus 0-1 (Anzahl vorne-hinten) Char(s) abgeschnitten

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("practitioner"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
