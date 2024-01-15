import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;

    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform() {
        String input = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(input);

        int first = 0;
        int len = input.length();
        for (int i = 0; i < len; i++)
            if (csa.index(i) == 0)
                first = i;

        BinaryStdOut.write(first);
        for (int i = 0; i < len; i++) {
            int lastIdx = (csa.index(i) - 1 + len) % len;
            BinaryStdOut.write(input.charAt(lastIdx));
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String lastCol = BinaryStdIn.readString();

        int n = lastCol.length();
        int[] count = new int[R + 1];
        for (int i = 0; i < n; i++)
            count[lastCol.charAt(i) + 1]++;
        for (int i = 0; i < R; i++)
            count[i + 1] += count[i];

        int[] next = new int[n];
        char[] firstCol = new char[n];
        for (int i = 0; i < n; i++) {
            int idx = count[lastCol.charAt(i)]++;
            firstCol[idx] = lastCol.charAt(i);
            next[idx] = i;
        }

        for (int i = 0; i < n; i++) {
            BinaryStdOut.write(firstCol[first]);
            first = next[first];
        }
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) BurrowsWheeler.transform();
        if (args[0].equals("+")) BurrowsWheeler.inverseTransform();
    }

}