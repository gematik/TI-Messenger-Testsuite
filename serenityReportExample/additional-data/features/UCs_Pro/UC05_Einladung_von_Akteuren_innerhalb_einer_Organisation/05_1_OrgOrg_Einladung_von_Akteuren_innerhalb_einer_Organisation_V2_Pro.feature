# language: de
@File:FeatureFile_05_01_V2_Pro @Ctl:UseCaseV2_05_Pro @Ctl:OneHomeServer @PRODUKT:TI-M_Client_Pro @PRODUKT:TI-M_FD_Pro @PRODUKT:TI-M_Client_Basis @PRODUKT:TI-M_FD_Basis @Zul:Pro @Zul:ProKK @AF-ID:AF_10104-03 @AFO-ID:A_25045-01 @AFO-ID:A_25046
Funktionalität: 5.1.5 (1) Einladung von Akteuren innerhalb einer Organisation (Pro Spec)
  In diesem Anwendungsfall wird ein Akteur, der zu einer gemeinsamen Organisation gehört, in einen Raum eingeladen,
  um Aktionen auszuführen. Für die Suche nach Akteuren innerhalb einer gemeinsamen Organisation durchsucht ein TI-M
  Client das Nutzerverzeichnis seiner Organisation auf dem Matrix-Homeserver. Anschließend wird die Einladung vom
  Einladenden an den Messenger-Proxy übermittelt. Dieser prüft, ob die beteiligten Akteure bei ihm registriert sind.
  Ist dies der Fall, erfolgt die Weiterleitung an den Matrix-Homeserver der Akteure. Ist dies nicht der Fall, handelt
  es sich bei dem einzuladenden Akteur nicht um einen Akteur innerhalb der Organisation und die Einladung wird für die
  externe Zustellung weitergeleitet.

  COMMENT: Pro
  AF_10104-03 Einladung von Akteuren innerhalb einer Organisation
  COMMENT: Basis
  A_25045-01  Funktionsumfang der Berechtigungskonfiguration
  A_25046     Durchsetzung der Berechtigungskonfiguration - Client

  Inhalt

  TF A  - B  Suche auf dem HomeServer (User & HSC)
  TF C  - D  User blockt/entblockt & allow/unallow Gruppe 
  TF 1  - 2  User (AllowAll) wird in Chat/Raum eingeladen
  TF 3  - 4  User (BlockAll) wird in Chat/Raum eingeladen - Negativ
  TF 5       User AllowAll (blockt/entblockt User) (Chat)
  TF 6       User AllowAll (2 User blockt/entblockt)
  TF 7       User AllowAll wird von geblocktem und ungeblocktem User eingeladen
  TF 8       User BlockAll (allow/unallow User) (Chat)
  TF 9       User BlockAll (2 User allow/unallow)
  TF 10      User BlockAll wird von allow und unallow User eingeladen
  TF 11      User (versucht) lädt anderen User ein im MixedMode
  TF 12 - 13 User AllowAll blockt/entblockt Server-Namen (Chat/Raum)
  TF 14 - 15 User BlockAll allow/unallow Server-Namen (Chat/Raum)
  TF 16 - 17 User lehnt Einladung ab (AllowAll) (Chat/Raum)
  TF 18 - 19 User lehnt Einladung ab (BlockAll) (Chat/Raum)
  TF 20      Dritter Nutzer wird in Chat eingeladen werden (AllowAll)
  TF 21      Dritter Nutzer wird in Chat eingeladen werden (BlockAll) - Negativ
  TF 22 - 25 Eintragung/Löschen in Liste von anderem Berechtigungsmodus (AllowAll) - Negativ
  TF 26 - 29 Eintragung/Löschen in Liste von anderem Berechtigungsmodus (BlockAll) - Negativ
  TF 30      Eintrag doppelter MXID Einträge in BlockListe (AllowAll)
  TF 31      Eintrag doppelter MXID Einträge in AllowListe (BlockAll)
  TF 32      Einträge bleiben trotz Logout erhalten (AllowAll)
  TF 33      Einträge bleiben trotz Logout erhalten (BlockAll)

  @TCID:TIM_V2_PRO_AF_050100A @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.00A Einladung innerhalb einer Organisation - User sucht anderen User im HomeServer
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @OrgAdmin @TCID:TIM_V2_PRO_AF_050100B @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.00B Einladung innerhalb einer Organisation - User sucht HealthCareService im HomeServer
    Angenommen Es werden folgende Clients reserviert:
      | A | ORG_ADMIN  | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
      | C | PRO_CLIENT | <ApiName1C> |
    Wenn "A" erstellt einen Healthcare-Service "HealthcareServiceName1" und setzen einen Endpunkt auf "B"
    Dann "C" findet "B" im Healthcare-Service "HealthcareServiceName1"

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("orgAdmin")) @Plugin:Filter(ApiName1B.hasTag("proClient")) @Plugin:Filter(ApiName1C.hasTag("proClient"))
    Beispiele:
      | ApiName1A           | ApiName1B             | ApiName1C             |
      | RU2-Ref-ORG-Web-HS3 | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:GruppenBP @Ctl:BP @Ctl:AllowAll @TCID:TIM_V2_PRO_AF_050100C @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 05.01.00C Einladung innerhalb einer Organisation - GruppenBlock - User AllowAll blockt/entblockt Gruppe
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt die Gruppe "Versicherte" in der Blockliste
    Dann erhält "A" einen Responsecode 200
    Und "A" entfernt die Gruppe "Versicherte" aus der Blockliste
    Dann erhält "A" einen Responsecode 204

    @Plugin:Filter(ApiName1.hasTag("proClient"))
    Beispiele:
      | ApiName1              |
      | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:GruppenBP @Ctl:BP @Ctl:BlockAll @TCID:TIM_V2_PRO_AF_050100D @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 05.01.00D Einladung innerhalb einer Organisation - GruppenBlock - User BlockAll allow/unallow Gruppe
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt die Gruppe "Versicherte" in der Allowliste
    Dann erhält "A" einen Responsecode 200
    Und "A" entfernt die Gruppe "Versicherte" aus der Allowliste
    Dann erhält "A" einen Responsecode 204

    @Plugin:Filter(ApiName1.hasTag("proClient"))
    Beispiele:
      | ApiName1              |
      | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050101 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.01 Einladung innerhalb einer Organisation - Chat - AllowAll - User lädt anderen User ein
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "B" setzt den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" schreibt "B" direkt "Testnachricht 1"
    Dann "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Und "B" empfängt eine Nachricht "Testnachricht 1" von "A"

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050102 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.02 Einladung innerhalb einer Organisation - Raum - AllowAll - User lädt anderen User ein
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "B" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Dann "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:BlockAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050103 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.03 Einladung innerhalb einer Organisation - Chat - BlockAll - User lädt anderen User ein
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "B" setzt den eigenen Authorization Mode auf "BlockAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" versucht "B" direkt "Testnachricht 1" zu schreiben
    Dann erhält "A" einen Responsecode 403
    Und "B" erhält KEINE Einladung von "A" [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:BlockAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050104 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.04 Einladung innerhalb einer Organisation - Raum - BlockAll - User lädt anderen User ein
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "B" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" versucht "B" in Chat-Raum "TIM Testraum 1" einzuladen
    Dann erhält "A" einen Responsecode 403
    Und "B" erhält KEINE Einladung von "A" für den Raum "TIM Testraum 1" [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050105 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.05 Einladung innerhalb einer Organisation - Chat - AllowAll - User AllowAll (blockt/entblockt User)
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "B" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt die User "B" in der Blockliste
    Wenn "B" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "B" versucht "A" direkt "Testnachricht 1" zu schreiben
    Dann erhält "B" einen Responsecode 403
    Und "A" erhält KEINE Einladung von "B" [Retry 6 - 1]
    Und "A" entfernt die User "B" in der Blockliste
    Und "B" schreibt "A" direkt "Testnachricht 2"
    Dann "A" erhält eine Einladung von "B"
    Und "A" bestätigt eine Einladung von "B"
    Und "A" empfängt eine Nachricht "Testnachricht 2" von "B"

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050106 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.06 Einladung innerhalb einer Organisation - Raum - AllowAll - User AllowAll (2 User blockt/entblockt)
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
      | C | PRO_CLIENT | <ApiName1C> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "B" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "C" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt die User "B" in der Blockliste
    Und "A" hinterlegt die User "C" in der Blockliste
    Und "B" erstellt einen Chat-Raum "TIM Testraum 1"
    Wenn "B" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "B" versucht "A" in Chat-Raum "TIM Testraum 1" einzuladen
    Dann erhält "B" einen Responsecode 403
    Und "A" erhält KEINE Einladung von "B" für den Raum "TIM Testraum 1" [Retry 6 - 1]
    Und "C" erstellt einen Chat-Raum "TIM Testraum 2"
    Wenn "C" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "C" versucht "A" in Chat-Raum "TIM Testraum 2" einzuladen
    Dann erhält "C" einen Responsecode 403
    Und "A" erhält KEINE Einladung von "C" für den Raum "TIM Testraum 2" [Retry 6 - 1]
    Und "A" entfernt die User "B" in der Blockliste
    Und "A" entfernt die User "C" in der Blockliste
    Und "B" lädt "A" in Chat-Raum "TIM Testraum 1" ein
    Dann "A" erhält eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "A" bestätigt eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "A" ist dem Raum "TIM Testraum 1" beigetreten
    Und "C" lädt "A" in Chat-Raum "TIM Testraum 2" ein
    Dann "A" erhält eine Einladung in Raum "TIM Testraum 2" von "C"
    Und "A" bestätigt eine Einladung in Raum "TIM Testraum 2" von "C"
    Und "A" ist dem Raum "TIM Testraum 2" beigetreten

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient")) @Plugin:Filter(ApiName1C.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             | ApiName1C             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050107 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.07 Einladung innerhalb einer Organisation - Raum - AllowAll - User AllowAll wird von geblocktem und ungeblocktem User eingeladen
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
      | C | PRO_CLIENT | <ApiName1C> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "B" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "C" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt die User "B" in der Blockliste
    Und "B" erstellt einen Chat-Raum "TIM Testraum 1"
    Wenn "B" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Wenn "B" findet TI-Messenger-Nutzer "C" bei der Suche im HomeServer
    Und "B" lädt "C" in Chat-Raum "TIM Testraum 1" ein
    Dann "C" erhält eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "C" bestätigt eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "C" ist dem Raum "TIM Testraum 1" beigetreten
    Und "B" versucht "A" in Chat-Raum "TIM Testraum 1" einzuladen
    Dann erhält "B" einen Responsecode 403
    Und "A" erhält KEINE Einladung von "B" für den Raum "TIM Testraum 1" [Retry 6 - 1]
    Und "C" lädt "A" in Chat-Raum "TIM Testraum 1" ein
    Dann "A" erhält eine Einladung in Raum "TIM Testraum 1" von "C"
    Und "A" bestätigt eine Einladung in Raum "TIM Testraum 1" von "C"
    Und "A" ist dem Raum "TIM Testraum 1" beigetreten

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient")) @Plugin:Filter(ApiName1C.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             | ApiName1C             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:BlockAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050108 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.08 Einladung innerhalb einer Organisation - Chat - BlockAll - User BlockAll (allow/unallow User)
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "B" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt die User "B" in der Allowliste
    Wenn "B" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "B" schreibt "A" direkt "Testnachricht 1"
    Dann "A" erhält eine Einladung von "B"
    Und "A" bestätigt eine Einladung von "B"
    Und "A" empfängt eine Nachricht "Testnachricht 1" von "B"
    Wenn "A" verlässt Chat mit "B"
    Und "A" entfernt die User "B" in der Allowliste
    Und "B" versucht "A" direkt "Testnachricht 2" zu schreiben
    Dann erhält "B" einen Responsecode 403
    Und "A" erhält KEINE Einladung von "B" [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:BlockAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050109 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.09 Einladung innerhalb einer Organisation - Raum - BlockAll - User BlockAll (2 User allow/unallow)
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
      | C | PRO_CLIENT | <ApiName1C> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "B" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "C" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt die User "B" in der Allowliste
    Und "A" hinterlegt die User "C" in der Allowliste
    Wenn "B" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "B" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "B" lädt "A" in Chat-Raum "TIM Testraum 1" ein
    Dann "A" erhält eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "A" bestätigt eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "A" ist dem Raum "TIM Testraum 1" beigetreten
    Und "A" verlässt Raum "TIM Testraum 1"
    Wenn "C" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "C" erstellt einen Chat-Raum "TIM Testraum 2"
    Und "C" lädt "A" in Chat-Raum "TIM Testraum 2" ein
    Dann "A" erhält eine Einladung in Raum "TIM Testraum 2" von "C"
    Und "A" bestätigt eine Einladung in Raum "TIM Testraum 2" von "C"
    Und "A" ist dem Raum "TIM Testraum 2" beigetreten
    Und "A" verlässt Raum "TIM Testraum 2"
    Und "A" entfernt die User "B" in der Allowliste
    Und "A" entfernt die User "C" in der Allowliste
    Und "B" versucht "A" in Chat-Raum "TIM Testraum 1" einzuladen
    Dann erhält "B" einen Responsecode 403
    Und "A" erhält KEINE Einladung von "B" für den Raum "TIM Testraum 1" [Retry 6 - 1]
    Und "C" versucht "A" in Chat-Raum "TIM Testraum 2" einzuladen
    Dann erhält "C" einen Responsecode 403
    Und "A" erhält KEINE Einladung von "C" für den Raum "TIM Testraum 2" [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient")) @Plugin:Filter(ApiName1C.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             | ApiName1C             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:BlockAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050110 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.10 Einladung innerhalb einer Organisation - Raum - BlockAll - User BlockAll wird von allow und unallow User eingeladen
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
      | C | PRO_CLIENT | <ApiName1C> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "B" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "C" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt die User "B" in der Allowliste
    Und "B" erstellt einen Chat-Raum "TIM Testraum 1"
    Wenn "B" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Wenn "B" findet TI-Messenger-Nutzer "C" bei der Suche im HomeServer
    Und "B" lädt "C" in Chat-Raum "TIM Testraum 1" ein
    Dann "C" erhält eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "C" bestätigt eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "C" ist dem Raum "TIM Testraum 1" beigetreten
    Und "B" lädt "A" in Chat-Raum "TIM Testraum 1" ein
    Dann "A" erhält eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "A" bestätigt eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "A" ist dem Raum "TIM Testraum 1" beigetreten
    Wenn "A" verlässt Raum "TIM Testraum 1"
    Und "C" versucht "A" in Chat-Raum "TIM Testraum 1" einzuladen
    Dann erhält "C" einen Responsecode 403
    Und "A" erhält KEINE Einladung von "C" für den Raum "TIM Testraum 1" [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient")) @Plugin:Filter(ApiName1C.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             | ApiName1C             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:MixedMode @Ctl:BP @TCID:TIM_V2_PRO_AF_050111 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.11 Einladung innerhalb einer Organisation - Raum - MixedMode - User (versucht)lädt anderen User ein
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "B" setzt den eigenen Authorization Mode auf "BlockAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" versucht "B" in Chat-Raum "TIM Testraum 1" einzuladen
    Dann erhält "A" einen Responsecode 403
    Und "B" erhält KEINE Einladung von "A" für den Raum "TIM Testraum 1" [Retry 6 - 1]
    Und "B" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Dann "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten
    Und "B" verlässt Raum "TIM Testraum 1"
    Und "B" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" versucht "B" in Chat-Raum "TIM Testraum 1" einzuladen
    Dann erhält "A" einen Responsecode 403
    Und "B" erhält KEINE Einladung von "A" für den Raum "TIM Testraum 1" [Retry 6 - 1]
    Und "B" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Dann "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:DomainBP @Ctl:BP @TCID:TIM_V2_PRO_AF_050112 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.12 Einladung innerhalb einer Organisation - Chat - DomainBlock - User blockt/ unblockt Server-Namen
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "B" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt den Server-Namen von "B" in der Blockliste
    Wenn "B" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "B" versucht "A" direkt "Testnachricht 1" zu schreiben
    Dann erhält "B" einen Responsecode 403
    Und "A" erhält KEINE Einladung von "B" [Retry 6 - 1]
    Und "A" entfernt den Server-Namen von "B" in der Blockliste
    Und "B" schreibt "A" direkt "Testnachricht 2"
    Dann "A" erhält eine Einladung von "B"
    Und "A" bestätigt eine Einladung von "B"
    Und "A" empfängt eine Nachricht "Testnachricht 2" von "B"

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:DomainBP @Ctl:BP @TCID:TIM_V2_PRO_AF_050113 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.13 Einladung innerhalb einer Organisation - Raum - DomainBlock - User blockt/ unblockt Server-Namen
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "B" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt den Server-Namen von "B" in der Blockliste
    Wenn "B" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "B" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "B" versucht "A" in Chat-Raum "TIM Testraum 1" einzuladen
    Dann erhält "B" einen Responsecode 403
    Und "A" erhält KEINE Einladung von "B" für den Raum "TIM Testraum 1" [Retry 6 - 1]
    Und "A" entfernt den Server-Namen von "B" in der Blockliste
    Und "B" lädt "A" in Chat-Raum "TIM Testraum 1" ein
    Dann "A" erhält eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "A" bestätigt eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "A" ist dem Raum "TIM Testraum 1" beigetreten

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:DomainBP @Ctl:BP @TCID:TIM_V2_PRO_AF_050114 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.14 Einladung innerhalb einer Organisation - Chat - DomainBlock - User allow/ unallow Server-Namen
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "B" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt den Server-Namen von "B" in der Allowliste
    Wenn "B" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "B" schreibt "A" direkt "Testnachricht 1"
    Dann "A" erhält eine Einladung von "B"
    Und "A" bestätigt eine Einladung von "B"
    Und "A" empfängt eine Nachricht "Testnachricht 1" von "B"
    Wenn "A" verlässt Chat mit "B"
    Und "A" entfernt den Server-Namen von "B" in der Allowliste
    Und "B" versucht "A" direkt "Testnachricht 2" zu schreiben
    Dann erhält "B" einen Responsecode 403
    Und "A" erhält KEINE Einladung von "B" [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:DomainBP @Ctl:BP @TCID:TIM_V2_PRO_AF_050115 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.15 Einladung innerhalb einer Organisation - Raum - DomainBlock - User allow/ unallow Server-Namen
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "B" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt den Server-Namen von "B" in der Allowliste
    Wenn "B" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "B" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "B" lädt "A" in Chat-Raum "TIM Testraum 1" ein
    Dann "A" erhält eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "A" bestätigt eine Einladung in Raum "TIM Testraum 1" von "B"
    Und "A" ist dem Raum "TIM Testraum 1" beigetreten
    Wenn "A" verlässt Raum "TIM Testraum 1"
    Und "A" entfernt den Server-Namen von "B" in der Allowliste
    Und "B" versucht "A" in Chat-Raum "TIM Testraum 1" einzuladen
    Dann erhält "B" einen Responsecode 403
    Und "A" erhält KEINE Einladung von "B" [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:InviteReject @Ctl:BP @TCID:TIM_V2_PRO_AF_050116 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.16 Einladung innerhalb einer Organisation - Chat - AllowAll - User lehnt Einladung ab
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "B" setzt den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" schreibt "B" direkt "Testnachricht 1"
    Dann "B" erhält eine Einladung von "A"
    Wenn "B" lehnt eine Einladung zum Chat mit "A" ab
    Dann "B" ist dem Chat mit "A" nicht beigetreten [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:InviteReject @Ctl:BP @TCID:TIM_V2_PRO_AF_050117 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.17 Einladung innerhalb einer Organisation - Raum - AllowAll - User lehnt Einladung ab
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "B" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Dann "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Wenn "B" lehnt eine Einladung von "A" für Raum "TIM Testraum 1" ab
    Dann "B" ist dem Raum "TIM Testraum 1" nicht beigetreten [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:BlockAll @Ctl:InviteReject @Ctl:BP @TCID:TIM_V2_PRO_AF_050118 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.18 Einladung innerhalb einer Organisation - Chat - BlockAll - User lehnt Einladung ab
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "B" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt die User "B" in der Allowliste
    Wenn "B" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "B" schreibt "A" direkt "Testnachricht 1"
    Dann "A" erhält eine Einladung von "B"
    Wenn "A" lehnt eine Einladung zum Chat mit "B" ab
    Dann "A" ist dem Chat mit "B" nicht beigetreten [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:BlockAll @Ctl:InviteReject @Ctl:BP @TCID:TIM_V2_PRO_AF_050119 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.19 Einladung innerhalb einer Organisation - Raum - BlockAll - User lehnt Einladung ab
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "B" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt die User "B" in der Allowliste
    Und "B" erstellt einen Chat-Raum "TIM Testraum 1"
    Wenn "B" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "B" lädt "A" in Chat-Raum "TIM Testraum 1" ein
    Dann "A" erhält eine Einladung in Raum "TIM Testraum 1" von "B"
    Wenn "A" lehnt eine Einladung von "B" für Raum "TIM Testraum 1" ab
    Dann "A" ist dem Raum "TIM Testraum 1" nicht beigetreten [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @BP @TCID:TIM_V2_PRO_AF_050120 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.20 Einladung innerhalb einer Organisation - Chat - AllowAll - Dritter Nutzer wird in Chat eingeladen werden
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
      | C | PRO_CLIENT | <ApiName1C> |
    Und "A", "B", "C" setzt den eigenen Authorization Mode auf "AllowAll"
    Wenn "C" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "C" schreibt "A" direkt "Testnachricht 1"
    Und "A" erhält eine Einladung von "C"
    Und "A" bestätigt eine Einladung von "C"
    Und "A" empfängt eine Nachricht "Testnachricht 1" von "C"
    Wenn "C" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "C" lädt "B" in Chat mit "A" ein
    Und "B" erhält eine Einladung von "C"
    Und "B" bestätigt eine Einladung von "C"
    Und "B" ist dem Chat mit "A", "C" beigetreten

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient")) @Plugin:Filter(ApiName1C.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             | ApiName1C             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @BP @TCID:TIM_V2_PRO_AF_050121 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.21 Einladung innerhalb einer Organisation - Chat - BlockAll - Dritter Nutzer wird in Chat eingeladen werden
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
      | C | PRO_CLIENT | <ApiName1C> |
    Und "B", "C" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Wenn "C" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "C" schreibt "B" direkt "Testnachricht 1"
    Und "B" erhält eine Einladung von "C"
    Und "B" bestätigt eine Einladung von "C"
    Und "B" empfängt eine Nachricht "Testnachricht 1" von "C"
    Wenn "C" findet TI-Messenger-Nutzer "A" bei der Suche im HomeServer
    Und "C" versucht "A" in Chat mit "B" einzuladen
    Dann erhält "C" einen Responsecode 403

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient")) @Plugin:Filter(ApiName1C.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             | ApiName1C             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050122 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.22 Einladung innerhalb einer Organisation - AllowAll - Eintragung in Liste von anderem Berechtigungsmodus
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" versucht User "B" in der Allowliste zu hinterlegen
    Dann erhält "A" einen Responsecode 403

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050123 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.23 Einladung innerhalb einer Organisation - AllowAll - Eintragung eines Server-Namen in Liste von anderem Berechtigungsmodus
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" versucht den Server-Namen von "B" in der Allowliste zu hinterlegen
    Dann erhält "A" einen Responsecode 403

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050124 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.24 Einladung innerhalb einer Organisation - AllowAll - Löschen in Liste von anderem Berechtigungsmodus
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt die User "B" in der Allowliste
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" versucht die User "B" in der Allowliste zu entfernen
    Dann erhält "A" einen Responsecode 403

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050125 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.25 Einladung innerhalb einer Organisation - AllowAll - Löschen eines Server-Namen in Liste von anderem Berechtigungsmodus
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt den Server-Namen von "B" in der Allowliste
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" versucht den Server-Namen von "B" in der Allowliste zu entfernen
    Dann erhält "A" einen Responsecode 403

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:BlockAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050126 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.26 Einladung innerhalb einer Organisation - BlockAll - Eintragung in Liste von anderem Berechtigungsmodus
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" versucht User "B" in der Blockliste zu hinterlegen
    Dann erhält "A" einen Responsecode 403

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:BlockAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050127 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.27 Einladung innerhalb einer Organisation - BlockAll - Eintragung eines Server-Namen in Liste von anderem Berechtigungsmodus
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" versucht den Server-Namen von "B" in der Blockliste zu hinterlegen
    Dann erhält "A" einen Responsecode 403

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:BlockAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050128 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.28 Einladung innerhalb einer Organisation - BlockAll - Löschen in Liste von anderem Berechtigungsmodus
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt die User "B" in der Blockliste
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" versucht die User "B" in der Blockliste zu entfernen
    Dann erhält "A" einen Responsecode 403

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:BlockAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050129 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.29 Einladung innerhalb einer Organisation - BlockAll - Löschen eines Server-Namen in Liste von anderem Berechtigungsmodus
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt den Server-Namen von "B" in der Blockliste
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" versucht den Server-Namen von "B" in der Blockliste zu entfernen
    Dann erhält "A" einen Responsecode 403

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050130 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 05.01.30 Einladung innerhalb einer Organisation - AllowAll - Eintrag doppelter MXID Einträge in BlockListe
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt die User "B" in der Blockliste
    Und "A" versucht die User "B" in der Blockliste erneut zu hinterlegen
    Dann erhält "A" einen der Responsecodes "200", "403"
    Und "A" entfernt die User "B" in der Blockliste
    Und "A" findet "B" nicht mehr in seiner Blockliste

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:BlockAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050131 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 05.01.31 Einladung innerhalb einer Organisation - BlockAll - Eintrag doppelter MXID Einträge in AllowListe
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt die User "B" in der Allowliste
    Und "A" versucht die User "B" in der Allowliste erneut zu hinterlegen
    Dann erhält "A" einen der Responsecodes "200", "403"
    Und "A" entfernt die User "B" in der Allowliste
    Und "A" findet "B" nicht mehr in seiner Allowliste

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050132 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.32 Einladung innerhalb einer Organisation - AllowAll - Einträge bleiben trotz Logout erhalten
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A" setzt den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt die User "B" in der Blockliste
    Und "A" hinterlegt den Server-Namen von "B" in der Blockliste
    Und "A" loggt sich im TI-Messenger aus
    Und "A" loggt sich im TI-Messenger ein
    Und "A" prüft, ob der eigene Authorization Mode auf "AllowAll" gesetzt ist
    Und "A" prüft, ob User "B" in der Blockliste gesetzt ist
    Und "A" prüft, ob der Server-Name von "B" in der Blockliste gesetzt ist

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:BlockAll @Ctl:BP @TCID:TIM_V2_PRO_AF_050133 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 05.01.33 Einladung innerhalb einer Organisation - BlockAll - Einträge bleiben trotz Logout erhalten
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A" setzt den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt die User "B" in der Allowliste
    Und "A" hinterlegt den Server-Namen von "B" in der Allowliste
    Und "A" loggt sich im TI-Messenger aus
    Und "A" loggt sich im TI-Messenger ein
    Und "A" prüft, ob der eigene Authorization Mode auf "BlockAll" gesetzt ist
    Und "A" prüft, ob User "B" in der Allowliste gesetzt ist
    Und "A" prüft, ob der Server-Name von "B" in der Allowliste gesetzt ist

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
# @MaxSameColumnProperty(ApiName1A,homeserver,1) #
