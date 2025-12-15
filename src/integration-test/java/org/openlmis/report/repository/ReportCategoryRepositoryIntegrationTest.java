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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.report.domain.JasperTemplate;
import org.openlmis.report.domain.ReportCategory;
import org.openlmis.report.utils.DashboardReportDataBuilder;
import org.openlmis.report.utils.ReportCategoryDataBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SuppressWarnings("PMD.TooManyMethods")
public class ReportCategoryRepositoryIntegrationTest extends
    BaseCrudRepositoryIntegrationTest<ReportCategory> {
  private static final String NAME = "ReportCategoryIntegrationTest";

  @Autowired
  private ReportCategoryRepository reportCategoryRepository;

  @Autowired
  private JasperTemplateRepository jasperTemplateRepository;

  @Autowired
  private DashboardReportRepository dashboardReportRepository;

  @Override
  ReportCategoryRepository getRepository() {
    return this.reportCategoryRepository;
  }

  @Override
  protected ReportCategory generateInstance() {
    return new ReportCategoryDataBuilder().withName(NAME).buildAsNew();
  }

  @Before
  public void setUp() {
    jasperTemplateRepository.deleteAll();
    dashboardReportRepository.deleteAll();
    reportCategoryRepository.deleteAll();
  }

  @Test
  public void shouldFindReportCategoryByName() {
    reportCategoryRepository.save(generateInstance());
    Optional<ReportCategory> foundCategory = reportCategoryRepository.findByName(NAME);

    assertThat(foundCategory.isPresent(), is(true));
    assertThat(foundCategory.get().getName(), is(NAME));
  }

  @Test
  public void shouldFindAllReportCategories() {
    ReportCategory reportCategory1 = reportCategoryRepository.save(
        new ReportCategoryDataBuilder().buildAsNew()
    );
    ReportCategory reportCategory2 = reportCategoryRepository.save(
        new ReportCategoryDataBuilder().buildAsNew()
    );

    reportCategoryRepository.save(reportCategory1);
    reportCategoryRepository.save(reportCategory2);

    Pageable pageable = PageRequest.of(0, 2);
    Page<ReportCategory> page = reportCategoryRepository.findAll(pageable);
    List<ReportCategory> found = page.getContent();

    assertThat(found.size(), is(2));
    assertThat(page.getTotalElements(), is(2L));
  }

  @Test
  public void shouldDeleteReportCategory() {
    ReportCategory reportCategory = reportCategoryRepository.save(
        new ReportCategoryDataBuilder().buildAsNew()
    );

    reportCategoryRepository.deleteById(reportCategory.getId());

    Optional<ReportCategory> foundCategory = reportCategoryRepository.findById(
        reportCategory.getId());

    assertThat(foundCategory, is(Optional.empty()));
  }

  @Test
  public void shouldUpdateReportCategory() {
    ReportCategory reportCategory = reportCategoryRepository.save(
        new ReportCategoryDataBuilder().buildAsNew()
    );
    reportCategory.setName("UpdatedCategoryName");
    reportCategoryRepository.save(reportCategory);

    Optional<ReportCategory> updatedCategory = reportCategoryRepository.findById(
        reportCategory.getId());

    assertThat(updatedCategory.isPresent(), is(true));
    assertThat(updatedCategory.get().getName(), is("UpdatedCategoryName"));
  }

  @Test
  public void shouldSaveReportCategory() {
    reportCategoryRepository.save(generateInstance());

    Optional<ReportCategory> foundCategory = reportCategoryRepository.findByName(NAME);

    assertThat(foundCategory.isPresent(), is(true));
    assertThat(foundCategory.get().getName(), is(NAME));
  }

  @Test
  public void shouldNotFindNonExistentReportCategoryById() {
    Optional<ReportCategory> foundCategory = reportCategoryRepository.findById(UUID.randomUUID());

    assertThat(foundCategory.isPresent(), is(false));
  }

  @Test
  public void shouldNotFindNonExistentReportCategoryByName() {
    Optional<ReportCategory> foundCategory = reportCategoryRepository
        .findByName("NonExistentCategory");

    assertThat(foundCategory.isPresent(), is(false));
  }

  @Test
  public void shouldReturnTrueWhenCategoryIsAssignedToDashboardReport() {
    ReportCategory reportCategory = reportCategoryRepository.save(
        new ReportCategoryDataBuilder().buildAsNew()
    );
    dashboardReportRepository.save(
        new DashboardReportDataBuilder().withCategory(reportCategory).build()
    );

    boolean exists = dashboardReportRepository.existsByCategory_Id(reportCategory.getId());
    assertThat(exists, is(true));
  }

  @Test
  public void shouldReturnFalseWhenCategoryIsNotAssignedToDashboardReport() {
    ReportCategory reportCategory = reportCategoryRepository.save(
        new ReportCategoryDataBuilder().buildAsNew()
    );
    boolean exists = dashboardReportRepository.existsByCategory_Id(reportCategory.getId());
    assertThat(exists, is(false));
  }

  @Test
  public void shouldReturnTrueWhenCategoryIsAssignedToJasperTemplate() {
    ReportCategory reportCategory = reportCategoryRepository.save(
        new ReportCategoryDataBuilder().buildAsNew()
    );

    JasperTemplate template = new JasperTemplate();
    template.setId(UUID.randomUUID());
    template.setName("name");
    template.setRequiredRights(new ArrayList<>());
    template.setCategory(reportCategory);

    jasperTemplateRepository.save(template);

    boolean exists = jasperTemplateRepository.existsByCategory_Id(reportCategory.getId());
    assertThat(exists, is(true));
  }

  @Test
  public void shouldReturnFalseWhenCategoryIsNotAssignedToJasperTemplate() {
    ReportCategory reportCategory = reportCategoryRepository.save(
        new ReportCategoryDataBuilder().buildAsNew()
    );
    boolean exists = jasperTemplateRepository.existsByCategory_Id(reportCategory.getId());
    assertThat(exists, is(false));
  }
}