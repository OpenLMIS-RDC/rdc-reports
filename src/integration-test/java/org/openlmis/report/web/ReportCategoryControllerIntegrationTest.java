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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.report.domain.ReportCategory;
import org.openlmis.report.dto.ReportCategoryDto;
import org.openlmis.report.repository.ReportCategoryRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ReportCategoryControllerIntegrationTest
    extends BaseWebIntegrationTest {
  private static final String RESOURCE_URL = "/api/reports/reportCategories";
  private static final String ID_URL = RESOURCE_URL + "/{id}";

  @MockBean
  private ReportCategoryRepository reportCategoryRepository;

  private UUID reportCategoryId;
  private ReportCategory reportCategory;

  @Before
  public void setUp() {
    reportCategory = new ReportCategory();
    reportCategoryId = UUID.randomUUID();
    reportCategory.setId(reportCategoryId);
    reportCategory.setName("Test Category");
  }

  @Test
  public void shouldReturnReportCategoryWhenCategoryExists() {
    when(reportCategoryRepository.findById(reportCategoryId)).thenReturn(
        Optional.of(reportCategory));

    // When & Then
    ReportCategoryDto response = restAssured.given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .when()
        .get(ID_URL, reportCategoryId)
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract().as(ReportCategoryDto.class);

    // Verify
    assertNotNull(response);
    assertEquals(response.getId(), reportCategory.getId());
    assertEquals(response.getName(), reportCategory.getName());

    verify(reportCategoryRepository).findById(reportCategoryId);
  }

  @Test
  public void shouldUpdateReportCategoryWhenValidDataProvided() {
    // Given
    ReportCategoryDto updatedDto = new ReportCategoryDto();
    updatedDto.setId(reportCategoryId);
    updatedDto.setName("Updated Category Name");

    ReportCategory updatedCategory = new ReportCategory();
    updatedCategory.setId(reportCategoryId);
    updatedCategory.setName(updatedDto.getName());

    when(reportCategoryRepository.existsByIdIsNotAndName(
        reportCategoryId, updatedDto.getName())).thenReturn(false);
    when(reportCategoryRepository.findById(reportCategoryId)).thenReturn(
        Optional.of(reportCategory));
    when(reportCategoryRepository.save(any(ReportCategory.class))).thenReturn(updatedCategory);

    // When & Then
    ReportCategoryDto response = restAssured.given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .body(updatedDto)
        .when()
        .put(ID_URL, reportCategoryId)
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract().as(ReportCategoryDto.class);

    // Verify
    assertNotNull(response);
    assertEquals(response.getId(), updatedDto.getId());
    assertEquals(response.getName(), updatedDto.getName());

    verify(reportCategoryRepository).existsByIdIsNotAndName(
        reportCategoryId, updatedDto.getName());
    verify(reportCategoryRepository).findById(reportCategoryId);
    verify(reportCategoryRepository).save(any(ReportCategory.class));
  }

}
