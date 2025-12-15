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

import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report_images")
@NoArgsConstructor
@AllArgsConstructor
public class ReportImage extends BaseEntity {

  @Column(columnDefinition = TEXT_COLUMN_DEFINITION, unique = true, nullable = false)
  @Getter
  @Setter
  private String name;

  @Column
  @Getter
  @Setter
  private byte[] data;

  @ManyToMany(mappedBy = "reportImages")
  @Getter
  @Setter
  private Set<JasperTemplate> jasperTemplates;

  /**
   * Create a new instance of ReportImage based on data from {@link Importer}.
   *
   * @param importer instance of {@link Importer}
   * @return new instance of image.
   */
  public static ReportImage newInstance(Importer importer) {
    ReportImage reportImage = new ReportImage();

    reportImage.setId(importer.getId());
    reportImage.setName(importer.getName());
    reportImage.setData(importer.getData());

    return reportImage;
  }

  /**
   * Copy values of attributes into new or updated ReportImage.
   *
   * @param reportImage Image with new values.
   */
  public void updateFrom(ReportImage reportImage) {
    this.name = reportImage.getName();
    this.data = reportImage.getData();
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setData(data);
    exporter.setId(id);
    exporter.setName(name);
  }

  public interface Exporter {
    void setId(UUID id);

    void setName(String name);

    void setData(byte[] data);

  }

  public interface Importer {
    UUID getId();

    String getName();

    byte[] getData();

  }
}
