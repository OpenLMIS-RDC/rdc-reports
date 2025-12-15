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

package org.openlmis.report.web;

import static org.openlmis.report.i18n.ReportImageMessageKeys.ERROR_NOT_FOUND;

import java.util.Collections;
import java.util.UUID;
import org.openlmis.report.domain.ReportImage;
import org.openlmis.report.exception.NotFoundMessageException;
import org.openlmis.report.exception.ReportingException;
import org.openlmis.report.repository.ReportImageRepository;
import org.openlmis.report.service.PermissionService;
import org.openlmis.report.service.ReportImageService;
import org.openlmis.report.utils.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Transactional
@RequestMapping("/api/reports/images")
public class ReportImageController extends BaseController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReportImageController.class);

  @Autowired
  private ReportImageRepository reportImageRepository;

  @Autowired
  private PermissionService permissionService;

  @Autowired
  private ReportImageService reportImageService;

  /**
   * Saves image to the database.
   *
   * @param file Image to upload
   * @param name Name of file in database
   */
  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void createReportImage(
      @RequestPart("file") MultipartFile file, String name) throws ReportingException {
    permissionService.canEditReportTemplates();

    LOGGER.debug("Creating new reportImage");
    reportImageService.saveImage(file, name);
  }

  /**
   * Get all reportImages.
   *
   * @return ReportImages.
   */
  @RequestMapping(method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Iterable<ReportImage> getAllReportImage() {
    permissionService.canViewReports();

    Iterable<ReportImage> reportImages = reportImageRepository.findAll();
    if (reportImages == null) {
      return Collections.emptySet();
    } else {
      return reportImages;
    }
  }

  /**
   * Get chosen reportImage.
   *
   * @param reportImageId UUID of reportImage which we want to get
   * @return the reportImage.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ReportImage getReportImage(
      @PathVariable("id") UUID reportImageId) {
    permissionService.canViewReports();

    return reportImageRepository.findById(reportImageId)
        .orElseThrow(() -> new NotFoundMessageException(new Message(ERROR_NOT_FOUND)));
  }

  /**
   * Allows deleting reportImage.
   *
   * @param reportImageId UUID of reportImage which we want to delete
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteReportImage(
      @PathVariable("id") UUID reportImageId) {
    permissionService.canEditReportTemplates();

    ReportImage reportImage = reportImageRepository.findById(reportImageId)
        .orElseThrow(() -> new NotFoundMessageException(new Message(ERROR_NOT_FOUND)));
    reportImageRepository.delete(reportImage);
  }
}
