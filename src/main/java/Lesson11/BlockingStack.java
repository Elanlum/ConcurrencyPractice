package Lesson11;

import java.util.NoSuchElementException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingStack<E> {

  private final Object[] data;
  private final Lock lock;
  private final Condition notFull;
  private final Condition notEmpty;

  private int count = 0;

  public BlockingStack(int size) {
    this(size, false);
  }

  public BlockingStack(int size, boolean fair) {
    data = new Object[size];
    lock = new ReentrantLock(fair);
    notEmpty = lock.newCondition();
    notFull = lock.newCondition();
  }

  /**
   * Removes the object at the top of this stack and returns that object as the value of this
   * function.
   * @return Gives the removed object.
   */
  public E pop() throws InterruptedException {
    lock.lock();
    try {
      while (count == data.length) {
        notFull.await();
      }
      E result = peek();
      if (result != null) {
        data[0] = null;
        for (int i = 1; i <= data.length; i++) {
          data[i - 1] = data[i];
        }
        --count;
        return result;
      } else {
        throw new NoSuchElementException();
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * Pushes an item onto the top of this stack.
   * @return The item object itself
   */
  public E push(E item) throws InterruptedException {
    lock.lock();
    try {
      while (count == 0) {
        notEmpty.await();
      }
      if (data[data.length - 1] == null) {
        for (int i = 0; i < data.length - 1; i++) {
          E prev = takeAt(i);
          data[i + 1] = prev;
        }
        return item;
      }
      else {
        throw new NoSuchElementException();
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * Looks at the object at the top of this stack without removing it from the stack.
   * @return The item object itself
   */
  public E peek() {
    lock.lock();
    try {
      return (E) data[0];
    } finally {
      lock.unlock();
    }
  }

  @SuppressWarnings("unchecked")
  public E takeAt(int index) {
    return (E) data[index];
  }

}


