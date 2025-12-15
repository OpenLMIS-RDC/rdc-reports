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

package org.openlmis.report.dto.external;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class EqualsVerifierWrapper<T> {
  private EqualsVerifier<T> verifier;

  public static <C> EqualsVerifierWrapper<C> forClass(Class<C> type) {
    return new EqualsVerifierWrapper<>(EqualsVerifier.forClass(type));
  }

  public EqualsVerifierWrapper<T> suppress(Warning... warnings) {
    verifier = verifier.suppress(warnings);
    return this;
  }

  /**
   * Adds prefabricated set.
   */
  public <S> EqualsVerifierWrapper<T> withPrefabSet(Class<S> setType) {
    Set<S> left = emptySet();
    Set<S> right = singleton(DtoGenerator.of(setType));
    verifier = verifier.withPrefabValues(Set.class, left, right);

    return this;
  }

  /**
   * Adds prefabricated value.
   */
  public <S> EqualsVerifierWrapper<T> withPrefabValue(Class<S> otherType) {
    List<S> list = DtoGenerator.of(otherType, 2);
    verifier = verifier.withPrefabValues(otherType, list.get(0), list.get(1));

    return this;
  }

  public void verify() {
    verifier.verify();
  }
}
