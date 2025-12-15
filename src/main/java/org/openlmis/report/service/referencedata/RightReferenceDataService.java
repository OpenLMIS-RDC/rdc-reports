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

package org.openlmis.report.service.referencedata;

import static org.openlmis.report.utils.RequestHelper.createUri;

import java.util.List;
import java.util.UUID;

import org.openlmis.report.dto.external.referencedata.RightDto;
import org.openlmis.report.exception.ValidationMessageException;
import org.openlmis.report.i18n.DashboardReportMessageKeys;
import org.openlmis.report.utils.Message;
import org.openlmis.report.utils.RequestHelper;
import org.openlmis.report.utils.RequestParameters;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Service
public class RightReferenceDataService extends BaseReferenceDataService<RightDto> {

  @Override
  protected String getUrl() {
    return "/api/rights";
  }

  @Override
  protected Class<RightDto> getResultClass() {
    return RightDto.class;
  }

  @Override
  protected Class<RightDto[]> getArrayResultClass() {
    return RightDto[].class;
  }

  /**
   * Find a correct right by the provided name.
   *
   * @param name right name
   * @return right related with the name or {@code null}.
   */
  public RightDto findRight(String name) {
    List<RightDto> rights = findAll("/search", RequestParameters.init().set("name", name));
    return rights.isEmpty() ? null : rights.get(0);
  }

  /**
   * Save a new right by making a POST request to the referenced data service.
   *
   * @param rightDto the RightDto to save
   */
  @SuppressWarnings("PMD.PreserveStackTrace")
  public void save(RightDto rightDto) {
    String url = getServiceUrl() + getUrl();

    try {
      runWithTokenRetry(() ->
          restTemplate.exchange(
              createUri(url),
              HttpMethod.PUT,
              RequestHelper.createEntity(authorizationService.obtainAccessToken(), rightDto),
              RightDto.class
        ));
    } catch (HttpStatusCodeException ex) {
      throw new ValidationMessageException(
          new Message(DashboardReportMessageKeys.ERROR_COULD_NOT_SAVE_RIGHT, rightDto.getName()));
    }
  }

  /**
   * Delete an existing right by making a DELETE request to the referencedata service.
   *
   * @param rightId the ID of the RightDto to delete
   */
  @SuppressWarnings("PMD.PreserveStackTrace")
  public void delete(UUID rightId) {
    String url = getServiceUrl() + getUrl() + "/" + rightId;

    try {
      runWithTokenRetry(() ->
          restTemplate.exchange(
              createUri(url),
              HttpMethod.DELETE,
              RequestHelper.createEntity(authorizationService.obtainAccessToken(), null),
              Void.class
        )
      );
    } catch (HttpStatusCodeException ex) {
      throw new ValidationMessageException(
          new Message(DashboardReportMessageKeys.ERROR_COULD_NOT_DELETE_RIGHT, rightId, ex));
    }
  }
}
