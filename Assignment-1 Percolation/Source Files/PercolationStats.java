/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/2/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    // generate random number sequence
    private static class RandomizedQueue {

        private static final int INVALID = -1;

        private int[] items;

        private int count;

        // create a sequence of 0 to n
        public RandomizedQueue(int n) {
            assert (n >= 1);

            items = new int[n];
            count = n;
            for (int i = 0; i != n; ++i) {
                items[i] = i;
            }
        }

        public boolean hasNext() {
            return count != 0;
        }

        public int next() {
            if (!hasNext()) {
                return INVALID;
            }

            int index = StdRandom.uniform(0, count);
            int item = items[index];
            moveLastItemTo(index);
            --count;
            return item;
        }

        // move the last item to the specific position
        private void moveLastItemTo(int index) {
            assert (0 <= index && index <= items.length);

            items[index] = items[count - 1];
        }
    }

    // threshold of each trial
    private final double[] thresholds;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0) {
            throw new IllegalArgumentException(
                    "[!] The size of grid must greater than 0");
        }

        if (trials <= 0) {
            throw new IllegalArgumentException(
                    "[!] The number of trials must greater than 0");
        }

        thresholds = new double[trials];
        for (int i = 0; i != trials; ++i) {
            RandomizedQueue indices = new RandomizedQueue(n * n);
            Percolation grid = new Percolation(n);
            while (!grid.percolates()) {
                assert (indices.hasNext());

                // open a site randomly
                int index = indices.next();
                int row = index / n + 1;
                int col = index % n + 1;
                grid.open(row, col);
            }

            // get the threshold
            thresholds[i] = Double.valueOf(grid.numberOfOpenSites()) / (n * n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(thresholds);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(thresholds);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        double mean = mean();
        double stddev = stddev();
        return mean - (1.96 * stddev / Math.sqrt(thresholds.length));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        double mean = mean();
        double stddev = stddev();
        return mean + (1.96 * stddev / Math.sqrt(thresholds.length));
    }

    // test client (see below)
    public static void main(String[] args) {
        if (args.length >= 2) {
            int n = Integer.parseInt(args[0]);
            int trials = Integer.parseInt(args[1]);
            PercolationStats test = new PercolationStats(n, trials);
            StdOut.printf("mean = %8f\n", test.mean());
            StdOut.printf("stddev = %8f\n", test.stddev());
            StdOut.printf("mean = [%8f, %8f]\n", test.confidenceLo(), test.confidenceHi());
        }
    }
}
