#!/bin/sh

# Sync source strings with Transifex, then build.
#
# This service has TWO source resources (backend "messages" and the JasperReports
# "report-translations" bundle) in one project, so it can't use the shared
# /transifex/sync_transifex.sh helper - that helper hardcodes a single "messages"
# resource per project. We drive the tx client directly instead.

TX_PUSH=${TRANSIFEX_PUSH:-false}
TX_PULL=${TRANSIFEX_PULL:-true}

add_resource() {
  tx add --organization=openlmis --project=openlmis-report --type=UNICODEPROPERTIES \
    --resource="$1" --file-filter="$2" "$3"
}

rm -rf .tx
tx init
add_resource messages \
  'src/main/resources/messages_<lang>.properties' \
  src/main/resources/messages_en.properties
add_resource report-translations \
  'src/main/resources/resourceBundles/report_translations_<lang>.properties' \
  src/main/resources/resourceBundles/report_translations.properties

[ "$TX_PUSH" = true ] && tx push -s
[ "$TX_PULL" = true ] && tx pull -a -f

gradle clean build
