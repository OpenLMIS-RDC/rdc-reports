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

package org.openlmis.report.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.openlmis.report.dto.DashboardReportDto;
import org.openlmis.report.utils.DashboardReportDataBuilder;

public class DashboardReportTest {
  @Test
  public void shouldCreateNewInstance() {
    DashboardReport dashboardReport = new DashboardReportDataBuilder().build();
    DashboardReportDto importer = DashboardReportDto.newInstance(dashboardReport);

    DashboardReport newInstance = DashboardReport.newInstance(importer);

    assertThat(importer.getName()).isEqualTo(newInstance.getName());
    assertThat(importer.getRightName()).isEqualTo((newInstance.getName() + "_RIGHT")
        .toUpperCase());
    assertThat(importer.getUrl()).isEqualTo(newInstance.getUrl());
    assertThat(importer.getType()).isEqualTo(newInstance.getType());
    assertThat(importer.isEnabled()).isEqualTo(newInstance.isEnabled());
    assertThat(importer.isShowOnHomePage()).isEqualTo(newInstance.isShowOnHomePage());
    assertThat(importer.getCategory().getId()).isEqualTo(newInstance.getCategory().getId());
  }

  @Test
  public void shouldExportData() {
    DashboardReport dashboardReport = new DashboardReportDataBuilder().build();
    DashboardReportDto dto = new DashboardReportDto();

    dashboardReport.export(dto);

    assertThat(dto.getName()).isEqualTo(dashboardReport.getName());
    assertThat(dto.getRightName()).isEqualTo((dashboardReport.getName() + "_RIGHT")
        .toUpperCase());
    assertThat(dto.getUrl()).isEqualTo(dashboardReport.getUrl());
    assertThat(dto.getType()).isEqualTo(dashboardReport.getType());
    assertThat(dto.isEnabled()).isEqualTo(dashboardReport.isEnabled());
    assertThat(dto.isShowOnHomePage()).isEqualTo(dashboardReport.isShowOnHomePage());
    assertThat(dto.getCategory().getId()).isEqualTo(dashboardReport.getCategory().getId());
  }

  @Test
  public void shouldHandleEnabledFlag() {
    DashboardReportDto dto = new DashboardReportDto();
    dto.setEnabled(true);

    DashboardReport dashboardReport = DashboardReport.newInstance(dto);

    assertThat(dashboardReport.isEnabled()).isTrue();

    dto.setEnabled(false);
    dashboardReport = DashboardReport.newInstance(dto);

    assertThat(dashboardReport.isEnabled()).isFalse();
  }

  @Test
  public void shouldBeEqualIfSameFields() {
    DashboardReportDto dto = new DashboardReportDto();
    dto.setName("Test Report");
    dto.setUrl("http://example.com");

    DashboardReport dashboardReport1 = DashboardReport.newInstance(dto);
    DashboardReport dashboardReport2 = DashboardReport.newInstance(dto);

    assertThat(dashboardReport1).isEqualTo(dashboardReport2);
    assertThat(dashboardReport1.hashCode()).isEqualTo(dashboardReport2.hashCode());
  }
}