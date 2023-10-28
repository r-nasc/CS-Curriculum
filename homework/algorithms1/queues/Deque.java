import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private class Node {
        Item item;
        Node next;
        Node prev;
    }

    private class DequeIterator implements Iterator<Item> {
        private Node current;

        public DequeIterator() {
            current = Deque.this.first;
        }

        // Checks if the next element exists
        public boolean hasNext() {
            return current != null && current.item != null;
        }

        // moves the cursor/iterator to next element
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No next element");
            }
            Item oldFirst = current.item;
            current = current.next;
            return oldFirst;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }
    }

    private Node first;
    private Node last;
    private int size;

    // construct an empty deque
    public Deque() {
        first = null;
        last = null;
        size = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null)
            throw new IllegalArgumentException("Item can't be null");

        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;
        size++;

        if (size == 1) {
            last = first;
        }
        else {
            oldFirst.prev = first;
        }
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null)
            throw new IllegalArgumentException("Item can't be null");

        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.prev = oldLast;
        size++;

        if (size == 1) {
            first = last;
        }
        else {
            oldLast.next = last;
        }
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty())
            throw new NoSuchElementException("Deque is empty");

        Item item = first.item;
        first = first.next;
        size--;

        if (isEmpty()) {
            last = null;
        }
        else {
            first.prev = null;
        }
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty())
            throw new NoSuchElementException("Deque is empty");

        Item item = last.item;
        last = last.prev;
        size--;

        if (isEmpty()) {
            first = null;
        }
        else {
            last.next = null;
        }
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        assert deque.isEmpty();

        deque.addFirst(1);
        deque.addFirst(2);
        assert 2 == deque.size();

        deque.addFirst(3);
        Integer first = deque.removeFirst();
        assert 3 == first;

        deque.addLast(4);
        Integer last = deque.removeLast();
        assert 4 == last;

        Iterator<Integer> it = deque.iterator();
        assert it.hasNext();
        assert it.next() == 2;
        assert it.hasNext();
        assert it.next() == 1;

        Deque<Integer> deque2 = new Deque<>();
        deque2.addFirst(1);
        Iterator<Integer> it2 = deque.iterator();
        assert it2.hasNext();
        assert it2.next() == 1;

        System.out.println("Deque is working fine");
    }
}