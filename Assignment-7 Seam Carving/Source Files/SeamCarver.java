/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/30/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {

    private static final class ColorHelper {

        public static int getRed(int color) {
            return (color >> 16) & 0xFF;
        }

        public static int getGreen(int color) {
            return (color >> 8) & 0xFF;
        }

        public static int getBlue(int color) {
            return color & 0xFF;
        }
    }

    // store the row and column indices
    private static final class Index {

        public final int x;
        public final int y;

        public Index(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private final class TraceNode implements Comparable<TraceNode> {

        // only stores the x-coordinate of the previous pixel
        public int pixelTo;

        public double distance;

        public TraceNode() {
            pixelTo = INVALID;
            distance = Double.POSITIVE_INFINITY;
        }

        @Override
        public int compareTo(TraceNode that) {
            if (that == null) {
                throw new IllegalArgumentException("[!] The argument can not be null");
            }

            return Double.compare(distance, that.distance);
        }
    }

    private static final double ENERGY_AT_BORDER = 1000;

    private static final int INVALID = -1;

    private int[][] colors;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        // to improve performance, only store the color of each pixel
        int width = picture.width();
        int height = picture.height();
        colors = new int[width][height];
        for (int i = 0; i != width; ++i) {
            for (int j = 0; j != height; ++j) {
                colors[i][j] = picture.getRGB(i, j);
            }
        }
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(width(), height());
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                picture.set(i, j, new Color(colors[i][j]));
            }
        }

        return picture;
    }

    // width of current picture
    public int width() {
        return colors.length;
    }

    // height of current picture
    public int height() {
        return colors[0].length;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (!isPixelValid(x, y)) {
            throw new IllegalArgumentException(String.format(
                    "[!] The indices x and y are integers between 0 and %d − 1 "
                            + "and between 0 and %d − 1 respectively",
                    width(), height()));
        }

        if (isPixelAtBorder(x, y)) {
            return ENERGY_AT_BORDER;
        }

        int rightHorizontalColor = colors[x + 1][y];
        int leftHorizontalColor = colors[x - 1][y];
        double squareDeltaX = Math.pow(ColorHelper.getRed(rightHorizontalColor) - ColorHelper.getRed(leftHorizontalColor), 2)
                + Math.pow(ColorHelper.getGreen(rightHorizontalColor) - ColorHelper.getGreen(leftHorizontalColor), 2)
                + Math.pow(ColorHelper.getBlue(rightHorizontalColor) - ColorHelper.getBlue(leftHorizontalColor), 2);

        int aboveVerticalColor = colors[x][y + 1];
        int belowVerticalColor = colors[x][y - 1];
        double squareDeltaY = Math.pow(ColorHelper.getRed(aboveVerticalColor) - ColorHelper.getRed(belowVerticalColor), 2)
                + Math.pow(ColorHelper.getGreen(aboveVerticalColor) - ColorHelper.getGreen(belowVerticalColor), 2)
                + Math.pow(ColorHelper.getBlue(aboveVerticalColor) - ColorHelper.getBlue(belowVerticalColor), 2);

        return Math.sqrt(squareDeltaX + squareDeltaY);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transposePixels();
        int[] seam = findVerticalSeam();
        transposePixels();
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        /*
         * the picture is an edge-weighted DAG
         * the downward edge is from pixel (x, y) to pixels (x − 1, y + 1), (x, y + 1) and (x + 1, y + 1)
         * but there is no need to do a depth-first search to get the topological order
         * the pixels in x row have higher order priority than the pixels in x - 1 row
         * the pixels in the same row have the same order priority
         * we can just relax all pixels row by row
         */
        TraceNode[][] traceNodes = new TraceNode[width()][height()];
        for (int i = 0; i != width(); ++i) {
            for (int j = 0; j != height(); ++j) {
                traceNodes[i][j] = new TraceNode();
                traceNodes[i][j].distance = (j != 0 ? Double.POSITIVE_INFINITY : energy(i, j));
            }
        }

        // relax edges from the top to the bottom (topological order)
        double[][] energy = calcPictureEnergy();
        for (int j = 0; j < height() - 1; j++) {
            for (int i = 0; i < width(); i++) {
                relaxVerticalNeighbors(i, j, traceNodes, energy);
            }
        }

        return findVerticalSeam(traceNodes);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (height() <= 1) {
            throw new IllegalArgumentException("[!] The picture is too narrow");
        }

        checkHorizontalSeam(seam);

        int newHeight = height() - 1;
        int[][] updatedColors = new int[width()][newHeight];
        for (int i = 0; i != seam.length; ++i) {
            System.arraycopy(colors[i], 0, updatedColors[i], 0, seam[i]);
            System.arraycopy(colors[i], seam[i] + 1, updatedColors[i], seam[i], newHeight - seam[i]);
        }

        colors = updatedColors;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (width() <= 1) {
            throw new IllegalArgumentException("[!] The picture is too narrow");
        }

        transposePixels();
        removeHorizontalSeam(seam);
        transposePixels();
    }

    private void transposePixels() {
        assert (colors != null);

        int[][] updatedColors = new int[height()][width()];
        for (int i = 0; i != height(); ++i) {
            for (int j = 0; j != width(); ++j) {
                updatedColors[i][j] = colors[j][i];
            }
        }

        colors = updatedColors;
    }

    // only can be called when finding vertical seam
    private void relaxVerticalNeighbors(int x, int y, TraceNode[][] traceNodes, double[][] energy) {
        assert (isPixelValid(x, y));
        assert (traceNodes != null && traceNodes.length == width() && traceNodes[0].length == height());
        assert (energy != null && energy.length == width() && energy[0].length == height());

        Index[] neighbors = getVerticalNeighbors(x, y);
        for (Index next : neighbors) {
            if (next != null) {
                double newDistance = traceNodes[x][y].distance + energy[next.x][next.y];
                // update the distance
                if (newDistance < traceNodes[next.x][next.y].distance) {
                    traceNodes[next.x][next.y].distance = newDistance;
                    traceNodes[next.x][next.y].pixelTo = x;
                }
            }
        }
    }

    private Index[] getVerticalNeighbors(int x, int y) {
        assert (isPixelValid(x, y));

        Index[] neighbors = new Index[3];
        if (isPixelValid(x - 1, y + 1)) {
            neighbors[0] = new Index(x - 1, y + 1);
        }

        if (isPixelValid(x, y + 1)) {
            neighbors[1] = new Index(x, y + 1);
        }

        if (isPixelValid(x + 1, y + 1)) {
            neighbors[2] = new Index(x + 1, y + 1);
        }

        return neighbors;
    }

    private double[][] calcPictureEnergy() {
        assert (colors != null);

        double[][] energy = new double[width()][height()];
        for (int i = 0; i != width(); ++i) {
            for (int j = 0; j != height(); ++j) {
                energy[i][j] = energy(i, j);
            }
        }

        return energy;
    }

    private int[] findVerticalSeam(TraceNode[][] traceNodes) {
        assert (width() > 1 && height() > 1);
        assert (traceNodes != null);
        assert (traceNodes.length == width() && traceNodes[0].length == height());

        // find the minimum distance in the last row
        TraceNode pathEndNode = traceNodes[0][height() - 1];
        int pathEndX = 0;
        for (int i = 1; i < width(); i++) {
            if (traceNodes[i][height() - 1].compareTo(pathEndNode) < 0) {
                pathEndNode = traceNodes[i][height() - 1];
                pathEndX = i;
            }
        }

        // get the path from the top to the bottom
        int[] seam = new int[height()];
        seam[height() - 1] = pathEndX;

        TraceNode node = pathEndNode;
        for (int i = 1; i != height(); ++i) {
            int y = height() - i - 1;
            seam[y] = node.pixelTo;
            node = traceNodes[node.pixelTo][y];
        }

        return seam;
    }

    private void checkHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        if (seam.length != width()) {
            throw new IllegalArgumentException("[!] The length of seam is wrong");
        }

        for (int i = 0; i != width(); ++i) {
            if (!isPixelValid(0, seam[i])) {
                throw new IllegalArgumentException("[!] The index of seam is invalid");
            }
        }

        for (int i = 1; i != width(); ++i) {
            if (Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException("[!] The index of seam is invalid");
            }
        }
    }

    private boolean isPixelValid(int x, int y) {
        return (0 <= x && x < width()) && (0 <= y && y < height());
    }

    private boolean isPixelAtBorder(int x, int y) {
        assert (isPixelValid(x, y));

        boolean isXAtBorder = (x == 0) || (x == (width() - 1));
        boolean isYAtBorder = (y == 0) || (y == (height() - 1));
        return isXAtBorder || isYAtBorder;
    }
}
