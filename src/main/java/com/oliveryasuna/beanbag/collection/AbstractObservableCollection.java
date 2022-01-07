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

import com.oliveryasuna.beanbag.ObservableBean;
import com.oliveryasuna.beanbag.collection.event.CollectionElementAddedEvent;
import com.oliveryasuna.beanbag.collection.event.CollectionElementRemovedEvent;
import com.oliveryasuna.beanbag.collection.listener.CollectionElementAddedListener;
import com.oliveryasuna.beanbag.collection.listener.CollectionElementRemovedListener;
import com.oliveryasuna.beanbag.helper.IteratorDecorator;
import com.oliveryasuna.commons.language.pattern.registry.Registration;
import org.apache.commons.lang3.event.EventListenerSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;

public abstract class AbstractObservableCollection<T, COL extends Collection<T>, SUB extends AbstractObservableCollection<T, COL, SUB>>
    extends ObservableBean<COL, SUB> implements Collection<T> {

  // Constructors
  //--------------------------------------------------

  protected AbstractObservableCollection(final COL collection) {
    super(collection);
  }

  // Listener registries
  //--------------------------------------------------

  protected final EventListenerSupport<CollectionElementAddedListener> elementAddedListeners =
      EventListenerSupport.create(CollectionElementAddedListener.class);

  protected final EventListenerSupport<CollectionElementRemovedListener> elementRemovedListeners =
      EventListenerSupport.create(CollectionElementRemovedListener.class);

  // Listener registration methods
  //--------------------------------------------------

  public Registration addElementAddedListener(final CollectionElementAddedListener<T, COL, SUB> listener) {
    elementAddedListeners.addListener(listener);

    return (() -> elementAddedListeners.removeListener(listener));
  }

  public void removedElementAddedListener(final CollectionElementAddedListener<T, COL, SUB> listener) {
    elementAddedListeners.removeListener(listener);
  }

  public Registration addElementRemovedListener(final CollectionElementRemovedListener<T, COL, SUB> listener) {
    elementRemovedListeners.addListener(listener);

    return (() -> elementRemovedListeners.removeListener(listener));
  }

  public void removedElementRemovedListener(final CollectionElementRemovedListener<T, COL, SUB> listener) {
    elementRemovedListeners.removeListener(listener);
  }

  // Listener dispatch methods
  //--------------------------------------------------

  protected void fireElementAddedEvent(final T element) {
    elementAddedListeners.fire().elementAdded(new CollectionElementAddedEvent<>(element, (SUB)this));
  }

  protected void fireElementRemovedEvent(final T element) {
    elementRemovedListeners.fire().elementRemoved(new CollectionElementRemovedEvent<>(element, (SUB)this));
  }

  // Getters/setters
  //--------------------------------------------------

  protected final COL getCollection() {
    return getBean();
  }

  protected final void setCollection(final COL collection) {
    setBean(collection);
  }

  // Collection methods
  //--------------------------------------------------

  @Override
  public boolean add(final T element) {
    final boolean modified = getCollection().add(element);

    if(modified) {
      fireElementAddedEvent(element);
    }

    return modified;
  }

  @Override
  public boolean addAll(final Collection<? extends T> collection) {
    final Collection<T> beforeAddAll = Collections.unmodifiableCollection(bean);

    final boolean modified = getCollection().addAll(collection);

    if(modified) {
      final Collection<T> added = new ArrayList<>(bean);
      added.removeAll(beforeAddAll);

      added.forEach(this::fireElementAddedEvent);
    }

    return modified;
  }

  @Override
  public boolean remove(final Object element) {
    final boolean modified = getCollection().remove(element);

    if(modified) {
      fireElementRemovedEvent((T)element);
    }

    return modified;
  }

  @Override
  public boolean removeAll(final Collection<?> collection) {
    final Collection<T> removed = new ArrayList<>(bean);

    final boolean modified = getCollection().removeAll(collection);

    if(modified) {
      removed.removeAll(bean);

      removed.forEach(this::fireElementRemovedEvent);
    }

    return modified;
  }

  @Override
  public boolean removeIf(final Predicate<? super T> filter) {
    final Collection<T> removed = new ArrayList<>(bean);

    final boolean modified = getCollection().removeIf(filter);

    if(modified) {
      removed.removeAll(bean);

      removed.forEach(this::fireElementRemovedEvent);
    }

    return modified;
  }

  @Override
  public boolean retainAll(final Collection<?> collection) {
    final Collection<T> removed = new ArrayList<>(bean);

    final boolean modified = getCollection().retainAll(collection);

    if(modified) {
      removed.removeAll(bean);

      removed.forEach(this::fireElementRemovedEvent);
    }

    return modified;
  }

  @Override
  public void clear() {
    final Collection<T> beforeClear = Collections.unmodifiableCollection(bean);

    getCollection().clear();

    beforeClear.forEach(this::fireElementRemovedEvent);
  }

  @Override
  public boolean contains(final Object element) {
    return getCollection().contains(element);
  }

  @Override
  public boolean containsAll(final Collection<?> collection) {
    return getCollection().containsAll(collection);
  }

  @Override
  public int size() {
    return getCollection().size();
  }

  @Override
  public boolean isEmpty() {
    return getCollection().isEmpty();
  }

  @Override
  public Iterator<T> iterator() {
    return new ObservableIterator();
  }

  @Override
  public Object[] toArray() {
    return getCollection().toArray();
  }

  @Override
  public <T2> T2[] toArray(final T2[] array) {
    return getCollection().toArray(array);
  }

  // Iterator
  //--------------------------------------------------

  protected class ObservableIterator extends IteratorDecorator<T> {

    // Constructors
    //--------------------------------------------------

    protected ObservableIterator(final Iterator<T> iterator) {
      super(iterator);
    }

    private ObservableIterator() {
      this(AbstractObservableCollection.this.getCollection().iterator());
    }

    // Fields
    //--------------------------------------------------

    protected T lastElement;

    // Iterator methods
    //--------------------------------------------------

    @Override
    public T next() {
      return (lastElement = super.next());
    }

    @Override
    public void remove() {
      super.remove();

      AbstractObservableCollection.this.fireElementRemovedEvent(lastElement);
    }

  }

}
