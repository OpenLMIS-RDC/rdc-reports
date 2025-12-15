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

import static org.openlmis.report.i18n.ReportingMessageKeys.ERROR_REPORTING_FILE_EMPTY;
import static org.openlmis.report.i18n.ReportingMessageKeys.ERROR_REPORTING_FILE_INCORRECT_TYPE;
import static org.openlmis.report.i18n.ReportingMessageKeys.ERROR_REPORTING_FILE_MISSING;

import java.util.Arrays;
import org.openlmis.report.exception.ReportingException;
import org.springframework.web.multipart.MultipartFile;

public final class ReportingValidationHelper {

  /**
   * Throws exception if file is empty.
   * @param file The file to check
   * @throws ReportingException thrown when file is empty
   */
  public static void throwIfFileIsEmpty(MultipartFile file) throws ReportingException {
    if (file.isEmpty()) {
      throw new ReportingException(ERROR_REPORTING_FILE_EMPTY);
    }
  }

  /**
   * Throws exception if file has incorrect type.
   * @param file The file to check
   * @param allowedFileTypes Allowed file types, e.g. png, jpg
   * @throws ReportingException thrown when file has incorrect type
   */
  public static void throwIfIncorrectFileType(MultipartFile file, String[] allowedFileTypes)
      throws ReportingException {
    if (Arrays.stream(allowedFileTypes).noneMatch(type ->
        file.getOriginalFilename().toLowerCase().endsWith("." + type))) {
      throw new ReportingException(ERROR_REPORTING_FILE_INCORRECT_TYPE);
    }
  }

  /**
   * Throws exception if file is null.
   * @param file The file to check
   * @throws ReportingException thrown when file is null
   */
  public static void throwIfFileIsNull(MultipartFile file) throws ReportingException {
    if (file == null) {
      throw new ReportingException(ERROR_REPORTING_FILE_MISSING);
    }
  }
}

