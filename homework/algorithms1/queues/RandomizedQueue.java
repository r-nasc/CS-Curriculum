import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class RandomizedQueue<Item> implements Iterable<Item> {

    private class RandomizedIterator implements Iterator<Item> {
        private int sizeCopy = size;
        private Item[] shuffledItems;

        public RandomizedIterator() {
            shuffledItems = (Item[]) new Object[size];
            System.arraycopy(items, 0, shuffledItems, 0, size);
            StdRandom.shuffle(shuffledItems);
        }

        public boolean hasNext() {
            return sizeCopy != 0;
        }

        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException("No next element");
            return shuffledItems[--sizeCopy];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private Item[] items;
    private int size;

    // construct an empty randomized queue
    public RandomizedQueue() {
        size = 0;
        items = (Item[]) new Object[1];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null)
            throw new IllegalArgumentException("Item can't be null");

        if (size == items.length)
            resize(2 * items.length);

        items[size] = item;
        size++;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty())
            throw new NoSuchElementException("Deque is empty");

        int rand = StdRandom.uniformInt(size);
        Item node = items[rand];
        if (size - 1 == rand) {
            items[rand] = null;
        }
        else {
            items[rand] = items[size - 1];
            items[size - 1] = null;
        }

        if (size <= (items.length / 4))
            resize(items.length / 2);

        size--;
        return node;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty())
            throw new NoSuchElementException("Deque is empty");

        int rand = StdRandom.uniformInt(size);
        return items[rand];
    }

    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            copy[i] = items[i];
        }
        items = copy;
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedIterator();
    }

    public static void main(String[] args) {
        RandomizedQueue<Integer> rqueue = new RandomizedQueue<Integer>();
        rqueue.enqueue(1);
        rqueue.enqueue(2);
        rqueue.enqueue(3);
        rqueue.enqueue(4);
        rqueue.enqueue(5);
        rqueue.enqueue(6);
        rqueue.enqueue(7);
        rqueue.enqueue(8);

        for (int node : rqueue) {
            System.out.println(node + " ");
        }
    }
}