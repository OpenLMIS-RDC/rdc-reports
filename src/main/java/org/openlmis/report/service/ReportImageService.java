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

import static org.openlmis.report.i18n.ReportingMessageKeys.ERROR_REPORTING_IO;

import java.io.IOException;
import org.openlmis.report.domain.ReportImage;
import org.openlmis.report.exception.ReportingException;
import org.openlmis.report.repository.ReportImageRepository;
import org.openlmis.report.utils.ReportingValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@SuppressWarnings("PMD.TooManyMethods")
public class ReportImageService {
  private static final String[] ALLOWED_FILETYPES = {"jpg", "png", "gif"};

  @Autowired
  private ReportImageRepository reportImageRepository;

  /**
   * Saves image with given name.
   *
   * @param file image file
   * @param name name of image
   * @return saved image
   */
  public ReportImage saveImage(MultipartFile file, String name) throws ReportingException {
    ReportImage reportImage = reportImageRepository.findByName(name);

    if (reportImage == null) {
      reportImage = new ReportImage();
    }
    reportImage.setName(name);

    validateFileAndSetData(reportImage, file);
    return reportImage;
  }

  void validateFileAndSetData(ReportImage reportImage, MultipartFile file)
      throws ReportingException {
    ReportingValidationHelper.throwIfFileIsNull(file);
    ReportingValidationHelper.throwIfIncorrectFileType(file, ALLOWED_FILETYPES);
    ReportingValidationHelper.throwIfFileIsEmpty(file);

    try {
      reportImage.setData(file.getBytes());
      reportImageRepository.save(reportImage);
    } catch (IOException ex) {
      throw new ReportingException(ex, ERROR_REPORTING_IO, ex.getMessage());
    }
  }
}
