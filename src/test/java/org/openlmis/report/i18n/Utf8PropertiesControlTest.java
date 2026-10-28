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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Utf8PropertiesControlTest {

  private static final String BASE_NAME = "report_translations";
  private static final String BASE_FILE = "report_translations.properties";
  private static final String FRENCH_FILE = "report_translations_fr.properties";
  private static final String STATUS_KEY = "report.value.status.released";
  private static final String COLUMN_KEY = "report.column.adjustedConsumption";

  // built from a char constant so that no string literal holds a non-ASCII character: the build
  // sets no source encoding
  private static final char E_ACUTE = 0xE9;
  private static final String RELEASED = "Lib" + E_ACUTE + "r" + E_ACUTE + "e";
  private static final String ADJUSTED_CONSUMPTION = "Consommation ajust" + E_ACUTE + "e";
  // E_ACUTE written as a Unicode escape sequence, the way the escaped values sit in the file
  private static final String ESCAPED_E_ACUTE = "\\" + "u00e9";

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  private URLClassLoader loader;

  @Before
  public void setUp() throws IOException {
    URL[] urls = {folder.getRoot().toURI().toURL()};
    loader = new URLClassLoader(urls, null);
  }

  @After
  public void tearDown() throws IOException {
    ResourceBundle.clearCache(loader);
    loader.close();
  }

  @Test
  public void shouldReadRawUtf8ValuesAndUnicodeEscapesFromTheSameFile() throws IOException {
    // the shape of a Transifex export: a value entered in the editor comes out as raw UTF-8,
    // a value uploaded with Unicode escapes comes out with the escapes
    write(FRENCH_FILE, StandardCharsets.UTF_8,
        entry(STATUS_KEY, RELEASED),
        entry(COLUMN_KEY, "Consommation ajust" + ESCAPED_E_ACUTE + "e"));

    ResourceBundle bundle = load(Locale.FRENCH);

    assertEquals(RELEASED, bundle.getString(STATUS_KEY));
    assertEquals(ADJUSTED_CONSUMPTION, bundle.getString(COLUMN_KEY));
  }

  @Test
  public void shouldReadFileThatIsNotValidUtf8AsIso88591() throws IOException {
    write(FRENCH_FILE, StandardCharsets.ISO_8859_1, entry(STATUS_KEY, RELEASED));

    assertEquals(RELEASED, load(Locale.FRENCH).getString(STATUS_KEY));
  }

  @Test
  public void shouldFallBackToBaseBundleInsteadOfJvmDefaultLocale() throws IOException {
    write(BASE_FILE, StandardCharsets.UTF_8, entry(STATUS_KEY, "Released"));
    write(FRENCH_FILE, StandardCharsets.UTF_8, entry(STATUS_KEY, RELEASED));

    Locale jvmDefault = Locale.getDefault();
    Locale.setDefault(Locale.FRENCH);
    try {
      assertEquals("Released", load(Locale.GERMAN).getString(STATUS_KEY));
    } finally {
      Locale.setDefault(jvmDefault);
    }
  }

  @Test(expected = MissingResourceException.class)
  public void shouldThrowMissingResourceExceptionWhenNoBundleFileExists() {
    load(Locale.FRENCH);
  }

  private ResourceBundle load(Locale locale) {
    return ResourceBundle.getBundle(BASE_NAME, locale, loader, new Utf8PropertiesControl());
  }

  private void write(String fileName, Charset charset, String... lines) throws IOException {
    Files.write(new File(folder.getRoot(), fileName).toPath(),
        String.join("\n", lines).getBytes(charset));
  }

  private static String entry(String key, String value) {
    return key + "=" + value;
  }
}
