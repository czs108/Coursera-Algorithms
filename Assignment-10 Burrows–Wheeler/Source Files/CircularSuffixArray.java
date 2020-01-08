/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      1/8/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray {

    private final String input;

    // the indices of original suffixes
    private final Integer[] indices;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        input = s;
        indices = new Integer[s.length()];
        for (int i = 0; i != indices.length; ++i) {
            indices[i] = i;
        }

        // do not store the shifted strings explicitly
        // compare two strings by setting different initial indices
        Comparator<Integer> comparator = (first, second) -> {
            int p = first;
            int q = second;
            for (int i = 0; i != input.length(); ++i) {
                if (input.charAt(p) > input.charAt(q)) {
                    return 1;
                } else if (input.charAt(p) < input.charAt(q)) {
                    return -1;
                }

                p = (p + 1) % input.length();
                q = (q + 1) % input.length();
            }

            return 0;
        };

        Arrays.sort(indices, comparator);
    }

    // length of s
    public int length() {
        return input.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length()) {
            throw new IllegalArgumentException("[!] The index is invalid");
        }

        return indices[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        for (int i = 0; i != csa.length(); ++i) {
            StdOut.println(csa.index(i));
        }
    }
}
