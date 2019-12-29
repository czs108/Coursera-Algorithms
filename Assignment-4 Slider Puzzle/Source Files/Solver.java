/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/2/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private static final class SearchNode implements Comparable<SearchNode> {

        private final Board board;

        private final int moveCount;

        // use Manhattan Distance
        private final int distance;

        private final int priority;

        private final SearchNode prevNode;

        public SearchNode(Board initial) {
            this(initial, null);
        }

        public SearchNode(Board current, SearchNode prevNode) {
            if (current == null) {
                throw new IllegalArgumentException("[!] The argument can not be null");
            }

            board = current;
            if (prevNode != null) {
                moveCount = prevNode.moveCount + 1;
            } else {
                moveCount = 0;
            }

            distance = current.manhattan();
            priority = distance + moveCount;
            this.prevNode = prevNode;
        }

        @Override
        public int compareTo(SearchNode that) {
            return priority - that.priority;
        }

        public int getMoveCount() {
            return moveCount;
        }

        public Board getBoard() {
            return board;
        }

        public boolean isGoal() {
            return board.isGoal();
        }

        public SearchNode getPrevNode() {
            return prevNode;
        }
    }

    private SearchNode lastNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        /*
         * Detecting Unsolvable Boards
         *
         * exactly one of the two initial boards will lead to the goal board
         */
        lastNode = new SearchNode(initial);
        MinPQ<SearchNode> minInitialPQ = new MinPQ<>();
        minInitialPQ.insert(lastNode);

        SearchNode lastTwinNode = new SearchNode(initial.twin());
        MinPQ<SearchNode> minTwinPQ = new MinPQ<>();
        minTwinPQ.insert(lastTwinNode);

        while (true) {
            lastNode = minInitialPQ.delMin();
            if (!lastNode.isGoal()) {
                putNeighbors(lastNode, minInitialPQ);
            } else {
                break;
            }

            lastTwinNode = minTwinPQ.delMin();
            if (!lastTwinNode.isGoal()) {
                putNeighbors(lastTwinNode, minTwinPQ);
            } else {
                break;
            }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return lastNode.isGoal();
    }

    // min number of moves to solve initial board
    public int moves() {
        if (isSolvable()) {
            return lastNode.getMoveCount();
        } else {
            return -1;
        }
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        if (!isSolvable()) {
            return null;
        }

        Stack<Board> boards = new Stack<Board>();
        SearchNode node = lastNode;
        while (node != null) {
            boards.push(node.getBoard());
            node = node.getPrevNode();
        }

        return boards;
    }

    // put all neighboring boards of the node to the priority queue
    private void putNeighbors(SearchNode node, MinPQ<SearchNode> queue) {
        assert (node != null && queue != null);

        Board currBoard = node.getBoard();
        Board prevBoard = null;
        SearchNode prevNode = node.getPrevNode();
        if (prevNode != null) {
            prevBoard = prevNode.getBoard();
        }

        Iterable<Board> neighbors = currBoard.neighbors();
        for (Board nextBoard : neighbors) {
            // don't enqueue a neighbor if its board is the same as the board of the previous search node
            if (!nextBoard.equals(prevBoard)) {
                queue.insert(new SearchNode(nextBoard, node));
            }
        }
    }

    // test client (see below)
    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }

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
