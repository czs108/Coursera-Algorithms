/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/2/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private class RandomizedQueueIterator implements Iterator<Item> {

        private int current = 0;

        private Item[] iterItems;

        private RandomizedQueueIterator() {
            if (isEmpty()) {
                return;
            }

            iterItems = (Item[]) new Object[count];
            for (int i = 0; i != count; ++i) {
                iterItems[i] = items[i];
            }

            // make the order random
            StdRandom.shuffle(iterItems);
        }

        @Override
        public boolean hasNext() {
            if (iterItems != null) {
                return current != iterItems.length;
            } else {
                return false;
            }
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException("[!] The queue is empty");
            }
            
            return iterItems[current++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                    "[!] The iterator does not support remove method");
        }
    }

    private Item[] items = (Item[]) new Object[1];

    // number of items
    private int count = 0;

    // construct an empty randomized queue
    public RandomizedQueue() { }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return count == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return count;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        items[count++] = item;

        // increase capacity
        if (count == items.length) {
            resize(items.length * 2);
        }
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("[!] The queue is empty");
        }

        int index = StdRandom.uniform(0, count);
        Item item = items[index];
        moveLastItemTo(index);

        // decrease capacity
        if (--count == items.length / 4) {
            resize(items.length / 2);
        }

        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException("[!] The queue is empty");
        }

        return items[StdRandom.uniform(0, count)];
    }

    // return an independent iterator over items in random order
    @Override
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private void resize(int capacity) {
        assert (capacity > count);

        // copy items
        Item[] newSpace = (Item[]) new Object[capacity];
        for (int i = 0; i != count; ++i) {
            newSpace[i] = items[i];
        }

        items = newSpace;
    }

    // move the last item to the specific position,
    // and set the last position to null
    private void moveLastItemTo(int index) {
        assert (!isEmpty());
        assert (0 <= index && index < count);

        items[index] = items[count - 1];
        items[count - 1] = null;
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<String> queue = new RandomizedQueue<String>();
        StdOut.println("[*] Add the items: 1, 2, 3, 4, 5, 6");
        queue.enqueue("1");
        queue.enqueue("2");
        queue.enqueue("3");
        queue.enqueue("4");
        queue.enqueue("5");
        queue.enqueue("6");

        StdOut.println("[*] The size is " + Integer.toString(queue.size()));
        StdOut.println("[*] Print all items randomly first time:");
        for (String i : queue) {
            StdOut.println("\t" + i);
        }

        StdOut.println("[*] Print all items randomly second time:");
        for (String i : queue) {
            StdOut.println("\t" + i);
        }

        StdOut.println("[*] Remove an item randomly: " + queue.dequeue());
        StdOut.println("[*] Remove an item randomly: " + queue.dequeue());
        StdOut.println("[*] Get an item randomly: " + queue.sample());
        StdOut.println("[*] The size is " + Integer.toString(queue.size()));

        StdOut.println("[*] The remaining items are:");
        for (String i : queue) {
            StdOut.println("\t" + i);
        }
    }
}
