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
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.openlmis.report.service.PermissionService.REPORTS_VIEW;

import guru.nidi.ramltester.junit.RamlMatchers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.report.domain.JasperTemplate;
import org.openlmis.report.domain.ReportCategory;
import org.openlmis.report.dto.JasperTemplateDto;
import org.openlmis.report.exception.PermissionMessageException;
import org.openlmis.report.repository.JasperTemplateRepository;
import org.openlmis.report.repository.ReportCategoryRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("PMD.TooManyMethods")
public class JasperTemplateControllerIntegrationTest extends BaseWebIntegrationTest {
  private static final String RESOURCE_URL = "/api/reports/templates/common";
  private static final String ID_URL = RESOURCE_URL + "/{id}";
  private static final String FORMAT_PARAM = "format";
  private static final String REPORT_URL = ID_URL + "/{" + FORMAT_PARAM + "}";
  private static final String PDF_FORMAT = "pdf";
  private static final String CATEGORY_NAME = "Default Category";

  @MockBean
  private JasperTemplateRepository jasperTemplateRepository;

  @MockBean
  private ReportCategoryRepository reportCategoryRepository;

  private ReportCategory reportCategory;

  @Before
  public void setUp() {
    mockUserAuthenticated();

    reportCategory = new ReportCategory();
    reportCategory.setId(UUID.randomUUID());
    reportCategory.setName(CATEGORY_NAME);

    given(reportCategoryRepository.findByName(anyString())).willReturn(
        Optional.ofNullable(reportCategory));
    given(reportCategoryRepository.save(any(ReportCategory.class))).willReturn(reportCategory);
  }

  // GET /api/reports/templates

  @Test
  public void shouldGetVisibleTemplates() {
    // given
    JasperTemplate[] templates = { generateExistentTemplate(), generateExistentTemplate() };
    given(jasperTemplateRepository.findByVisible(true)).willReturn(Arrays.asList(templates));

    // when
    JasperTemplateDto[] result = restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(RESOURCE_URL)
        .then()
        .statusCode(200)
        .extract().as(JasperTemplateDto[].class);

    // then
    assertNotNull(result);
    assertEquals(2, result.length);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // DELETE /api/reports/templates

  @Test
  public void shouldDeleteExistentTemplate() {
    // given
    JasperTemplate template = generateExistentTemplate();

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", template.getId())
        .when()
        .delete(ID_URL)
        .then()
        .statusCode(204);

    // then
    verify(jasperTemplateRepository, atLeastOnce()).delete(eq(template));
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldNotDeleteNonExistentTemplate() {
    // given
    given(jasperTemplateRepository.findById(anyUuid())).willReturn(Optional.empty());

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", UUID.randomUUID())
        .when()
        .delete(ID_URL)
        .then()
        .statusCode(404);

    // then
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // GET /api/reports/templates/{id}

  @Test
  public void shouldGetExistentTemplate() {
    // given
    JasperTemplate template = generateExistentTemplate();

    // when
    JasperTemplateDto result = restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", template.getId())
        .when()
        .get(ID_URL)
        .then()
        .statusCode(200)
        .extract().as(JasperTemplateDto.class);

    // then
    assertEquals(template.getId(), result.getId());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldNotGetNonExistentTemplate() {
    // given
    given(jasperTemplateRepository.findById(anyUuid())).willReturn(Optional.empty());

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", UUID.randomUUID())
        .when()
        .get(ID_URL)
        .then()
        .statusCode(404);

    // them
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  // GET /api/reports/templates/{id}/{format}

  @Test
  public void generateReportShouldRejectWhenUserHasNoViewReportsRight() {
    // given
    JasperTemplate template = generateExistentTemplate();
    given(jasperTemplateRepository.findById(template.getId())).willReturn(
        java.util.Optional.of(template));

    doThrow(mockPermissionException(REPORTS_VIEW)).when(permissionService).canViewReports();

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", template.getId())
        .pathParam(FORMAT_PARAM, PDF_FORMAT)
        .when()
        .get(REPORT_URL)
        .then()
        .statusCode(HttpStatus.FORBIDDEN.value());

    // then
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void generateReportShouldReturnNotFoundWhenReportTemplateDoesNotExist() {
    // given
    given(jasperTemplateRepository.findById(anyUuid())).willReturn(Optional.empty());

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", UUID.randomUUID())
        .pathParam(FORMAT_PARAM, PDF_FORMAT)
        .when()
        .get(REPORT_URL)
        .then()
        .statusCode(404);

    // then
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void generateReportShouldRejectWhenUserHasNoReportSpecificRight() {
    // given
    String deniedPermission = "USERS_MANAGE";

    JasperTemplate template = generateExistentTemplate();
    template.setRequiredRights(Collections.singletonList(deniedPermission));

    PermissionMessageException ex = mockPermissionException(deniedPermission);
    doThrow(ex).when(permissionService).validatePermissions(any());

    // when
    restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .pathParam("id", template.getId())
        .pathParam(FORMAT_PARAM, PDF_FORMAT)
        .when()
        .get(REPORT_URL)
        .then()
        .statusCode(403);

    // then
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  private JasperTemplate generateExistentTemplate() {
    return generateExistentTemplate(UUID.randomUUID());
  }

  private JasperTemplate generateExistentTemplate(UUID id) {
    JasperTemplate template = new JasperTemplate();
    template.setId(id);
    template.setName("name");
    template.setRequiredRights(new ArrayList<>());
    template.setCategory(reportCategory);

    given(jasperTemplateRepository.findById(id)).willReturn(Optional.of(template));

    return template;
  }
}
