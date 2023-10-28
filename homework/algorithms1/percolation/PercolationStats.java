/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;


public class PercolationStats {
    private static final double CONFIDENCE_95 = 1.96;
    private int size;
    private int trials;
    private double[] perStats;


    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0)
            throw new IllegalArgumentException("args must be greater than 0");

        this.size = n;
        this.trials = trials;
        this.perStats = new double[trials];

        for (int i = 0; i < trials; ++i) {
            Percolation perc = new Percolation(n);

            while (!perc.percolates()) {
                openRandomNode(perc);
            }
            perStats[i] = (double) perc.numberOfOpenSites() / (double) (n * n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(perStats);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(perStats);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        double sqrt = Math.sqrt(trials);
        return mean() - CONFIDENCE_95 * stddev() / sqrt;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        double sqrt = Math.sqrt(trials);
        return mean() + CONFIDENCE_95 * stddev() / sqrt;
    }

    // Open random nodes
    private void openRandomNode(Percolation perc) {
        int i, j;

        do {
            i = StdRandom.uniformInt(1, size + 1);
            j = StdRandom.uniformInt(1, size + 1);
        } while (perc.isOpen(i, j));

        perc.open(i, j);
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats test = new PercolationStats(n, t);
        System.out.println("mean:\t\t\t\t\t= " + test.mean());
        System.out.println("stddev:\t\t\t\t\t= " + test.stddev());

        double confLo = test.confidenceLo();
        double confHi = test.confidenceHi();
        System.out.printf("95%% confidence interval = [%f, %f]", confLo, confHi);
    }
}
