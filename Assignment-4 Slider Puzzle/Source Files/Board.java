/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/2/2019
 **************************************************************************** */

import java.util.ArrayList;

public final class Board {

    // store the row and column indices
    private static final class Position {

        public final int row;
        public final int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object y) {
            if (y == this) {
                return true;
            } else if (y == null) {
                return false;
            } else if (getClass() != y.getClass()) {
                return false;
            }

            Position that = (Position) y;
            if (row == that.row && col == that.col) {
                return true;
            } else {
                return false;
            }
        }
    }

    // representation of blank tile
    private static final int BLANK = 0;

    // game board
    private final int[][] tiles;

    // side length of board
    private final int SIDE_LENGTH;

    // position of blank tile
    private final Position blankPos;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        assert (2 <= tiles.length && tiles.length < 128);

        SIDE_LENGTH = tiles.length;
        Position blankPos = null;
        this.tiles = new int[SIDE_LENGTH][SIDE_LENGTH];
        for (int row = 0; row != SIDE_LENGTH; ++row) {
            if (tiles[row] == null) {
                throw new IllegalArgumentException("[!] The argument can not be null");
            }

            assert (tiles[row].length == SIDE_LENGTH);

            for (int col = 0; col != SIDE_LENGTH; ++col) {
                this.tiles[row][col] = tiles[row][col];
                if (tiles[row][col] == BLANK) {
                    assert (blankPos == null);
                    // set the position of blank tile
                    blankPos = new Position(row, col);
                }
            }
        }

        this.blankPos = blankPos;
    }

    // string representation of this board
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SIDE_LENGTH + "\n");

        for (int row = 0; row != SIDE_LENGTH; ++row) {
            for (int col = 0; col != SIDE_LENGTH; ++col) {
                sb.append(" " + tiles[row][col] + " ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    // board dimension n
    public int dimension() {
        return SIDE_LENGTH;
    }

    // number of tiles out of place
    public int hamming() {
        int distance = 0;
        for (int row = 0; row != SIDE_LENGTH; ++row) {
            for (int col = 0; col != SIDE_LENGTH; ++col) {
                // the tile is in the wrong position
                if (tiles[row][col] != BLANK && !holdGoalValue(row, col)) {
                    ++distance;
                }
            }
        }

        return distance;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int distance = 0;
        for (int row = 0; row != SIDE_LENGTH; ++row) {
            for (int col = 0; col != SIDE_LENGTH; ++col) {
                int value = tiles[row][col];
                // the tile is in the wrong position
                if (value != BLANK && !holdGoalValue(row, col)) {
                    Position goalPos = getGoalPosition(value);
                    distance += Math.abs(goalPos.row - row);
                    distance += Math.abs(goalPos.col - col);
                }
            }
        }

        return distance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    @Override
    public boolean equals(Object y) {
        if (y == this) {
            return true;
        } else if (y == null) {
            return false;
        } else if (getClass() != y.getClass()) {
            return false;
        }

        Board that = (Board) y;
        if (SIDE_LENGTH != that.SIDE_LENGTH) {
            return false;
        }

        for (int row = 0; row != SIDE_LENGTH; ++row) {
            for (int col = 0; col != SIDE_LENGTH; ++col) {
                if (tiles[row][col] != that.tiles[row][col]) {
                    return false;
                }
            }
        }

        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Position[] blankCandidates = new Position[] {
            new Position(blankPos.row - 1, blankPos.col),
            new Position(blankPos.row + 1, blankPos.col),
            new Position(blankPos.row, blankPos.col - 1),
            new Position(blankPos.row, blankPos.col + 1),
        };

        ArrayList<Board> neighbors = new ArrayList<Board>();
        for (Position candidate : blankCandidates) {
            if (isPositionValid(candidate.row, candidate.col)) {
                // change the position of blank tile
                neighbors.add(swapTiles(blankPos, candidate));
            }
        }

        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        for (int row = 0; row != SIDE_LENGTH; ++row) {
            for (int col = 0; col != SIDE_LENGTH - 1; ++col) {
                if (tiles[row][col] != BLANK && tiles[row][col + 1] != BLANK) {
                    // exchanging a pair of tiles
                    return swapTiles(new Position(row, col), new Position(row, col + 1));
                }
            }
        }

        assert (false);
        return null;
    }

    // check the range of board indices
    private boolean isPositionValid(int row, int col) {
        return (0 <= row && row < SIDE_LENGTH) && (0 <= col && col < SIDE_LENGTH);
    }

    // check the range of tile value
    private boolean isValueValid(int value) {
        return value == BLANK
                || (0 < value) && (value < SIDE_LENGTH * SIDE_LENGTH);
    }

    // check if the position holds the goal tile
    private boolean holdGoalValue(int row, int col) {
        int value = tiles[row][col];
        if (row == SIDE_LENGTH - 1 && col == SIDE_LENGTH - 1) {
            return value == BLANK;
        } else {
            int index = getLinearIndex(row, col);
            return value == index + 1;
        }
    }

    // transform the board indices into a linear index
    private int getLinearIndex(int row, int col) {
        assert (isPositionValid(row, col));

        return row * SIDE_LENGTH + col;
    }

    // get the goal position of the tile
    private Position getGoalPosition(int value) {
        assert (isValueValid(value));

        if (value != BLANK) {
            int row = (value - 1) / SIDE_LENGTH;
            int col = (value - 1) % SIDE_LENGTH;
            return new Position(row, col);
        } else {
            return new Position(SIDE_LENGTH - 1, SIDE_LENGTH - 1);
        }
    }

    // swap the two tiles
    private Board swapTiles(Position pos1, Position pos2) {
        assert (pos1 != null && pos2 != null);
        assert (isPositionValid(pos1.row, pos1.col) && isPositionValid(pos2.row, pos2.col));

        int[][] newTiles = cloneTiles();
        int temp = newTiles[pos1.row][pos1.col];
        newTiles[pos1.row][pos1.col] = newTiles[pos2.row][pos2.col];
        newTiles[pos2.row][pos2.col] = temp;
        return new Board(newTiles);
    }

    // clone game tiles
    private int[][] cloneTiles() {
        int[][] newTiles = new int[SIDE_LENGTH][SIDE_LENGTH];
        for (int row = 0; row != SIDE_LENGTH; ++row) {
            for (int col = 0; col != SIDE_LENGTH; ++col) {
                newTiles[row][col] = tiles[row][col];
            }
        }

        return newTiles;
    }
}
