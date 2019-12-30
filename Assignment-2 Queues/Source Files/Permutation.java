/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/2/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }

        int count = Integer.parseInt(args[0]);

        RandomizedQueue<String> queue = new RandomizedQueue<String>();
        while (!StdIn.isEmpty()) {
            queue.enqueue(StdIn.readString());
        }

        for (int i = 0; i != count; ++i) {
            StdOut.println(queue.dequeue());
        }
    }
}
