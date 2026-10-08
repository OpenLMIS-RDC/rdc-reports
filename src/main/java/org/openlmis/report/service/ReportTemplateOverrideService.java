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

package org.openlmis.report.service;

import static org.openlmis.report.i18n.ReportingMessageKeys.ERROR_REPORTING_FILE_INVALID;
import static org.openlmis.report.i18n.ReportingMessageKeys.ERROR_REPORTING_IO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.openlmis.report.exception.ReportingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 * Replaces report templates that other OpenLMIS services send to /api/reports/generate with their
 * RDC versions. The sending service has already checked the user's rights by then, so the RDC
 * version must take the same parameters as the template it replaces.
 */
@Service
public class ReportTemplateOverrideService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReportTemplateOverrideService.class);

  // Keyed by the compiled report name, not the request name: fulfillment 9.3.2 sends the Proof of
  // Delivery under the request name of the order printout.
  static final Map<String, String> OVERRIDES = Collections.singletonMap(
      "proofOfDelivery", "reports/proof_of_delivery.jrxml");

  private final Map<String, byte[]> compiledOverrides = new ConcurrentHashMap<>();

  /**
   * Finds the RDC version of the given report.
   *
   * @param report the compiled report sent by another service, may be null
   * @return the serialized RDC version, or null if the report has none
   * @throws ReportingException if the RDC version cannot be compiled
   */
  public byte[] findOverride(JasperReport report) throws ReportingException {
    if (report == null) {
      return null;
    }

    String path = OVERRIDES.get(report.getName());
    if (path == null) {
      return null;
    }

    byte[] compiled = compiledOverrides.get(path);
    if (compiled == null) {
      compiled = compile(path);
      compiledOverrides.put(path, compiled);
    }
    return compiled;
  }

  private byte[] compile(String path) throws ReportingException {
    try (InputStream is = new ClassPathResource(path).getInputStream()) {
      JasperReport report = JasperCompileManager.compileReport(is);

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
        out.writeObject(report);
      }
      return bos.toByteArray();
    } catch (JRException ex) {
      LOGGER.error("Cannot compile the report template {}", path, ex);
      throw new ReportingException(ex, ERROR_REPORTING_FILE_INVALID);
    } catch (IOException ex) {
      LOGGER.error("Cannot read the report template {}", path, ex);
      throw new ReportingException(ex, ERROR_REPORTING_IO, ex.getMessage());
    }
  }
}
