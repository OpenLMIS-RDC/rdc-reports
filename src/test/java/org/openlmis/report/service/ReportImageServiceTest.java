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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.openlmis.report.i18n.ReportingMessageKeys.ERROR_REPORTING_FILE_EMPTY;
import static org.openlmis.report.i18n.ReportingMessageKeys.ERROR_REPORTING_FILE_INCORRECT_TYPE;
import static org.openlmis.report.i18n.ReportingMessageKeys.ERROR_REPORTING_FILE_MISSING;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;

import java.util.UUID;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.report.domain.ReportImage;
import org.openlmis.report.exception.ReportingException;
import org.openlmis.report.repository.ReportImageRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({ReportImageService.class})
public class ReportImageServiceTest {

  @Mock
  private ReportImageRepository reportImageRepository;

  @InjectMocks
  private ReportImageService reportImageService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private static final String NAME_OF_FILE = "image.png";
  private static final String IMAGE_NAME = "logo";

  @Test
  public void shouldSaveValidNonExistentImage() throws Exception {
    // given
    given(reportImageRepository.findByName(anyString()))
        .willReturn(null);

    // when
    testSaveImage(IMAGE_NAME);
  }

  @Test
  public void shouldUpdateValidExistentImage() throws Exception {
    // given
    UUID oldId = UUID.randomUUID();

    ReportImage oldImage = new ReportImage();
    oldImage.setName(IMAGE_NAME);
    oldImage.setId(oldId);

    given(reportImageRepository.findByName(anyString()))
        .willReturn(oldImage);

    // when
    ReportImage image = testSaveImage(IMAGE_NAME);

    // then
    assertEquals(image.getId(), oldId);
  }

  private ReportImage testSaveImage(String name) throws ReportingException {
    ReportImageService service = spy(reportImageService);
    MultipartFile file = mock(MultipartFile.class);

    // validating and saving file is checked by other tests
    doNothing().when(service)
        .validateFileAndSetData(any(ReportImage.class), eq(file));

    // when
    ReportImage reportImage = service.saveImage(file, name);

    // then
    assertEquals(name, reportImage.getName());

    return reportImage;
  }
  
  @Test
  public void shouldThrowErrorIfFileHasIncorrectType() throws Exception {
    expectedException.expect(ReportingException.class);
    expectedException.expectMessage(ERROR_REPORTING_FILE_INCORRECT_TYPE);

    MockMultipartFile file = new MockMultipartFile("test.asd", new byte[1]);
    reportImageService.validateFileAndSetData(new ReportImage(), file);
  }

  @Test
  public void shouldThrowErrorIfFileEmpty() throws Exception {
    expectedException.expect(ReportingException.class);
    expectedException.expectMessage(ERROR_REPORTING_FILE_EMPTY);
    MockMultipartFile file = new MockMultipartFile(
        NAME_OF_FILE, NAME_OF_FILE, "", new byte[0]);

    reportImageService.validateFileAndSetData(new ReportImage(), file);
  }

  @Test
  public void shouldThrowErrorIfFileNotPresent() throws Exception {
    expectedException.expect(ReportingException.class);
    expectedException.expectMessage(ERROR_REPORTING_FILE_MISSING);

    reportImageService.validateFileAndSetData(new ReportImage(), null);
  }
}
