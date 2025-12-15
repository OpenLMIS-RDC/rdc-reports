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

package org.openlmis.report.utils;

import java.util.UUID;
import org.apache.commons.lang.RandomStringUtils;
import org.openlmis.report.domain.ReportCategory;

public class ReportCategoryDataBuilder {
  private final UUID id = UUID.randomUUID();
  private String name = RandomStringUtils.random(6);

  public ReportCategoryDataBuilder withName(String name) {
    this.name = name;
    return this;
  }

  /**
   * Builds an instance of the {@link ReportCategory} class with populated ID.
   *
   * @return the instance of {@link ReportCategory} class
   */
  public ReportCategory build() {
    ReportCategory reportCategory = buildAsNew();
    reportCategory.setId(id);
    return reportCategory;
  }

  /**
   * Build an instance of the {@link ReportCategory} class without ID field populated.
   *
   * @return the instance of {@link ReportCategory} class
   */
  public ReportCategory buildAsNew() {
    return new ReportCategory(name);
  }

}