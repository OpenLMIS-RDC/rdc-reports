/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.report.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.apache.commons.io.IOUtils;

/**
 * Loads {@code .properties} bundles as UTF-8 and, like
 * {@link ResourceBundle.Control#getNoFallbackControl}, without the JVM default-locale fallback.
 * Java 8 reads {@code .properties} bundles as ISO-8859-1, while Transifex exports a translation
 * typed in its editor as raw UTF-8. A file that is not valid UTF-8 is read as ISO-8859-1, so
 * bundles saved in either encoding load correctly, and Unicode escape sequences work in both.
 * Java 9 and later also read these bundles as UTF-8 with an ISO-8859-1 fallback, so after an
 * upgrade the JDK's own no-fallback control can take over from this class.
 */
class Utf8PropertiesControl extends ResourceBundle.Control {

  @Override
  public List<String> getFormats(String baseName) {
    return FORMAT_PROPERTIES;
  }

  @Override
  public Locale getFallbackLocale(String baseName, Locale locale) {
    return null;
  }

  @Override
  public ResourceBundle newBundle(String baseName, Locale locale, String format,
                                  ClassLoader loader, boolean reload) throws IOException {
    String resourceName = toResourceName(toBundleName(baseName, locale), "properties");
    try (InputStream stream = loader.getResourceAsStream(resourceName)) {
      if (stream == null) {
        return null;
      }
      return new PropertyResourceBundle(new StringReader(decode(IOUtils.toByteArray(stream))));
    }
  }

  private static String decode(byte[] content) {
    try {
      return StandardCharsets.UTF_8.newDecoder()
          .onMalformedInput(CodingErrorAction.REPORT)
          .onUnmappableCharacter(CodingErrorAction.REPORT)
          .decode(ByteBuffer.wrap(content))
          .toString();
    } catch (CharacterCodingException e) {
      return new String(content, StandardCharsets.ISO_8859_1);
    }
  }
}
