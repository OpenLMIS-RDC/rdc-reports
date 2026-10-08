1.3.0-SNAPSHOT (WIP)
==================

Improvements:
* [ODRC-148](https://openlmis.atlassian.net/browse/ODRC-148) Unified filters for the RDC Jasper reports and created them with fixed ids
* [ODRC-149](https://openlmis.atlassian.net/browse/ODRC-149) Change reports' date format to DD-MM-YYYY
* [ODRC-130](https://openlmis.atlassian.net/browse/ODRC-130) Native report translations aligned with core openlmis-report: shared ReportTranslationBundleProvider with deployment overrides, labels and page footers moved to the report.pattern/report.controls conventions, and the translation bundle synced with the Transifex source; corrected the French requisition report wording after QA (report title "Réquisition pour", full supply section "Produits à approvisionner", pack size column "Quantité par conditionnement")


New functionality:
* [MW-1449](https://openlmis.atlassian.net/browse/MW-1449): Ported the Superset guest token endpoint for embedded dashboards from core openlmis-report (SupersetService, guest-token endpoint gated by REPORTS_VIEW, embeddedUuid on dashboard reports).
* [ODRC-158](https://openlmis.atlassian.net/browse/ODRC-158) RDC Proof of Delivery printout with the shipped quantity in packs and in doses, wider quantity columns and header cells that grow together; the service fills it when fulfillment sends its Proof of Delivery template to /api/reports/generate

1.2.0 / 2026-06-09
==================

1.1.0 / 2026-04-22
==================

Improvements:
* [ODRC-24](https://openlmis.atlassian.net/browse/ODRC-24) Global header and translations implemented for native reports

1.0.0 / 2026-04-02
==================

* Initial release of rdc-report service

Improvements:
* [ODRC-24](https://openlmis.atlassian.net/browse/ODRC-24) Global header and translations implemented for reports