# language: de
@File:FeatureFile_06_03_V2_Basis @Ctl:UseCaseV2_06_Basis @PRODUKT:TI-M_Client_Basis @PRODUKT:TI-M_FD_Basis @PRODUKT:VZD_FHIR @Zul:Pro @AF-ID:AF_10062-03 @NB:JA
Funktionalität: 5.1.6 (3) Austausch von Events zwischen Akteuren außerhalb einer Organisation (Basis Spec)
  In diesem Anwendungsfall können Akteure, welche sich in einem gemeinsamen Raum befinden, Nachrichten austauschen und
  weitere in der Matrix-Spezifikation festgelegte Aktionen ausführen. Dieser Anwendungsfall setzt die erfolgreiche
  Annahme eines Invite-Events durch einen oder mehrere beteiligte Akteure voraus. Die Prüfung auf Domainzugehörigkeit
  findet bei jedem Event der Server-Server Kommunikation statt. In diesem Anwendungsfall sind die beteiligten Akteure
  in einem gemeinsamen Chatraum und auf unterschiedlichen Messenger-Services verteilt.

  COMMENT: Basis
  AF_10062-03 Austausch von Events zwischen Akteuren außerhalb einer Organisation

  Inhalt
  TF 1 - 2 User (AllowAll) sendet Nachricht an HBA-User (Chat/Raum) - Positiv

  @Ctl:AllowAll @Ctl:VZD @TCID:TIM_V2_BASIS_AF_060301 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 06.03.01 Austausch von Events außerhalb einer Organisation - Chat - AllowAll - HBA-User sendet Nachricht an User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_PRACTITIONER | <ApiName1> |
      | B | PRO_CLIENT       | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt seine MXID im Verzeichnis Dienst
    Und "B" findet "A" in FHIR
    Wenn "B" schreibt "A" direkt "Testnachricht 1"
    Und "A" erhält eine Einladung von "B"
    Und "A" bestätigt eine Einladung von "B"
    Dann "A" empfängt eine Nachricht "Testnachricht 1" von "B"
    Wenn "A" schreibt "B" direkt "Testnachricht 2"
    Dann "B" empfängt eine Nachricht "Testnachricht 2" von "A"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient")) @Plugin:Filter(ApiName1.hasTag("practitioner"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:VZD @TCID:TIM_V2_BASIS_AF_060302 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 06.03.02 Austausch von Events außerhalb einer Organisation - Raum - AllowAll - HBA-User sendet Nachricht an User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_PRACTITIONER | <ApiName1> |
      | B | PRO_CLIENT       | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt seine MXID im Verzeichnis Dienst
    Und "B" findet "A" in FHIR
    Und "B" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "B" lädt "A" in Chat-Raum "TIM Testraum 1" ein
    Und "A" erhält eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "A" bestätigt eine Einladung in Raum "TIM Testraum 1" von "B"
    Wenn "B" sendet die Nachricht "Testnachricht 1" an den Raum "TIM Testraum 1"
    Dann "A" empfängt eine Nachricht "Testnachricht 1" von "B" im Raum "TIM Testraum 1"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient")) @Plugin:Filter(ApiName1.hasTag("practitioner"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
