/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      1/7/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.TreeSet;

public class BoggleSolver {

    private static final int MIN_LENGTH = 3;

    // store the row and column indices
    private static final class Position {

        public final int row;
        public final int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private static class Trie {
        // Radix, English alphabet
        private static final int R = 26;

        private static final char OFFSET = 'A';

        private Node root = new Node();

        public static class Node {
            public boolean valid;
            public Node[] next = new Node[R];
        }

        public static boolean isNodeValid(Node node) {
            return node != null && node.valid;
        }

        public void put(String key) {
            root = put(root, key, 0);
        }

        public boolean contains(String key) {
            Node node = get(root, key, 0);
            return isNodeValid(node);
        }

        public Node getRoot() {
            return root;
        }

        public Node getNext(Node node, char c) {
            assert ('A' <= c && c <= 'Z');

            return node != null ? node.next[c - OFFSET] : null;
        }

        private Node put(Node node, String key, int index) {
            assert (key != null);
            assert (index >= 0);

            if (node == null) {
                node = new Node();
            }

            if (index != key.length()) {
                int c = key.charAt(index) - OFFSET;
                node.next[c] = put(node.next[c], key, index + 1);
            } else {
                node.valid = true;
            }

            return node;
        }

        private Node get(Node node, String key, int index) {
            assert (key != null);
            assert (index >= 0);

            if (node == null) {
                return null;
            }

            if (index != key.length()) {
                int c = key.charAt(index) - OFFSET;
                return get(node.next[c], key, index + 1);
            } else {
                return node;
            }
        }
    }

    // R-way trie
    private final Trie dict = new Trie();

    private BoggleBoard board;

    private Bag<Integer>[] adj;

    private TreeSet<String> validWords;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        for (String word : dictionary) {
            dict.put(word);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        this.board = board;
        buildAdjacency();
        validWords = new TreeSet<String>();

        // Depth-First-Search
        Trie.Node root = dict.getRoot();
        for (int i = 0; i != board.rows() * board.cols(); ++i) {
            boolean[] marked = new boolean[board.rows() * board.cols()];
            marked[i] = true;

            /*
             * we need a stack to help 'marked' to record and revert search progress
             *
             * e.g. Both "PINES" and "PIDS" are valid words
             * 'P', 'I', 'N', 'E', 'S' all will be marked when we finish the search of "PINES"
             * but when we get "PID", we won't get the last 'S' because it has been marked
             *
             * we use a stack to record each dice on the search path.
             * push and mark the dice when visiting it, pop and unmark the dice when finishing its follow-up search
             */
            Stack<Integer> visitingDices = new Stack<Integer>();
            visitingDices.push(i);

            char c = getLetter(i);
            if (c != 'Q') {
                searchValidWords(i, dict.getNext(root, c), c + "", marked, visitingDices);
            } else {
                searchValidWords(i, dict.getNext(dict.getNext(root, 'Q'), 'U'), "QU", marked, visitingDices);
            }
        }

        return validWords;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        if (!dict.contains(word)) {
            return 0;
        } else {
            switch (word.length()) {
                case 0:
                case 1:
                case 2:
                    return 0;
                case 3:
                case 4:
                    return 1;
                case 5:
                    return 2;
                case 6:
                    return 3;
                case 7:
                    return 5;
                default:
                    return 11;
            }
        }
    }

    // use 'node' and 'prefix' to record current search progress in the trie dictionary
    private void searchValidWords(int index, Trie.Node node, String prefix, boolean[] marked, Stack<Integer> visitingDices) {
        assert (validWords != null);
        assert (adj != null);

        // get a new valid word
        if (Trie.isNodeValid(node) && prefix.length() >= MIN_LENGTH) {
            validWords.add(prefix);
        }

        // do Depth-First-Search
        for (int v : adj[index]) {
            char c = getLetter(v);
            Trie.Node next = dict.getNext(node, c);
            if (!marked[v] && next != null) {
                // push and mark the dice when visiting it
                visitingDices.push(v);
                marked[v] = true;

                if (c != 'Q') {
                    searchValidWords(v, next, prefix + c, marked, visitingDices);
                } else {
                    searchValidWords(v, dict.getNext(next, 'U'), prefix + "QU", marked, visitingDices);
                }

                // pop and unmark the dice when finishing its follow-up search
                marked[visitingDices.pop()] = false;
            }
        }
    }

    private void buildAdjacency() {
        assert (board != null);

        adj = (Bag<Integer>[]) new Bag[board.rows() * board.cols()];
        for (int i = 0; i != board.rows(); ++i) {
            for (int j = 0; j != board.cols(); ++j) {
                int idx = getLinearIndex(i, j);
                adj[idx] = new Bag<Integer>();

                Iterable<Position> neighbors = getNeighbors(i, j);
                for (Position pos : neighbors) {
                    adj[idx].add(getLinearIndex(pos.row, pos.col));
                }
            }
        }
    }

    // all neighboring dices
    private Iterable<Position> getNeighbors(int row, int col) {
        Position[] blankCandidates = new Position[] {
            new Position(row - 1, col - 1),
            new Position(row - 1, col),
            new Position(row - 1, col + 1),
            new Position(row, col - 1),
            new Position(row, col + 1),
            new Position(row + 1, col - 1),
            new Position(row + 1, col),
            new Position(row + 1, col + 1)
        };

        ArrayList<Position> neighbors = new ArrayList<Position>();
        for (Position candidate : blankCandidates) {
            if (isDiceValid(candidate.row, candidate.col)) {
                neighbors.add(candidate);
            }
        }

        return neighbors;
    }

    // transform the board indices into a linear index
    private int getLinearIndex(int row, int col) {
        assert (isDiceValid(row, col));

        return row * board.cols() + col;
    }

    // get the letter from a linear index
    // (with 'Q' representing the two-letter sequence "Qu")
    private char getLetter(int index) {
        assert (board != null);

        int row = index / board.cols();
        int col = index % board.cols();

        assert (isDiceValid(row, col));

        return board.getLetter(row, col);
    }

    private boolean isDiceValid(int row, int col) {
        assert (board != null);

        return (0 <= row && row < board.rows()) && (0 <= col && col < board.cols());
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            return;
        }

        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
