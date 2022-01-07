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

import com.oliveryasuna.junitlib.StdReplacing;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

final class ObservableCollectionListenerTests extends StdReplacing {

  // Tests
  //--------------------------------------------------

  @Test
  final void observableCollection_listener_added() {
    final ObservableCollection<Integer> observable = new ObservableCollection<>(new HashSet<>());

    observable.addElementAddedListener(System.out::print);

    final int element = ThreadLocalRandom.current().nextInt();

    observable.add(element);

    assertThat(getStdout(), matchesPattern(
        "com\\.oliveryasuna\\.beanbag\\.collection\\.event\\.CollectionElementAddedEvent@[0-9a-f]+\\[source=\\[" + element + "],element=" + element + "]"));
  }

  @Test
  final void observableCollection_twoListeners_added() {
    final ObservableCollection<Integer> observable = new ObservableCollection<>(new HashSet<>());

    observable.addElementAddedListener(System.out::println);
    observable.addElementAddedListener(System.out::print);

    final int element = ThreadLocalRandom.current().nextInt();

    observable.add(element);

    assertThat(getStdout(), matchesPattern("" +
        "com\\.oliveryasuna\\.beanbag\\.collection\\.event\\.CollectionElementAddedEvent@[0-9a-f]+\\[source=\\[" + element + "],element=" + element + "]\n" +
        "com\\.oliveryasuna\\.beanbag\\.collection\\.event\\.CollectionElementAddedEvent@[0-9a-f]+\\[source=\\[" + element + "],element=" + element + "]"));
  }

  @Test
  final void observableCollection_listener_removed() {
    final int element = ThreadLocalRandom.current().nextInt();

    final ObservableCollection<Integer> observable = new ObservableCollection<>(new HashSet<>(Collections.singleton(element)));

    observable.addElementRemovedListener(System.out::print);

    observable.remove(element);

    assertThat(getStdout(), matchesPattern(
        "com\\.oliveryasuna\\.beanbag\\.collection\\.event\\.CollectionElementRemovedEvent@[0-9a-f]+\\[source=\\[],element=" + element + "]"));
  }

  @Test
  final void observableCollection_twoListeners_removed() {
    final int element = ThreadLocalRandom.current().nextInt();

    final ObservableCollection<Integer> observable = new ObservableCollection<>(new HashSet<>(Collections.singleton(element)));

    observable.addElementRemovedListener(System.out::println);
    observable.addElementRemovedListener(System.out::print);

    observable.remove(element);

    assertThat(getStdout(), matchesPattern("" +
        "com\\.oliveryasuna\\.beanbag\\.collection\\.event\\.CollectionElementRemovedEvent@[0-9a-f]+\\[source=\\[],element=" + element + "]\n" +
        "com\\.oliveryasuna\\.beanbag\\.collection\\.event\\.CollectionElementRemovedEvent@[0-9a-f]+\\[source=\\[],element=" + element + "]"));
  }

}
