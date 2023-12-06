import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private class Node implements Comparable<Node> {
        private final Board board;
        private final Node prev;
        private final int moves;
        private int prio;

        public Node(Board board, int moves, Node prev) {
            this.board = board;
            this.moves = moves;
            this.prev = prev;
            this.prio = moves + board.manhattan();
        }

        @Override
        public int compareTo(Node ot) {
            return Integer.compare(prio, ot.prio);
        }
    }

    private Node lastNode = null;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException();

        MinPQ<Node> queue = new MinPQ<>();
        Node head = new Node(initial, 0, null);
        queue.insert(head);

        while (!queue.isEmpty()) {
            Board curBoard = head.board;
            if (curBoard.twin().isGoal())
                return;

            if (curBoard.isGoal()) {
                lastNode = head;
                return;
            }
            
            for (Board neig : curBoard.neighbors()) {
                if (head.prev == null || !neig.equals(head.prev.board)) {
                    queue.insert(new Node(neig, head.moves + 1, head));
                }
            }
            head = queue.delMin();
        }
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return lastNode != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return isSolvable() ? lastNode.moves : -1;
    }

    // sequence of boards in the shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable())
            return null;

        Stack<Board> boards = new Stack<Board>();
        Node searchNode = lastNode;

        while (searchNode != null) {
            boards.push(searchNode.board);
            searchNode = searchNode.prev;
        }
        return boards;
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}