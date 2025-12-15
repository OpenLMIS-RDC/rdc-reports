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

package org.openlmis.report.i18n;

public class ReportingMessageKeys extends MessageKeys {
  private static final String ERROR = join(SERVICE_ERROR, "reporting");
  private static final String INCORRECT_TYPE = "incorrectType";
  private static final String PARAMETER = "parameter";
  private static final String CREATION = "creation";
  private static final String TEMPLATE = "template";
  private static final String INVALID = "invalid";
  private static final String MISSING = "missing";
  private static final String EXISTS = "exists";
  private static final String EMPTY = "empty";
  private static final String FILE = "file";


  public static final String ERROR_REPORTING_IO = join(ERROR, "io");
  public static final String ERROR_REPORTING_CLASS_NOT_FOUND = join(ERROR, "class", "notFound");
  public static final String ERROR_REPORTING_CREATION = join(ERROR, CREATION);
  public static final String ERROR_REPORTING_FILE_EMPTY = join(ERROR, FILE, EMPTY);
  public static final String ERROR_REPORTING_FILE_INCORRECT_TYPE =
      join(ERROR, FILE, INCORRECT_TYPE);
  public static final String ERROR_REPORTING_FILE_INVALID = join(ERROR, FILE, INVALID);
  public static final String ERROR_REPORTING_FILE_MISSING = join(ERROR, FILE, MISSING);
  public static final String ERROR_REPORTING_PARAMETER_INCORRECT_TYPE =
      join(ERROR, PARAMETER, INCORRECT_TYPE);
  public static final String ERROR_REPORTING_PARAMETER_MISSING =
      join(ERROR, PARAMETER, MISSING);
  public static final String ERROR_REPORTING_TEMPLATE_EXIST =
      join(ERROR, TEMPLATE, EXISTS);
}
