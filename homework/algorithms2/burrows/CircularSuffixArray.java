import java.util.Arrays;

public class CircularSuffixArray {
    private int n;
    private char[] val;
    private Integer[] idx;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("invalid string");

        n = s.length();
        val = new char[n];
        idx = new Integer[n];
        for (int i = 0; i < n; i++) {
            idx[i] = i;
            val[i] = s.charAt(i);
        }

        Arrays.sort(idx, (idx1, idx2) -> {
            for (int i = 0; i < n; i++) {
                char c1 = val[(i + idx1) % n];
                char c2 = val[(i + idx2) % n];
                if (c1 > c2) return 1;
                if (c1 < c2) return -1;
            }
            return 0;
        });
    }

    // length of s
    public int length() {
        return n;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= n)
            throw new IllegalArgumentException("invalid index");
        return idx[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        for (int i = 0; i < csa.length(); i++)
            System.out.print(csa.index(i) + " ");
    }
}