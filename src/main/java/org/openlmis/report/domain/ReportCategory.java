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

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "report_categories")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReportCategory extends BaseEntity {

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION, nullable = false, unique = true)
  private String name;

  /**
   * Create a new instance of a Report category based on data.
   * from {@link ReportCategory.Importer}.
   *
   * @param importer instance of {@link ReportCategory.Importer}.
   * @return new instance of a report category.
   */
  public static ReportCategory newInstance(ReportCategory.Importer importer) {
    ReportCategory category = new ReportCategory();
    category.setId(importer.getId());
    category.updateFrom(importer);

    return category;
  }

  /**
   * Copy values of attributes into new or updated Report Category.
   *
   * @param importer Report category importer with new values.
   */
  public void updateFrom(ReportCategory.Importer importer) {
    this.name = importer.getName();
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to.
   */
  public void export(ReportCategory.Exporter exporter) {
    exporter.setId(id);
    exporter.setName(name);
  }

  public interface Exporter {
    void setId(UUID id);

    void setName(String name);
  }

  public interface Importer {
    UUID getId();

    String getName();
  }

}