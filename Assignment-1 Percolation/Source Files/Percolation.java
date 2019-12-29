/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/2/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

// Note: the row and column indices are integers between 1 and n
public class Percolation {

    // store the row and column indices
    private static final class Index {

        public final int row;
        public final int col;

        public Index(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    /*
     * we can add two extra virtual sites, connected to the top row and the bottom row respectively
     * when we want to check whether the system percolates
     * we just check whether the two virtual sites are conntected
     * otherwise, for each site on the top, we need to check each site on the bottom
     */

    /*
     * Backwash Problem
     *
     * if we add two extra virtual sites, the bottom virtual site would connect all open sites in the bottom row
     * when the system percolates, the two virtual sites are connected
     * at this time, all open sites in the bottom row would connect to the top virtual site
     * by the link of the bottom virtual site
     * so isFull() would fail, because some open sites in the bottom row are actually not connected to the top
     *
     * see docs/backwash.png
     *
     * we can use two WeightedQuickUnion grids, one of them has two virtual sites, used for percolationp check
     * the other only has a top virtual site, used for isFull() check
     */
    private WeightedQuickUnionUF topBottomGrid;
    private WeightedQuickUnionUF topGrid;

    // open status of each site
    private boolean[] openStatus;

    // side length of grid
    private final int SIDE_LENGTH;

    // in the array, the first element is virtual top site and the last is virtual bottom site
    // the id of virtual top site is 0
    private static final int TOP_VIRTUAL_ID = 0;
    // the id of virtual bottom site is (n + 1)
    private final int BOTTOM_VIRTUAL_ID;

    // create n-by-n grid
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException(
                    "[!] The size of grid must greater than 0");
        }

        SIDE_LENGTH = n;
        int gridSitesCount = n * n;
        BOTTOM_VIRTUAL_ID = gridSitesCount + 1;
        topBottomGrid = new WeightedQuickUnionUF(gridSitesCount + 2);
        topGrid = new WeightedQuickUnionUF(gridSitesCount + 1);

        // all sites are initially blocked except the virtual sites
        openStatus = new boolean[gridSitesCount + 2];
        openStatus[TOP_VIRTUAL_ID] = true;
        openStatus[BOTTOM_VIRTUAL_ID] = true;
        for (int i = 1; i <= gridSitesCount; ++i) {
            openStatus[i] = false;
        }
    }

    // open the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (isOpen(row, col)) {
            return;
        }

        int id = getLinearId(row, col);
        openStatus[id] = true;
        // connect the virtual top site to the site in the top row
        if (row == 1) {
            // prevent backwash
            topGrid.union(id, TOP_VIRTUAL_ID);
            topBottomGrid.union(id, TOP_VIRTUAL_ID);
        }

        // connect the virtual bottom site to the site in the bottom row
        if (row == SIDE_LENGTH) {
            topBottomGrid.union(id, BOTTOM_VIRTUAL_ID);
        }

        // connect the site to its open neighbors
        Index[] neighbors = getNeighbors(row, col);
        for (Index neighbor : neighbors) {
            if (neighbor != null && isOpen(neighbor.row, neighbor.col)) {
                int neighborId = getLinearId(neighbor.row, neighbor.col);
                topGrid.union(id, neighborId);
                topBottomGrid.union(id, neighborId);
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (!isIndexValid(row, col)) {
            throw new IllegalArgumentException(
                    "[!] The row and column indices are integers between 1 and " +
                    Integer.toString(SIDE_LENGTH));
        }

        int id = getLinearId(row, col);
        return openStatus[id];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!isIndexValid(row, col)) {
            throw new IllegalArgumentException(
                    "[!] The row and column indices are integers between 1 and " +
                    Integer.toString(SIDE_LENGTH));
        }

        // check if the site is open and connected to an open site in the top row
        if (isOpen(row, col)) {
            int id = getLinearId(row, col);
            return topGrid.connected(id, TOP_VIRTUAL_ID);
        } else {
            return false;
        }
    }

    // return the number of open sites
    public int numberOfOpenSites() {
        int count = 0;
        for (int i = 1; i <= SIDE_LENGTH * SIDE_LENGTH; ++i) {
            if (openStatus[i]) {
                ++count;
            }
        }

        return count;
    }

    // does the system percolate?
    public boolean percolates() {
        // check if the two virtual sites are connected
        return topBottomGrid.connected(TOP_VIRTUAL_ID, BOTTOM_VIRTUAL_ID);
    }

    // get the neighbors of the site
    private Index[] getNeighbors(int row, int col) {
        assert (isIndexValid(row, col));

        // check the left, right, up and down sites
        Index[] neighbors = new Index[4];
        if (col >= 2) {
            neighbors[0] = new Index(row, col - 1);
        }

        if (col <= SIDE_LENGTH - 1) {
            neighbors[1] = new Index(row, col + 1);
        }

        if (row >= 2) {
            neighbors[2] = new Index(row - 1, col);
        }

        if (row <= SIDE_LENGTH - 1) {
            neighbors[3] = new Index(row + 1, col);
        }

        return neighbors;
    }

    // check the range of grid indices
    private boolean isIndexValid(int row, int col) {
        return (1 <= row && row <= SIDE_LENGTH) && (1 <= col && col <= SIDE_LENGTH);
    }

    // transform the grid indices into a linear id
    private int getLinearId(int row, int col) {
        assert (isIndexValid(row, col));

        return (row - 1) * SIDE_LENGTH + col;
    }
}
