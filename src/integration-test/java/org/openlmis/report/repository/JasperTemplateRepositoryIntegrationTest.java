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

package org.openlmis.report.repository;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.report.domain.JasperTemplate;
import org.openlmis.report.domain.JasperTemplateParameter;
import org.openlmis.report.domain.ReportCategory;
import org.springframework.beans.factory.annotation.Autowired;

public class JasperTemplateRepositoryIntegrationTest extends
    BaseCrudRepositoryIntegrationTest<JasperTemplate> {

  private static final String NAME = "TemplateRepositoryIntegrationTest";
  private static final String CATEGORY_NAME = "Default Category";

  @Autowired
  private JasperTemplateRepository jasperTemplateRepository;

  @Autowired
  private DashboardReportRepository dashboardReportRepository;

  @Autowired
  private ReportCategoryRepository reportCategoryRepository;

  @Override
  JasperTemplateRepository getRepository() {
    return this.jasperTemplateRepository;
  }

  @Override
  protected JasperTemplate generateInstance() {
    ReportCategory category = new ReportCategory();
    category.setName(CATEGORY_NAME + getNextCategoryNumber());
    ReportCategory savedCategory = reportCategoryRepository.save(category);

    JasperTemplate jasperTemplate = new JasperTemplate();
    jasperTemplate.setName(NAME + getNextInstanceNumber());
    jasperTemplate.setCategory(savedCategory);

    return jasperTemplate;
  }

  @Before
  public void setUp() {
    jasperTemplateRepository.deleteAll();
    dashboardReportRepository.deleteAll();
    reportCategoryRepository.deleteAll();
  }

  @Test
  public void shouldFindTemplateByName() {
    // given
    JasperTemplate entity = generateInstance();
    jasperTemplateRepository.save(entity);

    // when
    JasperTemplate found = jasperTemplateRepository.findByName(entity.getName());

    // then
    assertThat(found.getName(), is(entity.getName()));
  }

  @Test
  public void shouldBindParametersToTemplateOnSave() {
    // given
    JasperTemplateParameter templateParameter = new JasperTemplateParameter();
    templateParameter.setName("parameter");
    templateParameter.setRequired(true);

    JasperTemplate template = generateInstance();
    template.setTemplateParameters(Collections.singletonList(templateParameter));

    // when
    JasperTemplate result = jasperTemplateRepository.save(template);

    // then
    assertEquals(templateParameter.getTemplate().getId(), result.getId());
  }

  @Test
  public void shouldFindByVisibilityFlag() {
    JasperTemplate visibleTemplate = generateInstance();
    JasperTemplate hiddenTemplate = generateInstance();
    hiddenTemplate.setVisible(false);

    jasperTemplateRepository.save(visibleTemplate);
    jasperTemplateRepository.save(hiddenTemplate);

    assertThat(
        jasperTemplateRepository.findByVisible(true),
        hasItem(hasProperty("id", is(visibleTemplate.getId()))));

    assertThat(
        jasperTemplateRepository.findByVisible(false),
        hasItem(hasProperty("id", is(hiddenTemplate.getId()))));
  }
}
