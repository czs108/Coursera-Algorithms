/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      1/8/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;

import java.util.Arrays;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String input = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(input);

        // find the input string
        for (int i = 0; i != csa.length(); ++i) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
            }
        }

        for (int i = 0; i != csa.length(); ++i) {
            int index = csa.index(i);
            // get the character after shifting the string left
            index = index - 1;
            if (index < 0) {
                index = csa.length() - 1;
            }

            BinaryStdOut.write(input.charAt(index));
        }

        BinaryStdOut.close();
        BinaryStdIn.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String chars = BinaryStdIn.readString();
        char[] t = chars.toCharArray();

        // get the row indices of characters in the last column
        ST<Character, Queue<Integer>> indexMap = new ST<Character, Queue<Integer>>();
        for (int i = 0; i != t.length; ++i) {
            char c = t[i];
            if (indexMap.contains(c)) {
                indexMap.get(c).enqueue(i);
            } else {
                Queue<Integer> indices = new Queue<Integer>();
                indices.enqueue(i);
                indexMap.put(c, indices);
            }
        }

        // get the first column by sorting the last column
        Arrays.sort(t);

        // match the characters in the last and first columns
        int[] next = new int[t.length];
        for (int i = 0; i != t.length; ++i) {
            next[i] = indexMap.get(t[i]).dequeue();
        }

        // reconstruct the original input string
        for (int i = 0, j = first; i != t.length; ++i) {
            BinaryStdOut.write(t[j]);
            j = next[j];
        }

        BinaryStdOut.close();
        BinaryStdIn.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }

        if (args[0].equals("-")) {
            transform();
        } else if (args[0].equals("+")) {
            inverseTransform();
        }
    }
}
