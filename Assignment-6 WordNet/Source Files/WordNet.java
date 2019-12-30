/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/26/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.TreeMap;

public class WordNet {

    private static final int INVALID = -1;

    /*** Field Index ***/
    // in synsets.txt and hypernyms.txt files
    private static final int IDX_ID = 0;

    // in synsets.txt file
    private static final int IDX_SYNSET = 1;

    // in hypernyms.txt file
    private static final int IDX_HYPERNYM = 1;

    // list of every noun's ids
    // an identical noun may exist in multiple lines and synsets
    private final TreeMap<String, Bag<Integer>> nounIds = new TreeMap<String, Bag<Integer>>();

    // list of synsets
    private final ArrayList<String> synsets = new ArrayList<String>();

    // net of words
    private final Digraph synsetNet;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        int synsetCount = initSynsets(new In(synsets));
        synsetNet = new Digraph(synsetCount);
        initHypernyms(synsetCount, new In(hypernyms));
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounIds.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        return nounIds.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        checkNoun(nounA);
        checkNoun(nounB);

        SAP sap = new SAP(synsetNet);
        return sap.length(nounIds.get(nounA), nounIds.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        checkNoun(nounA);
        checkNoun(nounB);

        SAP sap = new SAP(synsetNet);
        int id = sap.ancestor(nounIds.get(nounA), nounIds.get(nounB));
        return id != INVALID ? synsets.get(id) : null;
    }

    private int initSynsets(In in) {
        assert (in != null);

        int count = 0;
        while (in.hasNextLine()) {
            String[] line = in.readLine().split(",");
            int id = Integer.parseInt(line[IDX_ID]);

            // store the synset
            String synset = line[IDX_SYNSET];
            synsets.add(synset);
            // get the nouns in the synset
            String[] nouns = synset.split(" ");
            for (String noun : nouns) {
                // store the ids of each noun
                if (nounIds.containsKey(noun)) {
                    nounIds.get(noun).add(id);
                } else {
                    Bag<Integer> ids = new Bag<Integer>();
                    ids.add(id);
                    nounIds.put(noun, ids);
                }
            }

            ++count;
        }

        return count;
    }

    private void initHypernyms(int synsetCount, In in) {
        assert (in != null);

        boolean[] isNotRoot = new boolean[synsetCount];
        while (in.hasNextLine()) {
            String[] line = in.readLine().split(",");
            int id = Integer.parseInt(line[IDX_ID]);
            isNotRoot[id] = true;

            // add connects between the synset and its hypernyms
            for (int i = IDX_HYPERNYM; i != line.length; ++i) {
                synsetNet.addEdge(id, Integer.parseInt(line[i]));
            }
        }

        // check the number of roots
        int rootCount = 0;
        for (int i = 0; i != isNotRoot.length; ++i) {
            if (!isNotRoot[i]) {
                ++rootCount;
            }
        }

        if (rootCount > 1 || (new DirectedCycle(synsetNet)).hasCycle()) {
            throw new IllegalArgumentException("[!] The input contains multiple roots or a cycle");
        }
    }

    private void checkNoun(String noun) {
        if (noun == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        if (!isNoun(noun)) {
            throw new IllegalArgumentException("[!] The argument must be a WordNet noun");
        }
    }
}
