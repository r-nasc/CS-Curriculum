import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    private static final int BORDER_ENERGY = 1000;
    private int w;
    private int h;
    private final Picture pic;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("Invalid Picture");
        this.w = picture.width();
        this.h = picture.height();
        this.pic = new Picture(picture);
    }

    private int gradientSquare(int x1, int y1, int x2, int y2) {
        Color col1 = pic.get(x1, y1);
        Color col2 = pic.get(x2, y2);
        int red = col1.getRed() - col2.getRed();
        int green = col1.getGreen() - col2.getGreen();
        int blue = col1.getBlue() - col2.getBlue();
        return red * red + green * green + blue * blue;
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(w, h);
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y)
                picture.set(x, y, pic.get(x, y));
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return w;
    }

    // height of current picture
    public int height() {
        return h;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= w || y < 0 || y >= h)
            throw new IllegalArgumentException("Invalid coords");

        if (x == 0 || x == w - 1 || y == 0 || y == h - 1)
            return BORDER_ENERGY;

        int dx = gradientSquare(x + 1, y, x - 1, y);
        int dy = gradientSquare(x, y + 1, x, y - 1);
        return Math.sqrt(dx + dy);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] sum = new double[2][h];
        int[][] parent = new int[w][h];
        for (int y = 0; y < h; ++y) {
            sum[0][y] = BORDER_ENERGY;
            parent[0][y] = y;
        }

        for (int x = 1; x < w; ++x) {
            int tmpX = (x - 1) % 2;
            for (int y = 0; y < h; ++y) {
                double temp = sum[tmpX][y];
                parent[x][y] = y;
                if (y > 0 && sum[tmpX][y - 1] < temp) {
                    temp = sum[tmpX][y - 1];
                    parent[x][y] = y - 1;
                }

                if (y < h - 1 && sum[tmpX][y + 1] < temp) {
                    temp = sum[tmpX][y + 1];
                    parent[x][y] = y + 1;
                }
                sum[x % 2][y] = energy(x, y) + temp;
            }
        }

        int index = 0;
        for (int y = 1; y < h; ++y) {
            if (sum[(w - 1) % 2][y] < sum[(w - 1) % 2][index])
                index = y;
        }

        int[] seam = new int[w];
        seam[w - 1] = index;
        for (int x = w - 2; x >= 0; --x) {
            seam[x] = parent[x + 1][index];
            index = parent[x + 1][index];
        }
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] sum = new double[w][2];
        int[][] parent = new int[w][h];
        for (int x = 0; x < w; ++x) {
            sum[x][0] = BORDER_ENERGY;
            parent[x][0] = x;
        }

        for (int y = 1; y < h; ++y) {
            int tmpY = (y - 1) % 2;
            for (int x = 0; x < w; ++x) {
                double temp = sum[x][tmpY];
                parent[x][y] = x;
                if (x > 0 && sum[x - 1][tmpY] < temp) {
                    temp = sum[x - 1][tmpY];
                    parent[x][y] = x - 1;
                }

                if (x < w - 1 && sum[x + 1][tmpY] < temp) {
                    temp = sum[x + 1][tmpY];
                    parent[x][y] = x + 1;
                }
                sum[x][y % 2] = energy(x, y) + temp;
            }
        }

        int index = 0;
        for (int x = 1; x < w; ++x) {
            if (sum[x][(h - 1) % 2] < sum[index][(h - 1) % 2])
                index = x;
        }

        int[] seam = new int[h];
        seam[h - 1] = index;
        for (int y = h - 2; y >= 0; --y) {
            seam[y] = parent[index][y + 1];
            index = parent[index][y + 1];
        }
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != w)
            throw new IllegalArgumentException("Invalid Seam");

        if (h <= 1)
            throw new IllegalArgumentException("Height is less than or equal to 1");

        for (int x = 0; x < w; ++x) {
            if (seam[x] < 0 || seam[x] >= h || (x > 0 && Math.abs(seam[x] - seam[x - 1]) > 1))
                throw new IllegalArgumentException("Invalid Seam");

            for (int y = seam[x]; y < h - 1; ++y)
                pic.set(x, y, pic.get(x, y + 1));
        }
        --h;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != h)
            throw new IllegalArgumentException("Invalid Seam");

        if (w <= 1)
            throw new IllegalArgumentException("Width is less than or equal to 1");

        for (int y = 0; y < h; ++y) {
            if (seam[y] < 0 || seam[y] >= w || (y > 0 && Math.abs(seam[y] - seam[y - 1]) > 1))
                throw new IllegalArgumentException("Invalid Seam");

            for (int x = seam[y]; x < w - 1; ++x)
                pic.set(x, y, pic.get(x + 1, y));
        }
        --w;
    }
}