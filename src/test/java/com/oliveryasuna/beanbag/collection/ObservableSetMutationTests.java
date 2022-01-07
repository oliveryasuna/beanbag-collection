/*
 * Copyright 2022 Oliver Yasuna
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oliveryasuna.beanbag.collection;

import com.oliveryasuna.beanbag.collection.util.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.oliveryasuna.beanbag.collection.util.TestUtils.uniqueElement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ObservableSetMutationTests {

  // Tests
  //--------------------------------------------------

  @Test
  final void observableSet_add() {
    final int element = ThreadLocalRandom.current().nextInt();

    final Set<Integer> set = new HashSet<>();

    new ObservableSet<>(set).add(element);

    assertEquals(1, set.size());

    assertTrue(set.contains(element));
  }

  @Test
  final void observableSet_addAll() {
    final Set<Integer> set = new HashSet<>();

    final int element1 = ThreadLocalRandom.current().nextInt();
    final int element2 = uniqueElement(() -> ThreadLocalRandom.current().nextInt(), element1);

    new ObservableSet<>(set).addAll(Set.of(element1, element2));

    assertEquals(2, set.size());

    assertTrue(set.contains(element1));
    assertTrue(set.contains(element2));
  }

  @Test
  final void observableSet_remove() {
    final int element = ThreadLocalRandom.current().nextInt();

    final Set<Integer> set = new HashSet<>(Collections.singleton(element));

    new ObservableSet<>(set).remove(element);

    assertTrue(set.isEmpty());
  }

  @Test
  final void observableSet_removeAll() {
    final int element1 = ThreadLocalRandom.current().nextInt();
    final int element2 = uniqueElement(() -> ThreadLocalRandom.current().nextInt(), element1);

    final Set<Integer> set = new HashSet<>(Set.of(element1, element2));

    new ObservableSet<>(set).removeAll(Set.of(element1, element2));

    assertTrue(set.isEmpty());
  }

  @Test
  final void observableSet_removeIf() {
    final int element1 = ThreadLocalRandom.current().nextInt();
    final int element2 = uniqueElement(() -> ThreadLocalRandom.current().nextInt(), element1);

    final Set<Integer> set = new HashSet<>(Set.of(element1, element2));

    new ObservableSet<>(set).removeIf(TestUtils::alwaysTruePredicate);

    assertTrue(set.isEmpty());
  }

  @Test
  final void observableSet_retainAll() {
    final int element1 = ThreadLocalRandom.current().nextInt();
    final int element2 = uniqueElement(() -> ThreadLocalRandom.current().nextInt(), element1);
    final int element3 = uniqueElement(() -> ThreadLocalRandom.current().nextInt(), element1, element2);

    final Set<Integer> set = new HashSet<>(Set.of(element1, element2, element3));

    new ObservableSet<>(set).retainAll(Set.of(element1, element2));

    assertEquals(2, set.size());

    assertTrue(set.contains(element1));
    assertTrue(set.contains(element2));
  }

  @Test
  final void observableSet_clear() {
    final int element1 = ThreadLocalRandom.current().nextInt();
    final int element2 = uniqueElement(() -> ThreadLocalRandom.current().nextInt(), element1);

    final Set<Integer> set = new HashSet<>(Set.of(element1, element2));

    new ObservableSet<>(set).clear();

    assertTrue(set.isEmpty());
  }

}
