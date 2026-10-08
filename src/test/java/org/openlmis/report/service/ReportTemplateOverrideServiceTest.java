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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.junit.Test;

public class ReportTemplateOverrideServiceTest {
  private static final String PROOF_OF_DELIVERY = "proofOfDelivery";

  private final ReportTemplateOverrideService service = new ReportTemplateOverrideService();

  @Test
  public void shouldReturnNullWithoutReport() throws Exception {
    assertNull(service.findOverride(null));
  }

  @Test
  public void shouldReturnNullForReportWithoutOverride() throws Exception {
    assertNull(service.findOverride(reportNamed("order")));
    assertNull(service.findOverride(reportNamed(null)));
  }

  @Test
  public void shouldCompileProofOfDeliveryOverrideOnce() throws Exception {
    byte[] first = service.findOverride(reportNamed(PROOF_OF_DELIVERY));
    byte[] second = service.findOverride(reportNamed(PROOF_OF_DELIVERY));

    assertNotNull(first);
    assertSame(first, second);
  }

  @Test
  public void shouldTakeTheParametersOfTheCoreProofOfDeliveryTemplate() throws Exception {
    JasperReport override = load(service.findOverride(reportNamed(PROOF_OF_DELIVERY)));

    assertEquals(PROOF_OF_DELIVERY, override.getName());
    Map<String, JRParameter> parameters = Arrays.stream(override.getParameters())
        .filter(parameter -> !parameter.isSystemDefined())
        .collect(Collectors.toMap(JRParameter::getName, Function.identity()));
    assertEquals(String.class, parameters.get("id").getValueClass());
    assertTrue(parameters.containsKey("headerTemplate"));
    assertTrue(parameters.containsKey("decimalFormat"));
    assertFalse(parameters.containsKey("userId"));
    assertTrue(override.getQuery().getText().contains("where pod.id = $P{id}::uuid"));
  }

  @Test
  public void shouldFillProofOfDeliveryOverrideThroughReportsViewService() throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("format", "pdf");
    params.put("datasource", new JREmptyDataSource(0));
    params.put("dateFormat", "dd/MM/yyyy");
    params.put("dateTimeFormat", "dd/MM/yyyy HH:mm:ss");
    params.put("timeZoneId", "UTC");
    params.put("decimalFormat", new DecimalFormat());

    byte[] pdf = new JasperReportsViewService()
        .getJasperReportsView(service.findOverride(reportNamed(PROOF_OF_DELIVERY)), params);

    assertEquals("%PDF", new String(pdf, 0, 4, StandardCharsets.US_ASCII));
  }

  @Test
  public void shouldKeepTheOverrideMappingInSyncWithTheTemplateFiles() throws Exception {
    for (Map.Entry<String, String> entry : ReportTemplateOverrideService.OVERRIDES.entrySet()) {
      JasperReport override = load(service.findOverride(reportNamed(entry.getKey())));
      assertEquals(entry.getKey(), override.getName());
    }
  }

  private static JasperReport reportNamed(String name) {
    JasperReport report = mock(JasperReport.class);
    when(report.getName()).thenReturn(name);
    return report;
  }

  private static JasperReport load(byte[] data) throws Exception {
    try (InputStream is = new ByteArrayInputStream(data)) {
      return (JasperReport) JRLoader.loadObject(is);
    }
  }
}
