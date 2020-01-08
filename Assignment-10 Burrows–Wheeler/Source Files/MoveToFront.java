/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      1/8/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;
import java.util.List;

public class MoveToFront {

    private static final int R = 256;

    private static final int BITS = 8;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        List<Character> alphabet = initAlphabet();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int index = alphabet.indexOf(c);
            BinaryStdOut.write(index, BITS);

            alphabet.remove(index);
            alphabet.add(0, c);
        }

        BinaryStdOut.close();
        BinaryStdIn.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        List<Character> alphabet = initAlphabet();
        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readChar();
            char c = alphabet.get(index);
            BinaryStdOut.write(c, BITS);

            alphabet.remove(index);
            alphabet.add(0, c);
        }

        BinaryStdOut.close();
        BinaryStdIn.close();
    }

    private static List<Character> initAlphabet() {
        List<Character> alphabet = new LinkedList<Character>();
        for (int i = 0; i != R; ++i) {
            alphabet.add((char) i);
        }

        return alphabet;
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }

        if (args[0].equals("-")) {
            encode();
        } else if (args[0].equals("+")) {
            decode();
        }
    }
}
