/**
 * We can't use inheritance, so I made this class from the provided TrieST code
 * and added a custom method hasKeyStartingWith specifically made to improve the
 * BoggleSolver's speed.
 **/

public class CustomTrieST<Value> {
    private static final int R = 26;


    private Node root;      // root of trie

    // R-way trie node
    private static class Node {
        private Object val;
        private Node[] next = new Node[R];
    }

    public CustomTrieST() {
    }


    public Value get(String key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        Node x = get(root, key, 0);
        if (x == null) return null;
        return (Value) x.val;
    }

    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }

    public boolean hasKeyStartingWith(String prefix) {
        return get(root, prefix, 0) != null;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        int c = key.charAt(d) - 'A';
        return get(x.next[c], key, d + 1);
    }

    public void put(String key, Value val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, Value val, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            x.val = val;
            return x;
        }
        int c = key.charAt(d) - 'A';
        x.next[c] = put(x.next[c], key, val, d + 1);
        return x;
    }

}