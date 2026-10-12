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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Provides the JasperReports translation bundle (the {@code $R{...}} keys used in report templates)
 * for a given locale. The Transifex-managed base bundle ships in the service classpath and is
 * merged with an optional deployment-specific override mounted under
 * {@value #DEPLOYMENT_BUNDLE_DIR}. A deployment override wins only for keys whose value actually
 * differs from the shipped English source - this lets a deployment change specific labels (in any
 * locale) while a leftover full English copy in the override directory cannot mask the classpath
 * translations for non-English locales. Bundles are resolved without the JVM default-locale
 * fallback, so a locale with no translation file deterministically falls back to the English base
 * bundle regardless of the locale the container happens to run under. The merged bundle is cached
 * per locale and only rebuilt on redeploy/restart.
 */
@Component
public class ReportTranslationBundleProvider {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ReportTranslationBundleProvider.class);

  private static final String RESOURCE_BUNDLE_BASE_NAME = "report_translations";
  private static final String RESOURCE_BUNDLE_CLASSPATH = "resourceBundles/report_translations";
  private static final String DEPLOYMENT_BUNDLE_DIR = "/config/reports/resourceBundles";

  // no JVM default-locale fallback: an unsupported locale falls straight to the base
  // (English) properties file instead of whatever locale the JVM happens to default to
  private static final ResourceBundle.Control BUNDLE_CONTROL =
      ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES);

  private final Map<Locale, ResourceBundle> cache = new ConcurrentHashMap<>();

  /**
   * Returns the merged translation bundle for the given locale, or {@code null} if no translations
   * are available at all.
   *
   * @param locale the requested locale
   * @return the merged (classpath + deployment override) bundle, or {@code null} when none exists
   */
  public ResourceBundle getBundle(Locale locale) {
    return cache.computeIfAbsent(locale, this::buildBundle);
  }

  private ResourceBundle buildBundle(Locale locale) {
    ResourceBundle classpath = loadClasspathBundle(locale);
    ResourceBundle overrides = loadDeploymentBundle(locale);

    if (overrides == null) {
      return classpath;
    }
    if (classpath == null) {
      return overrides;
    }
    return mergeBundles(classpath, overrides, loadClasspathBundle(Locale.ROOT), locale);
  }

  /**
   * Load the deployment-specific override bundle from the mounted config directory, if present.
   *
   * @return the override bundle, or {@code null} when the directory or bundle is absent
   */
  private ResourceBundle loadDeploymentBundle(Locale locale) {
    File resourceBundleDir = new File(DEPLOYMENT_BUNDLE_DIR);

    if (resourceBundleDir.exists() && resourceBundleDir.isDirectory()) {
      try {
        URL[] urls = {resourceBundleDir.toURI().toURL()};
        try (URLClassLoader externalLoader = new URLClassLoader(urls)) {
          return ResourceBundle.getBundle(
              RESOURCE_BUNDLE_BASE_NAME, locale, externalLoader, BUNDLE_CONTROL);
        }
      } catch (IOException | MissingResourceException e) {
        return null;
      }
    }

    return null;
  }

  /**
   * Load the base translations bundle bundled in the service classpath (Transifex-managed).
   *
   * @return the base bundle, or {@code null} when it is absent
   */
  private ResourceBundle loadClasspathBundle(Locale locale) {
    try {
      return ResourceBundle.getBundle(RESOURCE_BUNDLE_CLASSPATH, locale, BUNDLE_CONTROL);
    } catch (MissingResourceException e) {
      return null;
    }
  }

  /**
   * Merge the classpath translations for the locale with the deployment override. A deployment
   * override value is applied only when it genuinely differs from the shipped English source
   * ({@code englishBase}). This lets a deployment change specific labels for every locale, while a
   * leftover full English copy in the override directory (a value identical to the English source)
   * is treated as "not a real override" so it cannot overwrite the classpath translation for a
   * non-English locale (e.g. English "Facility" must not mask Spanish "Establecimiento").
   */
  private ResourceBundle mergeBundles(ResourceBundle classpath, ResourceBundle overrides,
                                      ResourceBundle englishBase, Locale locale) {
    Map<String, Object> merged = new HashMap<>();
    for (String key : Collections.list(classpath.getKeys())) {
      merged.put(key, classpath.getObject(key));
    }
    OverrideSummary summary = classifyOverrides(overrides, englishBase);
    for (String key : summary.getApplied()) {
      merged.put(key, overrides.getObject(key));
    }
    logOverrides(locale, summary);
    return new MapResourceBundle(merged);
  }

  /**
   * Split the override bundle's keys into the ones that are applied over the classpath
   * translations and the ones that are discarded because they merely repeat the shipped English
   * source. Kept separate from the merge itself so the outcome can be asserted directly.
   */
  static OverrideSummary classifyOverrides(ResourceBundle overrides, ResourceBundle englishBase) {
    List<String> applied = new ArrayList<>();
    List<String> ignored = new ArrayList<>();

    for (String key : Collections.list(overrides.getKeys())) {
      if (isRealOverride(key, overrides, englishBase)) {
        applied.add(key);
      } else {
        ignored.add(key);
      }
    }
    Collections.sort(applied);
    Collections.sort(ignored);

    return new OverrideSummary(applied, ignored);
  }

  /**
   * Report what the deployment override directory actually did to the shipped translations. The
   * merged bundle is built once per locale, so this runs on the first report generated for that
   * locale after a restart. Both outcomes are worth surfacing: the applied keys are the
   * deployment's deliberate deviation from the Transifex base, and the ignored ones mean the
   * override directory holds a copy of the base bundle, which is usually a leftover that is one
   * release away from masking a base translation.
   */
  private void logOverrides(Locale locale, OverrideSummary summary) {
    if (!summary.getApplied().isEmpty()) {
      LOGGER.warn("Deployment report translation override for locale [{}] applied {} key(s): {}",
          locale, summary.getApplied().size(), summary.getApplied());
    }
    if (!summary.getIgnored().isEmpty()) {
      LOGGER.warn("Deployment report translation override for locale [{}] ignored {} key(s) whose "
              + "value is identical to the shipped English source - this is a leftover copy of the "
              + "base bundle and should be trimmed from the override directory (enable debug "
              + "logging on this class to list the keys)",
          locale, summary.getIgnored().size());
      LOGGER.debug("Ignored report translation override keys for locale [{}]: {}",
          locale, summary.getIgnored());
    }
  }

  /**
   * The outcome of comparing a deployment override bundle against the shipped English source.
   */
  static final class OverrideSummary {
    private final List<String> applied;
    private final List<String> ignored;

    OverrideSummary(List<String> applied, List<String> ignored) {
      this.applied = applied;
      this.ignored = ignored;
    }

    List<String> getApplied() {
      return applied;
    }

    List<String> getIgnored() {
      return ignored;
    }
  }

  private static boolean isRealOverride(String key, ResourceBundle overrides,
                                        ResourceBundle englishBase) {
    if (englishBase == null || !englishBase.containsKey(key)) {
      return true;
    }
    return !overrides.getObject(key).equals(englishBase.getObject(key));
  }

  /**
   * A {@link ResourceBundle} backed by an in-memory map, used to expose the merged base + override
   * translations to JasperReports through the report resource bundle parameter.
   */
  private static final class MapResourceBundle extends ResourceBundle {
    private final Map<String, Object> entries;

    MapResourceBundle(Map<String, Object> entries) {
      this.entries = entries;
    }

    @Override
    protected Object handleGetObject(String key) {
      return entries.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
      return Collections.enumeration(entries.keySet());
    }
  }
}
