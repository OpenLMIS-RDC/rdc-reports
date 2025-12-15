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

package org.openlmis.report.utils;

import java.util.regex.Pattern;

public class PropertyKeyUtil {

  private static final Pattern INVALID_CHARACTERS_PATTERN = Pattern.compile("[^a-zA-Z0-9_]");

  /**
   * Transforms a string to a valid property key format.
   * - Trims whitespace
   * - Converts to uppercase
   * - Replaces spaces with underscores
   * - Removes invalid characters
   * - Ensures the key does not start with a number
   *
   * @param nameToTransform input string
   * @return the valid name which can be used as property key
   */
  public static String transformToPropertyKey(String nameToTransform) {
    if (nameToTransform == null || nameToTransform.trim().isEmpty()) {
      return null;
    }

    String transformed = nameToTransform.trim().toUpperCase().replace(" ", "_");

    transformed = INVALID_CHARACTERS_PATTERN.matcher(transformed).replaceAll("");

    if (Character.isDigit(transformed.charAt(0))) {
      transformed = "_" + transformed;
    }

    return transformed + "_RIGHT";
  }
}
