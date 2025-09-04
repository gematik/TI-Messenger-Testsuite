# language: de
@File:FeatureFile_11X_01_V2_Basis @Ctl:UseCaseV2_11X_Basis @PRODUKT:TI-M_Client_Basis @PRODUKT:TI-M_FD_Basis @Zul:Pro @Zul:ProKK @AFO-ID:A_25395-01
Funktionalität: 11X. (1) Message Types (Basis Spec)
  In diesen Testfällen soll das Versenden und Empfangen von den in Matrix spezifizierten Message Types (inkl. ihrer
  Konformität zur Matrix Spezifiaktion) überprüft werden.

  Wichtig: Die Gematik behält sich vor die Typen/Files der Attachments (m.image, m.file, m.audio, m.video) jederzeit zu
  ändern oder zu erweitern, da die Struktur dieser Matrix-Events davon unbetroffen ist.

  COMMENT: Basis
  A_25395-01   Matrix Module - Instant Messaging

  Inhalt
  TF  1 -  2  m.text (Chat/Raum)
  TF  3 -  4  m.emote (Chat/Raum)
  TF  5 -  6  m.notice (Chat/Raum)
  TF  7 -  8  m.location (Chat/Raum)
  TF  9 - 12  m.image (Chat/Raum) (jpg,png)
  TF 13 - 16  m.file (Chat/Raum) (txt,pdf)
  TF 17 - 20  m.audio (Chat/Raum) (mp3,wav)
  TF 21 - 24  m.video (Chat/Raum) (mp4,mov)

  @Ctl:AllowAll @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0101 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 11X.01.01 Message Types - Chat - AllowAll - Org-User sendet Nachricht (m.text) an anderen Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" schreibt "B" direkt "Testnachricht 1" als "m.text"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt eine Nachricht "Testnachricht 1" von "A" als "m.text"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0102 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 11X.01.02 Message Types - Raum - AllowAll - Org-User sendet Nachricht (m.text) an anderen Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Wenn "A" sendet die Nachricht "Testnachricht 1" als "m.text" an den Raum "TIM Testraum 1"
    Dann "B" empfängt eine Nachricht "Testnachricht 1" als "m.text" von "A" im Raum "TIM Testraum 1"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0103 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.03 Message Types - Chat - AllowAll - Org-User sendet Nachricht (m.emote) an anderen Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" schreibt "B" direkt "Testnachricht 1" als "m.emote"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt eine Nachricht "Testnachricht 1" von "A" als "m.emote"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0104 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.04 Message Types - Raum - AllowAll - Org-User sendet Nachricht (m.emote) an anderen Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Wenn "A" sendet die Nachricht "Testnachricht 1" als "m.emote" an den Raum "TIM Testraum 1"
    Dann "B" empfängt eine Nachricht "Testnachricht 1" als "m.emote" von "A" im Raum "TIM Testraum 1"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0105 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.05 Message Types - Chat - AllowAll - Org-User sendet Nachricht (m.notice) an anderen Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" schreibt "B" direkt "Testnachricht 1" als "m.notice"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt eine Nachricht "Testnachricht 1" von "A" als "m.notice"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0106 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.06 Message Types - Raum - AllowAll - Org-User sendet Nachricht (m.notice) an anderen Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Wenn "A" sendet die Nachricht "Testnachricht 1" als "m.notice" an den Raum "TIM Testraum 1"
    Dann "B" empfängt eine Nachricht "Testnachricht 1" als "m.notice" von "A" im Raum "TIM Testraum 1"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0107 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.07 Message Types - Chat - AllowAll - Org-User sendet Location (m.location) an anderen Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" schreibt "B" direkt "Testnachricht 1"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt eine Nachricht "Testnachricht 1" von "A"
    Wenn "A" sendet "B" die Location "Location 1" als "m.location"
    Dann "B" empfängt die Location "Location 1" von "A" als "m.location"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0108 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.08 Message Types - Raum - AllowAll - Org-User sendet Location (m.location) an anderen Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Wenn "A" sendet die Location "Location 1" als "m.location" an den Raum "TIM Testraum 1"
    Dann "B" empfängt die Location "Location 1" als "m.location" von "A" im Raum "TIM Testraum 1"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0109 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 11X.01.09 Message Types - Chat - AllowAll - Org-User sendet ein m.image Attachment (jpg) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" sendet ein Attachment "bild.jpg" direkt an "B" als "m.image"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt das Attachment "bild.jpg" von "A" über Matrix-Protokoll v1.11 als "m.image"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0110 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 11X.01.10 Message Types - Raum - AllowAll - Org-User sendet ein m.image Attachment (jpg) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten
    Wenn "A" sendet ein Attachment "bild.jpg" als "m.image" an den Raum "TIM Testraum 1"
    Dann "B" empfängt das Attachment "bild.jpg" von "A" im Raum "TIM Testraum 1" über Matrix-Protokoll v1.11 als "m.image"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0111 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 11X.01.11 Message Types - Chat - AllowAll - Org-User sendet ein m.image Attachment (png) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" sendet ein Attachment "bild.png" direkt an "B" als "m.image"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt das Attachment "bild.png" von "A" über Matrix-Protokoll v1.11 als "m.image"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0112 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 11X.01.12 Message Types - Raum - AllowAll - Org-User sendet ein m.image Attachment (png) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten
    Wenn "A" sendet ein Attachment "bild.png" als "m.image" an den Raum "TIM Testraum 1"
    Dann "B" empfängt das Attachment "bild.png" von "A" im Raum "TIM Testraum 1" über Matrix-Protokoll v1.11 als "m.image"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0113 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 11X.01.13 Message Types - Chat - AllowAll - Org-User sendet ein m.file Attachment (txt) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" sendet ein Attachment "datei.txt" direkt an "B" als "m.file"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt das Attachment "datei.txt" von "A" über Matrix-Protokoll v1.11 als "m.file"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0114 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 11X.01.14 Message Types - Raum - AllowAll - Org-User sendet ein m.file Attachment (txt) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten
    Wenn "A" sendet ein Attachment "datei.txt" als "m.file" an den Raum "TIM Testraum 1"
    Dann "B" empfängt das Attachment "datei.txt" von "A" im Raum "TIM Testraum 1" über Matrix-Protokoll v1.11 als "m.file"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0115 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 11X.01.15 Message Types - Chat - AllowAll - Org-User sendet ein m.file Attachment (pdf) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" sendet ein Attachment "datei.pdf" direkt an "B" als "m.file"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt das Attachment "datei.pdf" von "A" über Matrix-Protokoll v1.11 als "m.file"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0116 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:NEIN
  Szenariogrundriss: 11X.01.16 Message Types - Raum - AllowAll - Org-User sendet ein m.file Attachment (pdf) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten
    Wenn "A" sendet ein Attachment "datei.pdf" als "m.file" an den Raum "TIM Testraum 1"
    Dann "B" empfängt das Attachment "datei.pdf" von "A" im Raum "TIM Testraum 1" über Matrix-Protokoll v1.11 als "m.file"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0117 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.17 Message Types - Chat - AllowAll - Org-User sendet ein m.audio Attachment (mp3) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" sendet ein Attachment "audio.mp3" direkt an "B" als "m.audio"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt das Attachment "audio.mp3" von "A" über Matrix-Protokoll v1.11 als "m.audio"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0118 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.18 Message Types - Raum - AllowAll - Org-User sendet ein m.audio Attachment (mp3) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten
    Wenn "A" sendet ein Attachment "audio.mp3" als "m.audio" an den Raum "TIM Testraum 1"
    Dann "B" empfängt das Attachment "audio.mp3" von "A" im Raum "TIM Testraum 1" über Matrix-Protokoll v1.11 als "m.audio"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0119 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.19 Message Types - Chat - AllowAll - Org-User sendet ein m.audio Attachment (wav) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" sendet ein Attachment "audio.wav" direkt an "B" als "m.audio"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt das Attachment "audio.wav" von "A" über Matrix-Protokoll v1.11 als "m.audio"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0120 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.20 Message Types - Raum - AllowAll - Org-User sendet ein m.audio Attachment (wav) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten
    Wenn "A" sendet ein Attachment "audio.wav" als "m.audio" an den Raum "TIM Testraum 1"
    Dann "B" empfängt das Attachment "audio.wav" von "A" im Raum "TIM Testraum 1" über Matrix-Protokoll v1.11 als "m.audio"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0121 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.21 Message Types - Chat - AllowAll - Org-User sendet ein m.video Attachment (mp4) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" sendet ein Attachment "video.mp4" direkt an "B" als "m.video"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt das Attachment "video.mp4" von "A" über Matrix-Protokoll v1.11 als "m.video"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0122 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.22 Message Types - Raum - AllowAll - Org-User sendet ein m.video Attachment (mp4) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten
    Wenn "A" sendet ein Attachment "video.mp4" als "m.video" an den Raum "TIM Testraum 1"
    Dann "B" empfängt das Attachment "video.mp4" von "A" im Raum "TIM Testraum 1" über Matrix-Protokoll v1.11 als "m.video"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0123 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.23 Message Types - Chat - AllowAll - Org-User sendet ein m.video Attachment (mov) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Wenn "A" sendet ein Attachment "video.mov" direkt an "B" als "m.video"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Dann "B" empfängt das Attachment "video.mov" von "A" über Matrix-Protokoll v1.11 als "m.video"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS4 | RU2-Ref-Pract-SDK-HS3 |

  @Ctl:AllowAll @Ctl:Attachment @Ctl:MsgType @TCID:TIM_V2_BASIS_AF_11X0124 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert @NB:JA
  Szenariogrundriss: 11X.01.24 Message Types - Raum - AllowAll - Org-User sendet ein m.video Attachment (mov) über Matrix-Protokoll v1.11 an Org-User
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1"
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Und "B" ist dem Raum "TIM Testraum 1" beigetreten
    Wenn "A" sendet ein Attachment "video.mov" als "m.video" an den Raum "TIM Testraum 1"
    Dann "B" empfängt das Attachment "video.mov" von "A" im Raum "TIM Testraum 1" über Matrix-Protokoll v1.11 als "m.video"

    # @MaxSameColumnProperty(ApiName2,homeserver,1) #
    @Plugin:Shuffle(true) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
# @MaxSameColumnProperty(ApiName2,homeserver,1) #
