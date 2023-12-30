import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordNet {
    private final Map<String, ArrayList<Integer>> nounToId;
    private final Map<Integer, String> idToNoun;
    private int n;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("File name is null.");
        }

        nounToId = new HashMap<String, ArrayList<Integer>>();
        idToNoun = new HashMap<Integer, String>();
        n = 0;
        readSynsets(synsets);
        readHypernyms(hypernyms);
    }

    private void readSynsets(String synsets) {
        In in = new In(synsets);
        String line;
        while ((line = in.readLine()) != null) {
            String[] strs = line.split(",");
            if (strs.length < 2)
                continue;

            ++n;
            int id = Integer.parseInt(strs[0]);
            idToNoun.put(id, strs[1]);
            String[] nouns = strs[1].split(" ");
            for (String noun : nouns) {
                ArrayList<Integer> ids = nounToId.get(noun);
                if (ids != null) {
                    ids.add(id);
                }
                else {
                    ArrayList<Integer> nids = new ArrayList<Integer>();
                    nids.add(id);
                    nounToId.put(noun, nids);
                }
            }
        }
    }

    private void readHypernyms(String hypernyms) {
        In in = new In(hypernyms);
        String line;
        Digraph digraph = new Digraph(n);
        while ((line = in.readLine()) != null) {
            String[] strs = line.split(",");
            if (strs.length < 2)
                continue;

            int start = Integer.parseInt(strs[0]);
            for (int i = 1; i < strs.length; ++i)
                digraph.addEdge(start, Integer.parseInt(strs[i]));
        }

        DirectedCycle dc = new DirectedCycle(digraph);
        if (dc.hasCycle())
            throw new IllegalArgumentException("Cycle detected");

        int numRoot = 0;
        for (int i = 0; i < digraph.V(); ++i) {
            if (digraph.outdegree(i) == 0) {
                ++numRoot;
                if (numRoot > 1)
                    throw new IllegalArgumentException("More than 1 root");
            }
        }
        sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToId.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException("word is null");
        return nounToId.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Not a WordNet noun");
        return sap.length(nounToId.get(nounA), nounToId.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Not a WordNet noun");
        return idToNoun.get(sap.ancestor(nounToId.get(nounA), nounToId.get(nounB)));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        if (args.length > 1) {
            WordNet wordNet = new WordNet(args[0], args[1]);
            wordNet.isNoun("a");
        }
    }
}