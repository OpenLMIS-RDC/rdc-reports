/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2020 VillageReach
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.junit.Before;
import org.junit.Test;

public abstract class BaseJasperExporterTest {

  protected static final String FORMAT_PARAM = "format";
  protected JasperPrint jasperPrint;

  @Before
  public void setUp() throws Exception {
    try (InputStream inputStream = getClass().getResourceAsStream("/example-report.jrxml")) {
      assert inputStream != null;
      assertNotNull(inputStream.toString(), inputStream);

      JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
      jasperPrint = JasperFillManager.fillReport(jasperReport,
          getParamsWithFormat(getReportFormat()), new JREmptyDataSource());
    }
  }

  @Test
  public void shouldGenerateBytesFromReport() throws JRException {
    JasperExporter exporter = getExporter();
    byte[] result = exporter.exportReport();
    assertTrue("Generated report is empty: " + Arrays.toString(result), result.length > 0);
  }

  protected Map<String, Object> getParamsWithFormat(String format) {
    Map<String, Object> params = new HashMap<>();
    params.put(FORMAT_PARAM, format);
    return params;
  }

  protected abstract JasperExporter getExporter();

  protected abstract String getReportFormat();
}
