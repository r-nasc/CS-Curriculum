import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] aux = new char[R];
        for (char i = 0; i < R; i++)
            aux[i] = i;

        String input = BinaryStdIn.readString();
        for (int i = 0; i < input.length(); i++)
            BinaryStdOut.write(move(aux, input.charAt(i)));
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] aux = new char[R];
        for (char i = 0; i < R; i++)
            aux[i] = i;

        while (!BinaryStdIn.isEmpty()) {
            char c = aux[BinaryStdIn.readInt(8)];
            BinaryStdOut.write(c);
            move(aux, c);
        }
        BinaryStdOut.close();
    }

    private static char move(char[] aux, char c) {
        char prev = aux[0];
        for (int i = 0; i < R; i++) {
            // When it reaches c, swap to front and stop
            if (aux[i] == c) {
                aux[0] = c;
                aux[i] = prev;
                return (char) i;
            }
            // Swap with next
            char tmp = aux[i];
            aux[i] = prev;
            prev = tmp;
        }
        return 0;
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) MoveToFront.encode();
        if (args[0].equals("+")) MoveToFront.decode();
    }

}
