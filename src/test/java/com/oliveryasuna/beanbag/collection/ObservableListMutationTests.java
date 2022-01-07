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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.oliveryasuna.beanbag.collection.util.TestUtils.uniqueElement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ObservableListMutationTests {

  // Tests
  //--------------------------------------------------

  @Test
  final void observableList_add() {
    final int element = ThreadLocalRandom.current().nextInt();

    final List<Integer> list = new ArrayList<>();

    new ObservableList<>(list).add(element);

    assertEquals(1, list.size());

    assertTrue(list.contains(element));
  }

  @Test
  final void observableList_addAll() {
    final List<Integer> list = new ArrayList<>();

    final int element1 = ThreadLocalRandom.current().nextInt();
    final int element2 = uniqueElement(() -> ThreadLocalRandom.current().nextInt(), element1);

    new ObservableList<>(list).addAll(List.of(element1, element2));

    assertEquals(2, list.size());

    assertTrue(list.contains(element1));
    assertTrue(list.contains(element2));
  }

  @Test
  final void observableList_remove() {
    final int element = ThreadLocalRandom.current().nextInt();

    final List<Integer> list = new ArrayList<>(Collections.singletonList(element));

    new ObservableList<>(list).remove((Integer)element);

    assertTrue(list.isEmpty());
  }

  @Test
  final void observableList_removeAll() {
    final int element1 = ThreadLocalRandom.current().nextInt();
    final int element2 = uniqueElement(() -> ThreadLocalRandom.current().nextInt(), element1);

    final List<Integer> list = new ArrayList<>(List.of(element1, element2));

    new ObservableList<>(list).removeAll(List.of(element1, element2));

    assertTrue(list.isEmpty());
  }

  @Test
  final void observableList_removeIf() {
    final int element1 = ThreadLocalRandom.current().nextInt();
    final int element2 = uniqueElement(() -> ThreadLocalRandom.current().nextInt(), element1);

    final List<Integer> list = new ArrayList<>(List.of(element1, element2));

    new ObservableList<>(list).removeIf(TestUtils::alwaysTruePredicate);

    assertTrue(list.isEmpty());
  }

  @Test
  final void observableList_retainAll() {
    final int element1 = ThreadLocalRandom.current().nextInt();
    final int element2 = uniqueElement(() -> ThreadLocalRandom.current().nextInt(), element1);
    final int element3 = uniqueElement(() -> ThreadLocalRandom.current().nextInt(), element1, element2);

    final List<Integer> list = new ArrayList<>(List.of(element1, element2, element3));

    new ObservableList<>(list).retainAll(List.of(element1, element2));

    assertEquals(2, list.size());

    assertTrue(list.contains(element1));
    assertTrue(list.contains(element2));
  }

  @Test
  final void observableList_clear() {
    final int element1 = ThreadLocalRandom.current().nextInt();
    final int element2 = uniqueElement(() -> ThreadLocalRandom.current().nextInt(), element1);

    final List<Integer> list = new ArrayList<>(List.of(element1, element2));

    new ObservableList<>(list).clear();

    assertTrue(list.isEmpty());
  }

}
