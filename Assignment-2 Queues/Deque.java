/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/2/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private final class Node {

        public final Item item;
        public Node prev;
        public Node next;

        public Node(Item item) {
            this.item = item;
        }
    }

    private class DequeIterator implements Iterator<Item> {

        private Node current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException("[!] The deque is empty");
            }

            Item item = current.item;
            current = current.next;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                    "[!] The iterator does not support remove method");
        }
    }

    // the first and the last items
    private Node first;
    private Node last;

    // number of items
    private int count = 0;

    // construct an empty deque
    public Deque() { }

    // is the deque empty?
    public boolean isEmpty() {
        return count == 0;
    }

    // return the number of items on the deque
    public int size() {
        return count;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        Node newNode = new Node(item);
        newNode.next = first;
        if (first != null) {
            first.prev = newNode;
        } else {
            // the deque is empty
            last = newNode;
        }

        first = newNode;
        ++count;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        Node newNode = new Node(item);
        newNode.prev = last;
        if (last != null) {
            last.next = newNode;
        } else {
            // the deque is empty
            first = newNode;
        }

        last = newNode;
        ++count;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("[!] The deque is empty");
        }

        Item item = first.item;
        Node nextNode = first.next;
        if (nextNode != null) {
            nextNode.prev = null;
        } else {
            // the deque is empty
            last = null;
        }

        first = nextNode;
        --count;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("[!] The deque is empty");
        }

        Item item = last.item;
        Node prevNode = last.prev;
        if (prevNode != null) {
            prevNode.next = null;
        } else {
            // the deque is empty
            first = null;
        }

        last = prevNode;
        --count;
        return item;
    }

    // return an iterator over items in order from front to back
    @Override
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> deque = new Deque<String>();
        StdOut.println("[*] Add the items to the first position: 1, 2, 3");
        deque.addFirst("1");
        deque.addFirst("2");
        deque.addFirst("3");
        StdOut.println("[*] Add the items to the last position: 4, 5, 6");
        deque.addLast("4");
        deque.addLast("5");
        deque.addLast("6");

        StdOut.println("[*] The size is " + Integer.toString(deque.size()));
        StdOut.println("[*] Remove the first item: " + deque.removeFirst());
        StdOut.println("[*] Remove the first item: " + deque.removeFirst());
        StdOut.println("[*] Remove the last item: " + deque.removeLast());
        StdOut.println("[*] Remove the last item: " + deque.removeLast());
        StdOut.println("[*] The size is " + Integer.toString(deque.size()));

        StdOut.println("[*] The remaining items are:");
        for (String i : deque) {
            StdOut.println("\t" + i);
        }
    }
}
