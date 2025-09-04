# language: de
@File:FeatureFile_00_01_V2_Basis @Ctl:UseCaseV2_00_Basis @PRODUKT:TI-M_FD_Basis @Zul:Pro @Zul:ProKK @AFO-ID:A_25641-01 @NB:NEIN
Funktionalität: 00 (1) Abfragen der Schnittstellen-Information und die Geräte-Liste aller Schnittstellen
  Die folgenden Testfälle sollen die Funktionalität der Abfrage der Schnittstellen und Geräte Informationen sicherstellen,
  die nicht weiter in der Spezifikationen des TI-Messengers beschrieben sind.

  COMMENT: Basis
  A_25641-01 - Matrix Spezifikationsversion

  Inhalt
  TF 1 - 2 Schnittstellen-Information
  TF 3 - 4 Geräte-Liste
  TF 5     Matrix Version prüfen

  @Ctl:SchnittstellenDaten @TCID:TIM_V2_BASIS_AF_000101 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 00.01.01 Abfragen der Schnittstellen-Information durch Pro-User-Client
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
    Wenn "A" Informationen über den Testtreiber anfragt
    Dann werden Informationen über den Testtreiber an "A" zurückgegeben

    @Plugin:Filter(ApiName1.hasTag("proClient")||(ApiName1.hasTag("proClient")&&ApiName1.hasTag("practitioner")))
    Beispiele:
      | ApiName1              |
      | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:SchnittstellenDaten @TCID:TIM_V2_BASIS_AF_000102 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 00.01.02 Abfragen der Schnittstellen-Information durch Org-Admin-Client
    Angenommen Es werden folgende Clients reserviert:
      | A | ORG_ADMIN | <ApiName1> |
    Wenn "A" Informationen über den Testtreiber anfragt
    Dann werden Informationen über den Testtreiber an "A" zurückgegeben

    @Plugin:Filter(ApiName1.hasTag("orgAdmin"))
    Beispiele:
      | ApiName1            |
      | RU2-Ref-ORG-Web-HS4 |

  @Ctl:SchnittstellenDaten @TCID:TIM_V2_BASIS_AF_000103 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 00.01.03 Abfragen der Geräte-Liste durch Pro-User-Client
    Angenommen Es werden folgende Clients reserviert:
      | A | PRO_CLIENT | <ApiName1> |
    Wenn die Liste der Geräte abgerufen wird
    Dann prüfe ob "A" ein Gerät reserviert hat

    @Plugin:Filter(ApiName1.hasTag("proClient")||(ApiName1.hasTag("proClient")&&ApiName1.hasTag("practitioner")))
    Beispiele:
      | ApiName1              |
      | RU2-Ref-Pract-SDK-HS4 |

  @Ctl:SchnittstellenDaten @TCID:TIM_V2_BASIS_AF_000104 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 00.01.04 Abfragen der Geräte-Liste durch Org-Admin-Client
    Angenommen Es werden folgende Clients reserviert:
      | A | ORG_ADMIN | <ApiName1> |
    Wenn die Liste der Geräte abgerufen wird
    Dann prüfe ob "A" ein Gerät reserviert hat

    @Plugin:Filter(ApiName1.hasTag("orgAdmin"))
    Beispiele:
      | ApiName1            |
      | RU2-Ref-ORG-Web-HS4 |

  @Ctl:SchnittstellenDaten @TCID:TIM_V2_BASIS_AF_000105 @PRIO:1 @TESTFALL:Positiv @STATUS:Implementiert
  Szenariogrundriss: 00.01.05 Abfrage der Matrix-Server-Version
    Angenommen A fragt an Schnittstelle <ApiName1> die Home-Server-Adresse ab
    Wenn A die supporteten Matrix-Versions vom Home-Server abfragt
    Dann ist die unterstützte Matrix-Version "1.11"

    @Plugin:Filter(ApiName1.hasTag("proClient")||(ApiName1.hasTag("proClient")&&ApiName1.hasTag("practitioner")))
    Beispiele:
      | ApiName1              |
      | RU2-Ref-Pract-SDK-HS4 |
