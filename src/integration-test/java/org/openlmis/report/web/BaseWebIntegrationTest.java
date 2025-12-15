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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.openlmis.report.i18n.PermissionMessageKeys.ERROR_NO_PERMISSION;
import static org.openlmis.report.web.utils.WireMockResponses.MOCK_CHECK_RESULT;
import static org.openlmis.report.web.utils.WireMockResponses.MOCK_TOKEN_REQUEST_RESPONSE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.restassured.RestAssuredClient;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openlmis.report.domain.BaseEntity;
import org.openlmis.report.dto.external.referencedata.UserDto;
import org.openlmis.report.exception.PermissionMessageException;
import org.openlmis.report.service.PermissionService;
import org.openlmis.report.utils.AuthenticationHelper;
import org.openlmis.report.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
@DirtiesContext
public abstract class BaseWebIntegrationTest {
  protected static final String BASE_URL = System.getenv("BASE_URL");
  protected static final String CONTENT_TYPE = "Content-Type";
  protected static final String RAML_ASSERT_MESSAGE =
      "HTTP request/response should match RAML definition.";

  private final AtomicInteger categoryNumber = new AtomicInteger(0);

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(80);

  @MockBean
  protected AuthenticationHelper authenticationHelper;

  @MockBean
  protected PermissionService permissionService;

  protected RestAssuredClient restAssured;

  @Autowired
  private ObjectMapper objectMapper;

  @LocalServerPort
  private int serverPort;

  /**
   * Method called to initialize basic resources after the object is created.
   */
  @PostConstruct
  public void init() {
    mockExternalAuthorization();

    RestAssured.baseURI = BASE_URL;
    RestAssured.port = serverPort;
    RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
        new ObjectMapperConfig().jackson2ObjectMapperFactory((clazz, charset) -> objectMapper)
    );

    RamlDefinition ramlDefinition = RamlLoaders.fromClasspath()
        .load("api-definition-raml.yaml").ignoringXheaders();
    restAssured = ramlDefinition.createRestAssured();
  }

  int getNextCategoryNumber() {
    return this.categoryNumber.incrementAndGet();
  }

  protected void mockUserAuthenticated() {
    UserDto user = new UserDto();
    user.setId(UUID.randomUUID());
    user.setFirstName("admin");
    user.setLastName("strator");
    user.setEmail("admin@openlmis.org");

    given(authenticationHelper.getCurrentUser()).willReturn(user);
  }

  protected PermissionMessageException mockPermissionException(String... deniedPermissions) {
    PermissionMessageException exception = mock(PermissionMessageException.class);

    Message errorMessage = new Message(ERROR_NO_PERMISSION, (Object[])deniedPermissions);
    given(exception.asMessage()).willReturn(errorMessage);

    return exception;
  }

  protected UUID anyUuid() {
    return any(UUID.class);
  }

  protected String getTokenHeader() {
    return "Bearer " + UUID.randomUUID().toString();
  }

  protected void mockExternalAuthorization() {
    // This mocks the auth check to always return valid admin credentials.
    wireMockRule.stubFor(post(urlEqualTo("/api/oauth/check_token"))
        .willReturn(aResponse()
            .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(MOCK_CHECK_RESULT)));

    // This mocks the auth token request response
    wireMockRule.stubFor(post(urlPathEqualTo("/api/oauth/token?grant_type=client_credentials"))
        .willReturn(aResponse()
            .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBody(MOCK_TOKEN_REQUEST_RESPONSE)));
  }

  protected static class SaveAnswer<T extends BaseEntity> implements Answer<T> {

    @Override
    public T answer(InvocationOnMock invocation) throws Throwable {
      T obj = (T) invocation.getArguments()[0];

      if (null == obj) {
        return null;
      }

      if (null == obj.getId()) {
        obj.setId(UUID.randomUUID());
      }

      extraSteps(obj);

      return obj;
    }

    void extraSteps(T obj) {
      // should be overridden if any extra steps are required.
    }

  }
}
