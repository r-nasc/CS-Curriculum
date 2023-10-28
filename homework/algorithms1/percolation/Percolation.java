/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int openCount;
    private int size;
    private WeightedQuickUnionUF map;
    private boolean[][] openCache;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("n must be > 0");

        size = n;
        openCount = 0;
        openCache = new boolean[n][n];

        // Create single virtual element above first row
        // and single virtual element beneath last row
        int setSize = n * n + 2;
        map = new WeightedQuickUnionUF(setSize);

        // Connect first "virtual" element to first row
        // and last "virtual" element to last row
        for (int col = 1; col <= n; col++) {
            map.union(0, getIdx(1, col));
            map.union(setSize - 1, getIdx(n, col));
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        checkBounds(row, col);

        if (isOpen(row, col))
            return;

        openCount++;
        openCache[row - 1][col - 1] = true;

        int mainIdx = getIdx(row, col);
        if (col > 1 && isOpen(row, col - 1))
            map.union(mainIdx, getIdx(row, col - 1));
        if (col < size && isOpen(row, col + 1))
            map.union(mainIdx, getIdx(row, col + 1));
        if (row > 1 && isOpen(row - 1, col))
            map.union(mainIdx, getIdx(row - 1, col));
        if (row < size && isOpen(row + 1, col))
            map.union(mainIdx, getIdx(row + 1, col));
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        checkBounds(row, col);
        return openCache[row - 1][col - 1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        checkBounds(row, col);
        int idx = getIdx(row, col);
        return isOpen(row, col) && map.find(idx) == map.find(0);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openCount;
    }

    // does the system percolate?
    public boolean percolates() {
        int lastElement = size * size + 1;
        return map.find(lastElement) == map.find(0);
    }

    private void checkBounds(int row, int col) {
        if (row < 1 || row > size || col < 1 || col > size)
            throw new IllegalArgumentException("out of bounds");
    }

    private int getIdx(int row, int col) {
        return (row - 1) * size + col;
    }
}