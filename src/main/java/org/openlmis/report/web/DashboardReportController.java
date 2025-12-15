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

import static org.openlmis.report.web.DashboardReportController.RESOURCE_PATH;

import java.util.UUID;
import javax.validation.Valid;
import org.openlmis.report.dto.DashboardReportDto;
import org.openlmis.report.service.DashboardReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Transactional
@RequestMapping(RESOURCE_PATH)
public class DashboardReportController extends BaseController {
  public static final String RESOURCE_PATH = "/api/reports/dashboardReports";

  @Autowired
  private DashboardReportService dashboardReportService;

  /**
   * Get chosen dashboard report.
   *
   * @param reportId UUID of dashboard report we want to get.
   */
  @GetMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public DashboardReportDto getDashboardReport(@PathVariable("id") UUID reportId) {
    return dashboardReportService.getDashboardReport(reportId);
  }

  /**
   * Get page of all dashboard reports.
   *
   * @return All dashboard reports matching certain criteria.
   */
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Page<DashboardReportDto> getDashboardReports(Pageable pageable) {
    return dashboardReportService.getDashboardReports(pageable);
  }

  /**
   * Get page of all dashboard reports for which user has rights.
   *
   * @return All dashboard reports.
   */
  @GetMapping(value = "/availableReports")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Page<DashboardReportDto> getDashboardReportsForUser(Pageable pageable,
      @RequestParam(required = false, defaultValue = "false") boolean showOnHomePage) {
    return dashboardReportService.getDashboardReportsForUser(pageable, showOnHomePage);
  }

  /**
   * Allows deleting dashboard report.
   *
   * @param reportId UUID of dashboard report to delete.
   */
  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDashboardReport(@PathVariable("id") UUID reportId) {
    dashboardReportService.deleteDashboardReport(reportId);
  }

  /**
   * Adds or updates dashboard report to database.
   *
   * @param dto Data transfer object containing Dashboard Report data.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public DashboardReportDto createDashboardReport(@Valid @RequestBody DashboardReportDto dto) {
    return dashboardReportService.createDashboardReport(dto);
  }

  /**
   * Allows updating a dashboard report.
   *
   * @param id The UUID of the dashboard report to update.
   * @param dashboardReportDto Data transfer object containing updated dashboard report data.
   * @return The updated DashboardReportDto.
   */
  @PutMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public DashboardReportDto updateDashboardReport(@PathVariable("id") UUID id,
      @Valid @RequestBody DashboardReportDto dashboardReportDto) {
    return dashboardReportService.updateDashboardReport(id, dashboardReportDto);
  }
}
