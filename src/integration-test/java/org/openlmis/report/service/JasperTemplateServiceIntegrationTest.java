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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.report.domain.JasperTemplate;
import org.openlmis.report.domain.JasperTemplateParameter;
import org.openlmis.report.domain.ReportCategory;
import org.openlmis.report.repository.JasperTemplateRepository;
import org.openlmis.report.repository.ReportCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class JasperTemplateServiceIntegrationTest {

  private static final String NAME = "OverrideWithDependencies";
  private static final String CATEGORY = "Default Category";
  private static final String JRXML = "report-with-dependencies.jrxml";

  @Autowired
  private JasperTemplateService jasperTemplateService;

  @Autowired
  private JasperTemplateRepository jasperTemplateRepository;

  @Autowired
  private ReportCategoryRepository reportCategoryRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Before
  public void setUp() {
    // reuse the category seeded by the migrations, or create it when running against an empty db
    if (!reportCategoryRepository.findByName(CATEGORY).isPresent()) {
      ReportCategory category = new ReportCategory();
      category.setName(CATEGORY);
      reportCategoryRepository.save(category);
    }
  }

  private MultipartFile jrxmlFile() throws IOException {
    byte[] content = StreamUtils.copyToByteArray(
        getClass().getClassLoader().getResourceAsStream(JRXML));
    return new MockMultipartFile("file", JRXML, "application/xml", content);
  }

  /**
   * Reproduces the QA failure: re-uploading (override=true) a template whose parameters carry
   * dependencies previously crashed with HTTP 500 because a dependency row was written with a
   * null parameterId. The override must now succeed, preserve the template and parameter ids,
   * and persist the dependency row with a non-null parameterId.
   */
  @Test
  public void shouldOverrideTemplateThatHasParameterDependencies() throws Exception {
    // first upload (INSERT)
    jasperTemplateService.saveTemplate(
        jrxmlFile(), NAME, "initial", Collections.emptyList(), CATEGORY, false);
    entityManager.flush();

    JasperTemplate afterInsert = jasperTemplateRepository.findByName(NAME);
    final UUID templateId = afterInsert.getId();
    final UUID paramId = afterInsert.getTemplateParameters().get(0).getId();
    assertNotNull(templateId);
    assertEquals(1, afterInsert.getTemplateParameters().get(0).getDependencies().size());

    // simulate a fresh request so the override path reloads a managed entity from the database
    entityManager.clear();

    // re-upload the SAME template with override=true (the scenario that returned HTTP 500)
    jasperTemplateService.saveTemplate(
        jrxmlFile(), NAME, "overridden", Collections.emptyList(), CATEGORY, true);
    entityManager.flush();

    JasperTemplate afterOverride = jasperTemplateRepository.findByName(NAME);
    assertEquals(templateId, afterOverride.getId());

    JasperTemplateParameter parameter = afterOverride.getTemplateParameters().get(0);
    assertEquals(paramId, parameter.getId());
    assertEquals(1, parameter.getDependencies().size());
    assertNotNull(parameter.getDependencies().get(0).getParameter());
    assertEquals(parameter.getId(), parameter.getDependencies().get(0).getParameter().getId());
  }
}
