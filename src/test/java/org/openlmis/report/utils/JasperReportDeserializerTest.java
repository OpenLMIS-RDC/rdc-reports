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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.junit.Test;

public class JasperReportDeserializerTest {

  private final JasperReportDeserializer deserializer = new JasperReportDeserializer();

  @Test
  public void shouldDeserializeCompiledJasperReport() throws Exception {
    JasperReport report;
    try (InputStream jrxml = getClass().getResourceAsStream("/empty-report.jrxml")) {
      report = JasperCompileManager.compileReport(jrxml);
    }

    JasperReport result = deserializer.deserialize(serialize(report));

    assertNotNull(result);
    assertEquals(report.getName(), result.getName());
  }

  @Test
  public void shouldDeserializeCompiledReportContainingCrosstab() throws Exception {
    JasperReport report;
    try (InputStream jrxml = getClass().getResourceAsStream("/crosstab-report.jrxml")) {
      report = JasperCompileManager.compileReport(jrxml);
    }

    JasperReport result = deserializer.deserialize(serialize(report));

    assertNotNull(result);
    assertEquals(report.getName(), result.getName());
  }

  @Test(expected = InvalidClassException.class)
  public void shouldRejectPayloadWithClassOutsideAllowlist() throws Exception {
    deserializer.deserialize(serialize(new File("/tmp/not-a-report")));
  }

  @Test(expected = ClassCastException.class)
  public void shouldRejectAllowlistedObjectThatIsNotJasperReport() throws Exception {
    deserializer.deserialize(serialize("just a string"));
  }

  private byte[] serialize(Serializable object) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
      out.writeObject(object);
    }
    return bos.toByteArray();
  }
}
