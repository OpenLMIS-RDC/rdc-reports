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
import org.openlmis.report.dto.ReportCategoryDto;
import org.openlmis.report.utils.ReportCategoryDataBuilder;

public class ReportCategoryTest {

  @Test
  public void shouldCreateNewInstance() {
    ReportCategory reportCategory = new ReportCategoryDataBuilder().build();
    ReportCategoryDto importer = ReportCategoryDto.newInstance(reportCategory);
    ReportCategory newInstance = ReportCategory.newInstance(importer);

    assertThat(importer.getName()).isEqualTo(newInstance.getName());
  }

  @Test
  public void shouldExportData() {
    ReportCategory reportCategory = new ReportCategoryDataBuilder().build();
    ReportCategoryDto dto = new ReportCategoryDto();
    reportCategory.export(dto);

    assertThat(dto.getName()).isEqualTo(reportCategory.getName());
  }

  @Test
  public void shouldBeEqualIfSameFields() {
    ReportCategoryDto dto = new ReportCategoryDto();
    dto.setName("Test Category");

    ReportCategory reportCategory1 = ReportCategory.newInstance(dto);
    ReportCategory reportCategory2 = ReportCategory.newInstance(dto);

    assertThat(reportCategory1).isEqualTo(reportCategory2);
    assertThat(reportCategory1.hashCode()).isEqualTo(reportCategory2.hashCode());
  }
}
