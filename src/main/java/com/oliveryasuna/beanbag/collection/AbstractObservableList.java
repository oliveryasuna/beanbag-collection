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

import com.oliveryasuna.beanbag.collection.event.ListElementAddedEvent;
import com.oliveryasuna.beanbag.collection.event.ListElementChangedEvent;
import com.oliveryasuna.beanbag.collection.event.ListElementRemovedEvent;
import com.oliveryasuna.beanbag.collection.listener.ListElementAddedListener;
import com.oliveryasuna.beanbag.collection.listener.ListElementChangedListener;
import com.oliveryasuna.beanbag.collection.listener.ListElementRemovedListener;
import com.oliveryasuna.beanbag.helper.ListIteratorDecorator;
import com.oliveryasuna.commons.language.pattern.registry.Registration;
import org.apache.commons.lang3.event.EventListenerSupport;

import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractObservableList<T, LST extends List<T>, SUB extends AbstractObservableList<T, LST, SUB>>
    extends AbstractObservableCollection<T, LST, SUB> implements List<T> {

  // Constructors
  //--------------------------------------------------

  protected AbstractObservableList(final LST list) {
    super(list);
  }

  // Listener registries
  //--------------------------------------------------

  protected final EventListenerSupport<ListElementAddedListener> listElementAddedListeners =
      EventListenerSupport.create(ListElementAddedListener.class);

  protected final EventListenerSupport<ListElementRemovedListener> listElementRemovedListeners =
      EventListenerSupport.create(ListElementRemovedListener.class);

  protected final EventListenerSupport<ListElementChangedListener> listElementChangedListeners =
      EventListenerSupport.create(ListElementChangedListener.class);

  // Listener registration methods
  //--------------------------------------------------

  public Registration addElementAddedListener(final ListElementAddedListener<T, LST, SUB> listener) {
    listElementAddedListeners.addListener(listener);

    return (() -> listElementAddedListeners.removeListener(listener));
  }

  public void removedElementAddedListener(final ListElementAddedListener<T, LST, SUB> listener) {
    listElementAddedListeners.removeListener(listener);
  }

  public Registration addElementRemovedListener(final ListElementRemovedListener<T, LST, SUB> listener) {
    listElementRemovedListeners.addListener(listener);

    return (() -> listElementRemovedListeners.removeListener(listener));
  }

  public void removedElementRemovedListener(final ListElementRemovedListener<T, LST, SUB> listener) {
    listElementRemovedListeners.removeListener(listener);
  }

  public Registration addElementChangedListener(final ListElementChangedListener<T, LST, SUB> listener) {
    listElementChangedListeners.addListener(listener);

    return (() -> listElementChangedListeners.removeListener(listener));
  }

  public void changedElementChangedListener(final ListElementChangedListener<T, LST, SUB> listener) {
    listElementChangedListeners.removeListener(listener);
  }

  // Listener dispatch methods
  //--------------------------------------------------

  protected void fireElementAddedEvent(final T element, final int index) {
    fireElementAddedEvent(element);

    listElementAddedListeners.fire().elementAdded(new ListElementAddedEvent<>(element, index, (SUB)this));
  }

  protected void fireElementRemovedEvent(final T element, final int index) {
    fireElementRemovedEvent(element);

    listElementRemovedListeners.fire().elementRemoved(new ListElementRemovedEvent<>(element, index, (SUB)this));
  }

  protected void fireElementChangedEvent(final T newElement, final T oldElement, final int index) {
    listElementChangedListeners.fire().elementChanged(new ListElementChangedEvent<>(newElement, oldElement, index, (SUB)this));
  }

  // Getters/setters
  //--------------------------------------------------

  protected final LST getList() {
    return getCollection();
  }

  protected final void setList(final LST list) {
    setCollection(list);
  }

  // Collection methods
  //--------------------------------------------------

  @Override
  public boolean add(final T element) {
    final boolean modified = getList().add(element);

    if(modified) {
      fireElementAddedEvent(element, size() - 1);
    }

    return modified;
  }

  @Override
  public boolean addAll(final Collection<? extends T> collection) {
    final boolean modified = getList().addAll(collection);

    if(modified) {
      for(int i = getList().size() - collection.size(); i < getList().size(); i++) {
        fireElementAddedEvent(getList().get(i), i);
      }
    }

    return modified;
  }

  @Override
  public boolean remove(final Object element) {
    final int removedIndex = getList().indexOf(element);

    final boolean modified = getList().remove(element);

    if(modified) {
      fireElementRemovedEvent((T)element, removedIndex);
    }

    return modified;
  }

  @Override
  public boolean removeAll(final Collection<?> collection) {
    final List<T> copy = Collections.unmodifiableList(getList());

    final boolean modified = getList().removeAll(collection);

    if(modified) {
      for(int i = 0; i < copy.size(); i++) {
        final T element = copy.get(i);

        if(collection.contains(element)) {
          fireElementRemovedEvent(element, i);
        }
      }
    }

    return modified;
  }

  @Override
  public boolean removeIf(final Predicate<? super T> filter) {
    final List<T> copy = Collections.unmodifiableList(getList());

    final boolean modified = getList().removeIf(filter);

    if(modified) {
      for(int i = 0; i < copy.size(); i++) {
        final T element = copy.get(i);

        if(!getList().contains(element)) {
          fireElementRemovedEvent(element, i);
        }
      }
    }

    return modified;
  }

  @Override
  public boolean retainAll(final Collection<?> collection) {
    final List<T> copy = Collections.unmodifiableList(getList());

    final boolean modified = getList().retainAll(collection);

    if(modified) {
      for(int i = 0; i < copy.size(); i++) {
        final T element = copy.get(i);

        if(!collection.contains(element)) {
          fireElementRemovedEvent(element, i);
        }
      }
    }

    return modified;
  }

  @Override
  public void clear() {
    final List<T> removed = Collections.unmodifiableList(getList());

    getList().clear();

    for(int i = 0; i < removed.size(); i++) {
      fireElementRemovedEvent(removed.get(i), i);
    }
  }

  @Override
  public Iterator<T> iterator() {
    return new ObservableIterator();
  }

  // List methods
  //--------------------------------------------------

  @Override
  public T get(final int index) {
    return getList().get(index);
  }

  @Override
  public T set(final int index, final T element) {
    final T oldElement = getList().set(index, element);

    if(element != oldElement) {
      fireElementChangedEvent(element, oldElement, index);
    }

    return oldElement;
  }

  @Override
  public void add(final int index, final T element) {
    getList().add(index, element);

    fireElementAddedEvent(element, index);
  }

  @Override
  public boolean addAll(int index, final Collection<? extends T> collection) {
    final boolean modified = getList().addAll(index, collection);

    if(modified) {
      for(final T element : collection) {
        fireElementAddedEvent(element, index++);
      }
    }

    return modified;
  }

  @Override
  public T remove(final int index) {
    final T element = getList().remove(index);

    fireElementRemovedEvent(element, index);

    return element;
  }

  @Override
  public int indexOf(final Object element) {
    return getList().indexOf(element);
  }

  @Override
  public int lastIndexOf(final Object element) {
    return getList().lastIndexOf(element);
  }

  @Override
  public ListIterator<T> listIterator() {
    return new ObservableListIterator();
  }

  @Override
  public ListIterator<T> listIterator(final int index) {
    return new ObservableListIterator(index);
  }

  @Override
  public List<T> subList(final int fromIndex, final int toIndex) {
    return null; // TODO: Implement.
  }

  // Iterator
  //--------------------------------------------------

  protected class ObservableIterator extends AbstractObservableCollection<T, LST, SUB>.ObservableIterator {

    // Constructors
    //--------------------------------------------------

    protected ObservableIterator(final Iterator<T> iterator) {
      super(iterator);
    }

    private ObservableIterator() {
      this(AbstractObservableList.this.getList().iterator());
    }

    // Fields
    //--------------------------------------------------

    protected int lastIndex = -1;

    // Iterator methods
    //--------------------------------------------------

    @Override
    public T next() {
      lastElement = super.next();
      lastIndex++;

      return lastElement;
    }

    @Override
    public void remove() {
      super.remove();

      AbstractObservableList.this.fireElementRemovedEvent(lastElement, lastIndex--);
    }

  }

  // ListIterator
  //--------------------------------------------------

  protected class ObservableListIterator extends ListIteratorDecorator<T> {

    // Constructors
    //--------------------------------------------------

    protected ObservableListIterator(final ListIterator<T> listIterator) {
      super(listIterator);
    }

    private ObservableListIterator() {
      super(AbstractObservableList.this.getList().listIterator());
    }

    private ObservableListIterator(final int index) {
      super(AbstractObservableList.this.getList().listIterator(index));
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
      final int index = super.previousIndex();

      super.remove();

      AbstractObservableList.this.fireElementRemovedEvent(lastElement, index);
    }

    // ListIterator methods
    //--------------------------------------------------

    @Override
    public T previous() {
      return (lastElement = super.previous());
    }

    @Override
    public void set(final T element) {
      final int index = super.previousIndex();

      super.set(element);

      if(element != lastElement) {
        AbstractObservableList.this.fireElementChangedEvent(element, lastElement, index);
      }
    }

    @Override
    public void add(final T element) {
      final int index = super.previousIndex();

      super.add(element);

      AbstractObservableList.this.fireElementAddedEvent(element, index);
    }

  }

}
