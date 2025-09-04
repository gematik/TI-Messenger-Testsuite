# language: de
@File:FeatureFile_09X_01_V2_Basis @Ctl:UseCaseV2_09X_Basis @PRODUKT:TI-M_Client_Basis @PRODUKT:TI-M_FD_Basis @Zul:Pro @Zul:ProKK @AFO-ID:A_25813-01 @AFO-ID:A_25814-01 @AFO-ID:A_25426 @AFO-ID:A_26248 @AFO-ID:A_26338-01 @NB:NEIN
Funktionalität: 09X. (1) Room States (Basis Spec)
  Die folgenden Testfälle sollen die Funktionalität des Setzens verschiedener Room States testen. Im ersten Schritt wird
  an dieser Stelle das Setzen des Default-Room-States betrachtet.

  COMMENT: Basis
  A_25813-01 - Pflichtparameter bei der Chatroom-Erzeugung für föderierte und intersektorale Kommunikation
  A_25814-01 - Verwendung des Raumtypen für föderierte und intersektorale Kommunikation als Standard
  A_25426    - Verwendung des Chatroom-Typen für föderierte und intersektorale Kommunikation
  A_26248    - Default-Raumversion beim Erstellen von Räumen
  A_26338-01 - Erzeugung und Verwendung der Custom State Events für Raumnamen und -thema

  Inhalt
  TF 1 & 2 Default Room States und Raumversion in Chat und Raum (Org-User)

  @Ctl:AllowAll @Ctl:RoomStates @Ctl:RoomVersion @TCID:TIM_V2_BASIS_AF_09X0101 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 09X.01.01 Room States - Chat - Default Room State und Raumversion (Org-User)
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" schreibt "B" direkt "Testnachricht 1"
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung von "A"
    Wenn "B" empfängt eine Nachricht "Testnachricht 1" von "A"
    Dann "B" prüft die Raumversion im Chat mit "A" auf Version "10"
    Dann "B" prüft den Room State im Chat mit "A" auf "TIM_ROOM_TYPE"
    Dann "B" prüft, ob die Room States "ROOM_NAME,ROOM_TOPIC,TIM_ROOM_NAME,TIM_ROOM_TOPIC" im Chat mit "A" vorhanden sind
    Dann "B" prüft, ob der Parameter in den Room States "ROOM_NAME" im Chat mit "A" mit dem Wert von "TIM_ROOM_NAME" befüllt ist
    Dann "B" prüft, ob der Parameter in den Room States "ROOM_TOPIC" im Chat mit "A" mit dem Wert von "TIM_ROOM_TOPIC" befüllt ist

    @Plugin:Shuffle(true) @Plugin:MaxSameColumnProperty(ApiName1,homeserver,1) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:RoomStates @Ctl:RoomVersion @TCID:TIM_V2_BASIS_AF_09X0102 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 09X.01.02 Room States - Raum - Default Room State und Raumversion (Org-User)
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
      | B | PRO_CLIENT | <ApiName2> |
    Und "A", "B" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" erstellt einen Chat-Raum "TIM Testraum 1" inkl. Topic
    Und "A" lädt "B" in Chat-Raum "TIM Testraum 1" ein
    Und "B" erhält eine Einladung von "A"
    Und "B" bestätigt eine Einladung in Raum "TIM Testraum 1" von "A"
    Wenn "B" ist dem Raum "TIM Testraum 1" beigetreten
    Dann "B" prüft die Raumversion im Raum "TIM Testraum 1" auf Version "10"
    Dann "B" prüft den Room State im Raum "TIM Testraum 1" auf "TIM_ROOM_TYPE"
    Dann "B" prüft, ob die Room States "ROOM_NAME,ROOM_TOPIC,TIM_ROOM_NAME,TIM_ROOM_TOPIC" im Raum "TIM Testraum 1" vorhanden sind
    Dann "B" prüft, ob die Parameter in den Room States "TIM_ROOM_NAME,TIM_ROOM_TOPIC" im Raum "TIM Testraum 1" befüllt sind
    Dann "B" prüft, ob der Parameter in den Room States "ROOM_NAME" im Raum "TIM Testraum 1" mit dem Wert von "TIM_ROOM_NAME" befüllt ist
    Dann "B" prüft, ob der Parameter in den Room States "ROOM_TOPIC" im Raum "TIM Testraum 1" mit dem Wert von "TIM_ROOM_TOPIC" befüllt ist

    @Plugin:Shuffle(true) @Plugin:MaxSameColumnProperty(ApiName1,homeserver,1) @Plugin:DistinctProperty(homeserver) @Plugin:AllowSelfCombine(false) @Plugin:AllowDoubleLineup(true) @Plugin:Filter(ApiName1.hasTag("proClient")) @Plugin:Filter(ApiName2.hasTag("proClient"))
    Beispiele:
      | ApiName1              | ApiName2              |
      | RU2-Ref-Pract-SDK-HS3 | RU2-Ref-Pract-SDK-HS4 |
