# language: de
@File:FeatureFile_06_01_V2_Basis @Ctl:UseCaseV2_06_Basis @PRODUKT:TI-M_Client_Pro @PRODUKT:TI-M_FD_Pro @Zul:Pro @Zul:ProKK @AFO-ID:A_26390 @NB:JA
Funktionalität: 06X. (1) Berechtigungskonfiguration Benutzergruppen (Pro Spec)
  Für die Akteure des TI-M Pro wird die Möglichkeit geschaffen, bestimmte Nutzergruppen in der
  Berechtigungskonfiguration nutzen zu können. Als erste Benutzergruppe wird die Gruppe der Versicherten eingeführt.

  COMMENT: Pro
  A_26390       Schema der Berechtigungskonfiguration

  Inhalt
  TF 1      GruppenBlock - User blockt/entblockt Gruppe (AllowAll)
  TF 2      GruppenAllow - User allow/unallow Gruppe (BlockAll)
  TF 3      GruppenBlock - Einträge bleiben trotz Logout erhalten
  TF 4      GruppenAllow - Einträge bleiben trotz Logout erhalten

  @Ctl:AllowAll @Ctl:GruppenBP @TCID:TIM_V2_PRO_AF_06X0101 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert
  Szenariogrundriss: 06X.01.01 Berechtigungskonfiguration Benutzergruppen - AllowAll - GruppenBlock - User blockt/entblockt Gruppe
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
    Und "A" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt die Gruppe "Versicherte" in der Blockliste
    Und "A" prüft, dass die Gruppe "Versicherte" in der Blockliste gesetzt ist
    Und "A" entfernt die Gruppe "Versicherte" aus der Blockliste
    Und "A" prüft, dass die Gruppe "Versicherte" in der Blockliste nicht gesetzt ist

    @Plugin:Filter(ApiName1.hasTag("proClient"))
    Beispiele:
      | ApiName1              |
      | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:BlockAll @Ctl:GruppenBP @TCID:TIM_V2_PRO_AF_06X0102 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 06X.01.02 Berechtigungskonfiguration Benutzergruppen - BlockAll - GruppenAllow - User allow/unallow Gruppe
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
    Und "A" setzen den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt die Gruppe "Versicherte" in der Allowliste
    Und "A" prüft, dass die Gruppe "Versicherte" in der Allowliste gesetzt ist
    Und "A" entfernt die Gruppe "Versicherte" aus der Allowliste
    Und "A" prüft, dass die Gruppe "Versicherte" in der Allowliste nicht gesetzt ist

    @Plugin:Filter(ApiName1.hasTag("proClient"))
    Beispiele:
      | ApiName1              |
      | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:AllowAll @Ctl:GruppenBP @TCID:TIM_V2_PRO_AF_06X0103 @PRIO:1 @TESTFALL:Negativ @STATUS:Implementiert
  Szenariogrundriss: 06X.01.03 Berechtigungskonfiguration Benutzergruppen - AllowAll - GruppenBlock - Einträge bleiben trotz Logout erhalten
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
    Und "A" setzen den eigenen Authorization Mode auf "AllowAll"
    Und "A" hinterlegt die Gruppe "Versicherte" in der Blockliste
    Und "A" prüft, dass die Gruppe "Versicherte" in der Blockliste gesetzt ist
    Und "A" loggt sich im TI-Messenger aus
    Und "A" loggt sich im TI-Messenger ein
    Und "A" prüft, ob der eigene Authorization Mode auf "AllowAll" gesetzt ist
    Und "A" prüft, dass die Gruppe "Versicherte" in der Blockliste gesetzt ist

    @Plugin:Filter(ApiName1.hasTag("proClient"))
    Beispiele:
      | ApiName1              |
      | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:BlockAll @Ctl:GruppenBP @TCID:TIM_V2_PRO_AF_06X0104 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 06X.01.04 Berechtigungskonfiguration Benutzergruppen - BlockAll - GruppenAllow - Einträge bleiben trotz Logout erhalten
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
    Und "A" setzen den eigenen Authorization Mode auf "BlockAll"
    Und "A" hinterlegt die Gruppe "Versicherte" in der Allowliste
    Und "A" prüft, dass die Gruppe "Versicherte" in der Allowliste gesetzt ist
    Und "A" loggt sich im TI-Messenger aus
    Und "A" loggt sich im TI-Messenger ein
    Und "A" prüft, ob der eigene Authorization Mode auf "BlockAll" gesetzt ist
    Und "A" prüft, dass die Gruppe "Versicherte" in der Allowliste gesetzt ist

    @Plugin:Filter(ApiName1.hasTag("proClient"))
    Beispiele:
      | ApiName1              |
      | RU2-Ref-Pract-SDK-HS4 |
