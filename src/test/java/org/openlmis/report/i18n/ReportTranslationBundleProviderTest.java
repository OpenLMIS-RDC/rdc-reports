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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({
    ReportTranslationBundleProvider.class,
    ResourceBundle.class
})
public class ReportTranslationBundleProviderTest {

  private static final String RESOURCE_BUNDLE_NAME = "report_translations";
  private static final String RESOURCE_BUNDLE_CLASSPATH = "resourceBundles/report_translations";
  private static final String RESOURCE_BUNDLE_KEY = "resource_bundle_key";
  private static final String SHARED_KEY = "shared.key";
  private static final String DEPLOYMENT_DIR = "/config/reports/resourceBundles";
  private static final String DUMMY_FILE_URI = "file://dummy";
  private static final String MISSING = "missing";
  private static final String ESTABLECIMIENTO = "Establecimiento";
  private static final String GLOBAL_HEADER_TITLE = "report.globalHeader.title";

  private final ReportTranslationBundleProvider provider = new ReportTranslationBundleProvider();

  @Test
  public void getBundleShouldReturnNullWhenDeploymentDirMissingAndClasspathMissing()
      throws Exception {
    File mockDir = mock(File.class);
    whenNew(File.class).withArguments(DEPLOYMENT_DIR).thenReturn(mockDir);
    when(mockDir.exists()).thenReturn(false);

    mockStatic(ResourceBundle.class);
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_CLASSPATH), any(Locale.class)))
        .thenThrow(
            new MissingResourceException(MISSING, RESOURCE_BUNDLE_NAME, RESOURCE_BUNDLE_KEY));

    assertNull(provider.getBundle(Locale.ENGLISH));
  }

  @Test
  public void getBundleShouldFallBackToClasspathWhenDeploymentDirNotFound() throws Exception {
    File mockDir = mock(File.class);
    whenNew(File.class).withArguments(DEPLOYMENT_DIR).thenReturn(mockDir);
    when(mockDir.exists()).thenReturn(false);

    ResourceBundle classpathBundle = mock(ResourceBundle.class);
    mockStatic(ResourceBundle.class);
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_CLASSPATH), any(Locale.class)))
        .thenReturn(classpathBundle);

    assertEquals(classpathBundle, provider.getBundle(Locale.ENGLISH));
  }

  @Test
  public void getBundleShouldFallBackToClasspathWhenDeploymentBundleNotFound() throws Exception {
    File mockDir = mock(File.class);
    whenNew(File.class).withArguments(DEPLOYMENT_DIR).thenReturn(mockDir);
    when(mockDir.exists()).thenReturn(true);
    when(mockDir.isDirectory()).thenReturn(true);
    when(mockDir.toURI()).thenReturn(new java.net.URI(DUMMY_FILE_URI));

    ResourceBundle classpathBundle = mock(ResourceBundle.class);
    mockStatic(ResourceBundle.class);
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_NAME), any(Locale.class),
        any(URLClassLoader.class)))
        .thenThrow(
            new MissingResourceException(MISSING, RESOURCE_BUNDLE_NAME, RESOURCE_BUNDLE_KEY));
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_CLASSPATH), any(Locale.class)))
        .thenReturn(classpathBundle);

    assertEquals(classpathBundle, provider.getBundle(Locale.FRENCH));
  }

  @Test
  public void getBundleShouldReturnNullWhenDeploymentAndClasspathMissing() throws Exception {
    File mockDir = mock(File.class);
    whenNew(File.class).withArguments(DEPLOYMENT_DIR).thenReturn(mockDir);
    when(mockDir.exists()).thenReturn(true);
    when(mockDir.isDirectory()).thenReturn(true);
    when(mockDir.toURI()).thenReturn(new java.net.URI(DUMMY_FILE_URI));

    mockStatic(ResourceBundle.class);
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_NAME), any(Locale.class),
        any(URLClassLoader.class)))
        .thenThrow(
            new MissingResourceException(MISSING, RESOURCE_BUNDLE_NAME, RESOURCE_BUNDLE_KEY));
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_CLASSPATH), any(Locale.class)))
        .thenThrow(
            new MissingResourceException(MISSING, RESOURCE_BUNDLE_NAME, RESOURCE_BUNDLE_KEY));

    assertNull(provider.getBundle(Locale.ENGLISH));
  }

  @Test
  public void getBundleShouldMergeDeploymentOverClasspathWithDeploymentPrecedence()
      throws Exception {
    File mockDir = mock(File.class);
    whenNew(File.class).withArguments(DEPLOYMENT_DIR).thenReturn(mockDir);
    when(mockDir.exists()).thenReturn(true);
    when(mockDir.isDirectory()).thenReturn(true);
    when(mockDir.toURI()).thenReturn(new java.net.URI(DUMMY_FILE_URI));

    Map<String, Object> classpathEntries = new HashMap<>();
    classpathEntries.put(SHARED_KEY, "classpath value");
    classpathEntries.put("classpath.only", "classpath only value");
    Map<String, Object> deploymentEntries = new HashMap<>();
    deploymentEntries.put(SHARED_KEY, "deployment value");
    deploymentEntries.put("deployment.only", "deployment only value");

    mockStatic(ResourceBundle.class);
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_NAME), any(Locale.class),
        any(URLClassLoader.class))).thenReturn(bundleOf(deploymentEntries));
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_CLASSPATH), any(Locale.class)))
        .thenReturn(bundleOf(classpathEntries));

    ResourceBundle merged = provider.getBundle(Locale.FRENCH);

    // deployment key wins on collision (differs from the English source)
    assertEquals("deployment value", merged.getString(SHARED_KEY));
    // classpath-only key is preserved
    assertEquals("classpath only value", merged.getString("classpath.only"));
    // deployment-only key (absent from the English source) is added
    assertEquals("deployment only value", merged.getString("deployment.only"));
  }

  @Test
  public void getBundleShouldNotApplyOverrideEqualToEnglishSource() throws Exception {
    // A leftover full English copy in the override dir (value identical to the English source) must
    // not overwrite the classpath translation for a non-English locale.
    final Locale spanish = new Locale("es");
    File mockDir = mock(File.class);
    whenNew(File.class).withArguments(DEPLOYMENT_DIR).thenReturn(mockDir);
    when(mockDir.exists()).thenReturn(true);
    when(mockDir.isDirectory()).thenReturn(true);
    when(mockDir.toURI()).thenReturn(new java.net.URI(DUMMY_FILE_URI));

    Map<String, Object> spanishEntries = new HashMap<>();
    spanishEntries.put(SHARED_KEY, ESTABLECIMIENTO);
    Map<String, Object> englishEntries = new HashMap<>();
    englishEntries.put(SHARED_KEY, "Facility");
    Map<String, Object> overrideEntries = new HashMap<>();
    overrideEntries.put(SHARED_KEY, "Facility"); // leftover English copy, not a real override

    mockStatic(ResourceBundle.class);
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_CLASSPATH), eq(spanish)))
        .thenReturn(bundleOf(spanishEntries));
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_CLASSPATH), eq(Locale.ROOT)))
        .thenReturn(bundleOf(englishEntries));
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_NAME), eq(spanish),
        any(URLClassLoader.class))).thenReturn(bundleOf(overrideEntries));

    ResourceBundle merged = provider.getBundle(spanish);

    // classpath Spanish is preserved, not clobbered by the English-valued override
    assertEquals(ESTABLECIMIENTO, merged.getString(SHARED_KEY));
  }

  @Test
  public void getBundleShouldApplyGenuineOverrideForNonEnglishLocale() throws Exception {
    // A real deployment override (value differs from the English source) applies to every locale.
    final Locale spanish = new Locale("es");
    File mockDir = mock(File.class);
    whenNew(File.class).withArguments(DEPLOYMENT_DIR).thenReturn(mockDir);
    when(mockDir.exists()).thenReturn(true);
    when(mockDir.isDirectory()).thenReturn(true);
    when(mockDir.toURI()).thenReturn(new java.net.URI(DUMMY_FILE_URI));

    Map<String, Object> spanishEntries = new HashMap<>();
    spanishEntries.put(GLOBAL_HEADER_TITLE, "OpenLMIS");
    spanishEntries.put(SHARED_KEY, ESTABLECIMIENTO);
    Map<String, Object> englishEntries = new HashMap<>();
    englishEntries.put(GLOBAL_HEADER_TITLE, "OpenLMIS");
    englishEntries.put(SHARED_KEY, "Facility");
    Map<String, Object> overrideEntries = new HashMap<>();
    overrideEntries.put(GLOBAL_HEADER_TITLE, "OpenLMIS TEST TITLE");

    mockStatic(ResourceBundle.class);
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_CLASSPATH), eq(spanish)))
        .thenReturn(bundleOf(spanishEntries));
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_CLASSPATH), eq(Locale.ROOT)))
        .thenReturn(bundleOf(englishEntries));
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_NAME), eq(spanish),
        any(URLClassLoader.class))).thenReturn(bundleOf(overrideEntries));

    ResourceBundle merged = provider.getBundle(spanish);

    // genuine override applied even for a non-English locale...
    assertEquals("OpenLMIS TEST TITLE", merged.getString(GLOBAL_HEADER_TITLE));
    // ...while the classpath Spanish translation for other keys is preserved
    assertEquals(ESTABLECIMIENTO, merged.getString(SHARED_KEY));
  }

  @Test
  public void getBundleShouldCacheMergedBundlePerLocale() throws Exception {
    File mockDir = mock(File.class);
    whenNew(File.class).withArguments(DEPLOYMENT_DIR).thenReturn(mockDir);
    when(mockDir.exists()).thenReturn(true);
    when(mockDir.isDirectory()).thenReturn(true);
    when(mockDir.toURI()).thenReturn(new java.net.URI(DUMMY_FILE_URI));

    Map<String, Object> entries = new HashMap<>();
    entries.put(SHARED_KEY, "value");

    mockStatic(ResourceBundle.class);
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_NAME), any(Locale.class),
        any(URLClassLoader.class))).thenReturn(bundleOf(entries));
    when(ResourceBundle.getBundle(eq(RESOURCE_BUNDLE_CLASSPATH), any(Locale.class)))
        .thenReturn(bundleOf(entries));

    provider.getBundle(Locale.FRENCH);
    provider.getBundle(Locale.FRENCH);

    // second call for the same locale is served from cache - the deployment lookup runs only once
    verifyStatic(ResourceBundle.class, times(1));
    ResourceBundle.getBundle(
        eq(RESOURCE_BUNDLE_NAME), any(Locale.class), any(URLClassLoader.class));
  }

  private static ResourceBundle bundleOf(Map<String, Object> entries) {
    return new ResourceBundle() {
      @Override
      protected Object handleGetObject(String key) {
        return entries.get(key);
      }

      @Override
      public Enumeration<String> getKeys() {
        return Collections.enumeration(entries.keySet());
      }
    };
  }
}
