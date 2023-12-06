import java.util.ArrayList;
import java.util.List;

public class Board {

    private final int[][] board;
    private final int n;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null)
            throw new java.lang.IllegalArgumentException();

        if (tiles.length != tiles[0].length)
            throw new java.lang.IllegalArgumentException();

        n = tiles.length;
        board = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(tiles[i], 0, board[i], 0, n);
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(n).append("\n");

        for (int[] row : board) {
            for (int c : row)
                sb.append(c).append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int dist = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (cellHamming(i, j) != 0)
                    dist++;
            }
        }
        return dist;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int dist = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist += cellHamming(i, j);
            }
        }
        return dist;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null || y.getClass() != this.getClass())
            return false;

        Board oth = (Board) y;
        if (dimension() != oth.dimension())
            return false;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] != oth.board[i][j])
                    return false;
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        List<Board> neighbors = new ArrayList<Board>();

        int zeroX = 0, zeroY = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 0) {
                    zeroX = i;
                    zeroY = j;
                    break;
                }
            }
        }

        if (zeroX > 0) {
            Board copyBoard = new Board(board);
            copyBoard.exch(zeroX, zeroY, zeroX - 1, zeroY);
            neighbors.add(copyBoard);
        }

        if (zeroY > 0) {
            Board copyBoard = new Board(board);
            copyBoard.exch(zeroX, zeroY, zeroX, zeroY - 1);
            neighbors.add(copyBoard);
        }

        if (zeroX < n - 1) {
            Board copyBoard = new Board(board);
            copyBoard.exch(zeroX, zeroY, zeroX + 1, zeroY);
            neighbors.add(copyBoard);
        }

        if (zeroY < n - 1) {
            Board copyBoard = new Board(board);
            copyBoard.exch(zeroX, zeroY, zeroX, zeroY + 1);
            neighbors.add(copyBoard);
        }

        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        Board copyBoard = new Board(board);
        if (board[0][0] != 0) {
            if (board[0][1] != 0)
                copyBoard.exch(0, 1, 0, 0);
            else
                copyBoard.exch(1, 0, 0, 0);
        }
        else {
            copyBoard.exch(1, 0, 0, 1);
        }
        return copyBoard;
    }

    private int cellHamming(int i, int j) {
        int val = board[i][j];
        if (val == 0) return 0;

        int correctX = (val - 1) / n;
        int correctY = (val - 1) % n;
        return Math.abs(correctX - i) + Math.abs(correctY - j);
    }

    private void exch(int i1, int j1, int i2, int j2) {
        int tmp = board[i1][j1];
        board[i1][j1] = board[i2][j2];
        board[i2][j2] = tmp;
    }

    // unit testing (not graded)
    public static void main(String[] args) {

    }
}