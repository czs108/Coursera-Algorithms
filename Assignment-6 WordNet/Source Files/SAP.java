/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/27/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private static final int INVALID = -1;

    private final class SynsetNode implements Comparable<SynsetNode> {

        // id of current synset node
        public final int id;

        // length to target synset node
        public final int length;

        public SynsetNode(int id, int length) {
            assert (isIdValid(id));
            assert (length >= 0);

            this.id = id;
            this.length = length;
        }

        @Override
        public int compareTo(SynsetNode that) {
            if (that == null) {
                throw new IllegalArgumentException("[!] The argument can not be null");
            }

            return Integer.compare(length, that.length);
        }
    }

    private final Digraph wordnet;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null || G.V() == 0) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        wordnet = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        SynsetNode ancestor = ancestorNode(v, w);
        return ancestor != null ? ancestor.length : INVALID;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        SynsetNode ancestor = ancestorNode(v, w);
        return ancestor != null ? ancestor.id : INVALID;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        SynsetNode ancestor = ancestorNode(v, w);
        return ancestor != null ? ancestor.length : INVALID;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        SynsetNode ancestor = ancestorNode(v, w);
        return ancestor != null ? ancestor.id : INVALID;
    }

    private boolean isIdValid(int id) {
        return (0 <= id && id < wordnet.V());
    }

    private SynsetNode ancestorNode(int v, int w) {
        if (!isIdValid(v) || !isIdValid(w)) {
            throw new IllegalArgumentException(
                    "[!] The noun id is an integer between 0 and " +
                            Integer.toString(wordnet.V() - 1));
        }

        if (v == w) {
            return new SynsetNode(v, 0);
        }

        BreadthFirstDirectedPaths bfdOfV = new BreadthFirstDirectedPaths(wordnet, v);
        BreadthFirstDirectedPaths bfdOfW = new BreadthFirstDirectedPaths(wordnet, w);
        return ancestorNode(bfdOfV, bfdOfW);
    }

    private SynsetNode ancestorNode(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        try {
            BreadthFirstDirectedPaths bfdOfV = new BreadthFirstDirectedPaths(wordnet, v);
            BreadthFirstDirectedPaths bfdOfW = new BreadthFirstDirectedPaths(wordnet, w);
            return ancestorNode(bfdOfV, bfdOfW);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }
    }

    private SynsetNode ancestorNode(BreadthFirstDirectedPaths v, BreadthFirstDirectedPaths w) {
        assert (v != null && w != null);

        int minLength = Integer.MAX_VALUE;
        int minId = INVALID;
        for (int i = 0; i != wordnet.V(); ++i) {
            if (v.hasPathTo(i) && w.hasPathTo(i)) {
                int length = v.distTo(i) + w.distTo(i);
                if (length < minLength) {
                    minLength = length;
                    minId = i;
                }
            }
        }

        return minId != INVALID ? new SynsetNode(minId, minLength) : null;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }

        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
