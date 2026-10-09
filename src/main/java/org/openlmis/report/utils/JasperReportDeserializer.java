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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.springframework.stereotype.Component;

/**
 * Deserializes compiled {@link JasperReport} payloads through a class allowlist. Report bytes
 * reach the service from outside (template uploads stored in the database, and the
 * template/subreport payloads other services POST to the generate endpoint), so every
 * deserialization must go through this component - a plain {@code ObjectInputStream}/JRLoader
 * would execute whatever the payload contains.
 */
@Component
public class JasperReportDeserializer {

  /**
   * Deserialize a compiled JasperReport, allowing only the classes a legitimately compiled
   * report can contain.
   *
   * @param data the serialized compiled report
   * @return the deserialized report
   * @throws IOException            when the stream is corrupt or contains a disallowed class
   * @throws ClassNotFoundException when a class in the stream cannot be resolved
   */
  public JasperReport deserialize(byte[] data) throws IOException, ClassNotFoundException {
    try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data);
         ValidatingObjectInputStream vois = new ValidatingObjectInputStream(byteInputStream)) {
      vois.accept(
          "net.sf.jasperreports.*",
          "java.awt.*",
          "java.util.*",
          "java.lang.*",
          "java.math.*",
          "[Lnet.sf.jasperreports.*",
          "[Ljava.awt.*",
          "[Ljava.util.*",
          "[Ljava.lang.*",
          "[Ljava.math.*",
          // two-dimensional arrays are matched by their own "[[L" prefix, not by the
          // one-dimensional patterns above: a crosstab stores its cells as
          // JRCrosstabCell[][], so without these a compiled crosstab is rejected
          "[[Lnet.sf.jasperreports.*",
          "[[Ljava.awt.*",
          "[[Ljava.util.*",
          "[[Ljava.lang.*",
          "[[Ljava.math.*",
          "[B"
      );
      return (JasperReport) vois.readObject();
    }
  }
}
