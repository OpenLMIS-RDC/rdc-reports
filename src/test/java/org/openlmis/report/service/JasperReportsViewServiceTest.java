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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.openlmis.report.domain.JasperTemplate;
import org.openlmis.report.exception.JasperReportViewException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({JasperReportsViewService.class, JasperFillManager.class, DataSource.class})
public class JasperReportsViewServiceTest {

  private static final String FORMAT_PARAM = "format";

  @Mock
  private JasperCsvExporter jasperCsvExporter;

  @Mock
  private JasperXlsExporter jasperXlsExporter;

  @Mock
  private JasperXlsxExporter jasperXlsxExporter;

  @Mock
  private JasperHtmlExporter jasperHtmlExporter;

  @Mock
  private JasperPdfExporter jasperPdfExporter;

  @Mock
  private ObjectInputStream inputStream;

  @Mock
  private DataSource replicationDataSource;

  @Spy
  private final JasperTemplate jasperTemplate = new JasperTemplate();

  @Spy
  @InjectMocks
  private final JasperReportsViewService viewService = new JasperReportsViewService();

  @Before
  public void init() throws Exception {
    initializeExporterMocks();
    whenNew(ObjectInputStream.class).withAnyArguments().thenReturn(inputStream);
    whenNew(ByteArrayInputStream.class).withAnyArguments().thenReturn(null);
    mockStatic(JasperFillManager.class);
    when(JasperFillManager.fillReport(any(InputStream.class), any(), any(Connection.class)))
        .thenReturn(new JasperPrint());
  }

  @Test
  public void shouldSelectCsvExporterForCsvFormat() throws Exception {
    viewService.getJasperReportsView(jasperTemplate, getParamsWithFormat("csv"));
    verify(jasperCsvExporter, times(1)).exportReport();
  }

  @Test
  public void shouldSelectPdfExporterForPdfFormat() throws Exception {
    viewService.getJasperReportsView(jasperTemplate, getParamsWithFormat("pdf"));
    verify(jasperPdfExporter, times(1)).exportReport();
  }

  @Test
  public void shouldSelectXlsExporterForXlsFormat() throws Exception {
    viewService.getJasperReportsView(jasperTemplate, getParamsWithFormat("xls"));
    verify(jasperXlsExporter, times(1)).exportReport();
  }

  @Test
  public void shouldSelectHtmlExporterForHtmlFormat() throws Exception {
    viewService.getJasperReportsView(jasperTemplate, getParamsWithFormat("html"));
    verify(jasperHtmlExporter, times(1)).exportReport();
  }

  @Test
  public void shouldSelectXlsxExporterForXlsxFormat() throws Exception {
    viewService.getJasperReportsView(jasperTemplate, getParamsWithFormat("xlsx"));
    verify(jasperXlsxExporter, times(1)).exportReport();
  }

  @Test(expected = JasperReportViewException.class)
  public void shouldThrowJasperReportViewExceptionWhenConnectionCantBeOpen() throws Exception {
    when(replicationDataSource.getConnection()).thenThrow(new SQLException());
    viewService.getJasperReportsView(jasperTemplate, getParamsWithFormat("pdf"));
  }

  private Map<String, Object> getParamsWithFormat(String format) {
    Map<String, Object> params = new HashMap<>();
    params.put(FORMAT_PARAM, format);
    return params;
  }

  private void initializeExporterMocks() throws Exception {
    whenNew(JasperCsvExporter.class).withAnyArguments().thenReturn(jasperCsvExporter);
    whenNew(JasperXlsExporter.class).withAnyArguments().thenReturn(jasperXlsExporter);
    whenNew(JasperHtmlExporter.class).withAnyArguments().thenReturn(jasperHtmlExporter);
    whenNew(JasperPdfExporter.class).withAnyArguments().thenReturn(jasperPdfExporter);
    whenNew(JasperXlsxExporter.class).withAnyArguments().thenReturn(jasperXlsxExporter);
  }
}
