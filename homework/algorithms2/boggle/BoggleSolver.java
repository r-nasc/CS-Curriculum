import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.HashSet;


public class BoggleSolver {
    private final CustomTrieST<Integer> dictionary;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null)
            throw new IllegalArgumentException("invalid dictionary");

        this.dictionary = new CustomTrieST<Integer>();
        for (String word : dictionary) {
            int len = word.length();
            int score = 11;

            if (len <= 2) score = 0;
            else if (len <= 4) score = 1;
            else if (len == 5) score = 2;
            else if (len == 6) score = 3;
            else if (len == 7) score = 5;

            this.dictionary.put(word, score);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        int rows = board.rows();
        int cols = board.cols();

        HashSet<String> validWords = new HashSet<>();
        boolean[][] visited = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                dfs(board, i, j, visited, "", validWords);
            }
        }
        return validWords;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        return this.dictionary.contains(word) ? this.dictionary.get(word) : 0;
    }

    private void dfs(BoggleBoard board, int i, int j, boolean[][] visited, String prefix,
                     HashSet<String> validWords) {
        char letter = board.getLetter(i, j);
        String curPrefix = prefix + (letter == 'Q' ? "QU" : letter);

        if (!dictionary.hasKeyStartingWith(curPrefix))
            return;

        if (curPrefix.length() >= 3 && dictionary.contains(curPrefix))
            validWords.add(curPrefix);

        visited[i][j] = true;
        for (int x = i - 1; x <= i + 1; x++) {
            for (int y = j - 1; y <= j + 1; y++) {
                if (isValidCell(x, y, board) && !visited[x][y])
                    dfs(board, x, y, visited, curPrefix, validWords);
            }
        }
        visited[i][j] = false;
    }

    // Helper method to check if the cell coordinates are valid
    private boolean isValidCell(int i, int j, BoggleBoard board) {
        return i >= 0 && i < board.rows() && j >= 0 && j < board.cols();
    }

    public static void main(String[] args) {
        BoggleBoard[] board = new BoggleBoard[1000];
        for (int i = 0; i < 1000; i++)
            board[i] = new BoggleBoard();

        In dictIn = new In("./dictionary-algs4.txt");
        String[] dictionary = dictIn.readAllStrings();
        BoggleSolver bs = new BoggleSolver(dictionary);

        Stopwatch sw = new Stopwatch();
        for (BoggleBoard b : board)
            bs.getAllValidWords(b);
        System.out.println(sw.elapsedTime());
    }
}