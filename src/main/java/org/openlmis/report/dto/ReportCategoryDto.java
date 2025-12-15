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

package org.openlmis.report.dto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.report.domain.ReportCategory;
import org.openlmis.report.domain.ReportCategory.Exporter;
import org.openlmis.report.domain.ReportCategory.Importer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportCategoryDto implements Importer, Exporter {
  private UUID id;
  private String name;

  /**
   * Create new instance of a ReportCategoryDto based on given {@link ReportCategory}.
   *
   * @param reportCategory instance of a Report Category.
   * @return new instance of EmbeddedReportCategoryDto.
   */
  public static ReportCategoryDto newInstance(ReportCategory reportCategory) {
    if (reportCategory == null) {
      return null;
    }

    ReportCategoryDto reportCategoryDto = new ReportCategoryDto();
    reportCategory.export(reportCategoryDto);

    return reportCategoryDto;
  }

  /**
   * Create new list of a ReportCategoryDto based on given list of {@link ReportCategory}.
   *
   * @param reportCategories list of {@link ReportCategory}.
   * @return new list of ReportCategoryDto.
   */
  public static List<ReportCategoryDto> newInstance(Iterable<ReportCategory> reportCategories) {
    return StreamSupport.stream(reportCategories.spliterator(), false)
      .map(ReportCategoryDto::newInstance)
      .collect(Collectors.toList());
  }
}