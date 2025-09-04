# language: de
@File:FeatureFile_04_01_V2_Basis @Ctl:UseCaseV2_04_Basis @Ctl:OneHomeServer @PRODUKT:TI-M_Client_Basis @PRODUKT:TI-M_FD_Basis @Zul:Pro @Zul:ProKK @AF-ID:AF_10063-01 @AFO-ID:A_25575 @AFO-ID:A_25576 @AFO-ID:A_25492 @AFO-ID:A_26574 @AFO-ID:A_26575 @AFO-ID:A_25395-01 @AFO-ID:A_26077 @NB:NEIN
Funktionalität: 5.1.4 (1) Austausch von Events innerhalb einer Organisation (Basis Spec)
  Dieser Anwendungsfall ermöglicht es Akteuren, welche sich in einem gemeinsamen Raum innerhalb eines Messenger-Service befinden,
  Nachrichten auszutauschen und weitere durch die Matrix-Spezifikation festgelegte Aktionen (Events) auszuführen.

  COMMENT: Basis
  AF_10063-01 Austausch von Events zwischen Akteuren innerhalb einer Organisation
  A_25575    Löschen von Nachrichten
  A_25576    Lokales Löschen bei Entfernung von Nachrichten aus dem Room State
  A_25492    Löschfunktion des Clients
  A_26574    Entschlüsseln von Nachrichten nach Wiederanmeldung
  A_26575    Ablage von Schlüsseln zum Entschlüsseln von Nachrichten nach Wiederanmeldung
  A_25395-01 Matrix Module - Direct Messaging/Event Replacements
  A_26077    Server-seitiges Schlüssel-Backup

  Inhalt
  TF 1  - 2   User sendet Nachricht an anderen User über HomeServer (Chat/Raum)
  TF 3  - 4   User sendet Nachricht an anderen User über Healthcareservice (Chat/Raum)
  TF 5  - 6   Löschen einer Nachricht durch User (Chat/Raum)
  TF 7  - 8   Ändern einer Nachricht durch User (Chat/Raum)
  TF 9        User sendet eine Nachricht an ausgeloggten User - Dehydrated Devices (Raum)
  TF 11 - 12  User sendet und ändert eine Nachricht an ausgeloggten User - Dehydrated Devices (Chat/Raum)
  TF 13       User sendet ein Attachment an ausgeloggten User über Matrix-Protokoll v1.11 - Dehydrated Devices
  TF 15       Gruppenchat - Drei User schreiben sich gegenseitig (Raum)

  @Ctl:AllowAll @TCID:TIM_V2_BASIS_AF_040101 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 04.01.01 Austausch von Events innerhalb einer Organisation - Chat - User sendet Nachricht an anderen User über HomeServer
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Wenn "A" schreibt "B" direkt "Testnachricht 1"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt eine Nachricht "Testnachricht 1" von "A"
    Wenn "B" schreibt "A" direkt "Testnachricht 2"
    Dann "A" empfängt eine Nachricht "Testnachricht 2" von "B"

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @TCID:TIM_V2_BASIS_AF_040102 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 04.01.02 Austausch von Events innerhalb einer Organisation - Raum - User sendet Nachricht an anderen User über HomeServer
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Wenn "A" sendet die Nachricht "Testnachricht 1" an den Raum "TIM Testraum 1"
    Dann "B" empfängt eine Nachricht "Testnachricht 1" von "A" im Raum "TIM Testraum 1"

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:OrgAdmin @Ctl:VZD @TCID:TIM_V2_BASIS_AF_040103 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 04.01.03 Austausch von Events innerhalb einer Organisation - Chat - User sendet Nachricht an anderen User über Healthcareservice
    Angenommen Es werden folgende Clients reserviert:
      | A | ORG_ADMIN  | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
      | C | PRO_CLIENT | <ApiName1C> |
    Und "B", "C" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" erstellt einen Healthcare-Service "HealthcareServiceName1" und setzen einen Endpunkt auf "B"
    Dann "C" findet "B" im Healthcare-Service "HealthcareServiceName1"
    Wenn "C" schreibt "B" direkt "Testnachricht 1"
    Und "B" erhält eine Einladung von "C"
    Und "B" bestätigt eine Einladung von "C"
    Dann "B" empfängt eine Nachricht "Testnachricht 1" von "C"
    Wenn "B" schreibt "C" direkt "Testnachricht 2"
    Dann "C" empfängt eine Nachricht "Testnachricht 2" von "B"

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("orgAdmin")) @Plugin:Filter(ApiName1B.hasTag("proClient")) @Plugin:Filter(ApiName1C.hasTag("proClient"))
    Beispiele:
      | ApiName1A           | ApiName1B             | ApiName1C             |
      | RU2-Ref-ORG-Web-HS3 | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:OrgAdmin @Ctl:VZD @TCID:TIM_V2_BASIS_AF_040104 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 04.01.04 Austausch von Events innerhalb einer Organisation - Raum - User sendet Nachricht an anderen User über Healthcareservice
    Angenommen Es werden folgende Clients reserviert:
      | A | ORG_ADMIN  | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
      | C | PRO_CLIENT | <ApiName1C> |
    Und "B", "C" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" erstellt einen Healthcare-Service "HealthcareServiceName1" und setzen einen Endpunkt auf "B"
    Dann "C" findet "B" im Healthcare-Service "HealthcareServiceName1"
    Und "C" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "C" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "C"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "C"
    Wenn "C" sendet die Nachricht "Testnachricht 1" an den Raum "TIM Testraum 1"
    Dann "B" empfängt eine Nachricht "Testnachricht 1" von "C" im Raum "TIM Testraum 1"

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("orgAdmin")) @Plugin:Filter(ApiName1B.hasTag("proClient")) @Plugin:Filter(ApiName1C.hasTag("proClient"))
    Beispiele:
      | ApiName1A           | ApiName1B             | ApiName1C             |
      | RU2-Ref-ORG-Web-HS4 | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:MsgDelete @TCID:TIM_V2_BASIS_AF_040105 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 04.01.05 Austausch von Events innerhalb einer Organisation - Chat - Löschen einer Nachricht durch User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" schreibt "B" direkt "Testnachricht 1"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Und "B" empfängt eine Nachricht "Testnachricht 1" von "A"
    Und "B" schreibt "A" direkt "Testnachricht 2"
    Und "A" empfängt eine Nachricht "Testnachricht 2" von "B"
    Wenn "B" löscht seine Nachricht "Testnachricht 2" im Chat mit "A"
    Dann "A" kann die Nachricht "Testnachricht 2" von "B" im Chat mit "B" nicht mehr sehen [Retry 6 - 1]
    Und "B" kann die Nachricht "Testnachricht 2" von "B" im Chat mit "A" nicht mehr sehen [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:MsgDelete @TCID:TIM_V2_BASIS_AF_040106 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 04.01.06 Austausch von Events innerhalb einer Organisation - Raum - Löschen einer Nachricht durch User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "A" sendet die Nachricht "Testnachricht 1" an den Raum "TIM Testraum 1"
    Und "B" empfängt eine Nachricht "Testnachricht 1" von "A" im Raum "TIM Testraum 1"
    Wenn "A" löscht seine Nachricht "Testnachricht 1" im Raum "TIM Testraum 1"
    Dann "B" kann die Nachricht "Testnachricht 1" von "A" im Raum "TIM Testraum 1" nicht mehr sehen [Retry 6 - 1]
    Und "A" kann die Nachricht "Testnachricht 1" von "A" im Raum "TIM Testraum 1" nicht mehr sehen [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:MsgEdit @TCID:TIM_V2_BASIS_AF_040107 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 04.01.07 Austausch von Events innerhalb einer Organisation - Chat - Ändern einer Nachricht durch User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" schreibt "B" direkt "Testnachricht 1"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Und "B" empfängt eine Nachricht "Testnachricht 1" von "A"
    Dann "A" ändert seine letzte Nachricht im Chat mit "B" in "Testnachricht 2"
    Wenn "B" empfängt eine Nachricht "Testnachricht 2" von "A"
    Dann "B" kann die Nachricht "Testnachricht 1" von "A" im Chat mit "A" nicht mehr sehen [Retry 6 - 1]
    Und "A" kann die Nachricht "Testnachricht 1" von "A" im Chat mit "B" nicht mehr sehen [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:MsgEdit @TCID:TIM_V2_BASIS_AF_040108 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 04.01.08 Austausch von Events innerhalb einer Organisation - Raum - Ändern einer Nachricht durch User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "A" sendet die Nachricht "Testnachricht 1" an den Raum "TIM Testraum 1"
    Und "B" empfängt eine Nachricht "Testnachricht 1" von "A" im Raum "TIM Testraum 1"
    Dann "A" ändert seine letzte Nachricht im Raum "TIM Testraum 1" in "Testnachricht 2"
    Wenn "B" empfängt eine Nachricht "Testnachricht 2" von "A" im Raum "TIM Testraum 1"
    Und "B" kann die Nachricht "Testnachricht 1" von "A" im Raum "TIM Testraum 1" nicht mehr sehen [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:MsgDehydrated @TCID:TIM_V2_BASIS_AF_040109 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 04.01.09 Austausch von Events innerhalb einer Organisation - Raum - AllowAll - User sendet eine Nachricht an ausgeloggten User (Positiv)
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "B" loggt sich im TI-Messenger aus
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "A" sendet die Nachricht "Testnachricht 1" an den Raum "TIM Testraum 1"
    Wenn "B" loggt sich im TI-Messenger ein
    Dann "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" empfängt eine Nachricht "Testnachricht 1" von "A" im Raum "TIM Testraum 1"

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:MsgDehydrated @Ctl:MsgEdit @TCID:TIM_V2_BASIS_AF_040111 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 04.01.11 Austausch von Events innerhalb einer Organisation - Chat - AllowAll - User sendet und ändert eine Nachricht an ausgeloggten User (Positiv)
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "B" loggt sich im TI-Messenger aus
    Und "A" schreibt "B" direkt "Testnachricht 1"
    Dann "A" ändert seine letzte Nachricht im Chat mit "B" in "Testnachricht 2"
    Wenn "B" loggt sich im TI-Messenger ein
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Wenn "B" empfängt eine Nachricht "Testnachricht 2" von "A"
    Dann "B" kann die Nachricht "Testnachricht 1" von "A" im Chat mit "A" nicht mehr sehen [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:MsgDehydrated @Ctl:MsgEdit @TCID:TIM_V2_BASIS_AF_040112 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 04.01.12 Austausch von Events innerhalb einer Organisation - Raum - AllowAll - User sendet und ändert eine Nachricht an ausgeloggten User (Positiv)
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "B" loggt sich im TI-Messenger aus
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "A" sendet die Nachricht "Testnachricht 1" an den Raum "TIM Testraum 1"
    Und "A" ändert seine letzte Nachricht im Raum "TIM Testraum 1" in "Testnachricht 2"
    Wenn "B" loggt sich im TI-Messenger ein
    Dann "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" empfängt eine Nachricht "Testnachricht 2" von "A" im Raum "TIM Testraum 1"
    Und "B" kann die Nachricht "Testnachricht 1" von "A" im Raum "TIM Testraum 1" nicht mehr sehen [Retry 6 - 1]

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:MsgDehydrated @Ctl:Attachment @TCID:TIM_V2_BASIS_AF_040113 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 04.01.13 Austausch von Events innerhalb einer Organisation - Raum - AllowAll - User sendet ein Attachment an ausgeloggten User über Matrix-Protokoll v1.11
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "B" loggt sich im TI-Messenger aus
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Wenn "A" sendet ein Attachment "bild.jpg" als "m.image" an den Raum "TIM Testraum 1"
    Wenn "B" loggt sich im TI-Messenger ein
    Dann "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten
    Dann "B" empfängt das Attachment "bild.jpg" von "A" im Raum "TIM Testraum 1" über Matrix-Protokoll v1.11 als "m.image"

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @TCID:TIM_V2_BASIS_AF_040115 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 04.01.15 Austausch von Events innerhalb einer Organisation - Raum - AllowAll - Gruppenchat - Drei User schreiben sich gegenseitig
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1A> |
      | B | PRO_CLIENT | <ApiName1B> |
      | C | PRO_CLIENT | <ApiName1C> |
    Und "A", "B", "C" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" findet TI-Messenger-Nutzer "B" bei der Suche im HomeServer
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten
    Wenn "A" findet TI-Messenger-Nutzer "C" bei der Suche im HomeServer
    Und "A" lädt "C" in Chat-Raum "TIM Testraum 1" ein
    Und "C" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "C" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "C" ist dem Raum "TIM Testraum 1" beigetreten
    Und "A" sendet die Nachricht "Testnachricht 1" an den Raum "TIM Testraum 1"
    Und "B", "C" empfangen eine Nachricht "Testnachricht 1" von "A" im Raum "TIM Testraum 1"
    Und "B" sendet die Nachricht "Testnachricht 2" an den Raum "TIM Testraum 1"
    Und "A", "C" empfangen eine Nachricht "Testnachricht 2" von "B" im Raum "TIM Testraum 1"
    Und "C" sendet die Nachricht "Testnachricht 3" an den Raum "TIM Testraum 1"
    Und "A", "B" empfangen eine Nachricht "Testnachricht 3" von "C" im Raum "TIM Testraum 1"

    # @MaxSameColumnProperty(ApiName1A,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:EqualProperty(homeserver) @Plugin:AllowSelfCombine(true) @Plugin:AllowDoubleLineup(false) @Plugin:Filter(ApiName1A.hasTag("proClient")) @Plugin:Filter(ApiName1B.hasTag("proClient")) @Plugin:Filter(ApiName1C.hasTag("proClient"))
    Beispiele:
      | ApiName1A             | ApiName1B             | ApiName1C             |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS3 |
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
