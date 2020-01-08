/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/27/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class Outcast {

    private static final class NounNode implements Comparable<NounNode> {

        // id of current noun
        public final int id;

        // distance to target noun
        public int distance;

        public NounNode(int id, int distance) {
            assert (id >= 0);
            assert (distance >= 0);

            this.id = id;
            this.distance = distance;
        }

        @Override
        public int compareTo(NounNode that) {
            if (that == null) {
                throw new IllegalArgumentException("[!] The argument can not be null");
            }

            return Integer.compare(distance, that.distance);
        }
    }

    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        if (wordnet == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        if (nouns.length < 2) {
            throw new IllegalArgumentException("[!] The argument must contain at least two nouns");
        }

        NounNode[] nodes = new NounNode[nouns.length];
        for (int i = 0; i < nouns.length; i++) {
            nodes[i] = new NounNode(i, 0);
        }

        for (int i = 0; i < nouns.length; i++) {
            // starting with i + 1 can save time
            for (int j = i + 1; j < nouns.length; j++) {
                int distance = wordnet.distance(nouns[i], nouns[j]);
                nodes[i].distance += distance;
                nodes[j].distance += distance;
            }
        }

        Arrays.sort(nodes);
        return nouns[nodes[nodes.length - 1].id];
    }

    // test client
    public static void main(String[] args) {
        if (args.length < 3) {
            return;
        }

        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
