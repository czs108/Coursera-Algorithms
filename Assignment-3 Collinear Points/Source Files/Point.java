/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *  
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class Point implements Comparable<Point> {

    private enum RelativePosition {
        OVERLAPPING,
        VERTICAL,
        HORIZONTAL,
        OTHER
    }

    private class SlopeComparator implements Comparator<Point> {

        @Override
        public int compare(Point o1, Point o2) {
            double slope1 = slopeTo(o1);
            double slope2 = slopeTo(o2);
            return Double.compare(slope1, slope2);
        }
    }

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        assert (0 <= x && x <= 32767);
        assert (0 <= y && y <= 32767);

        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        switch (getRelativePosition(that)) {
            case OVERLAPPING: {
                // the two points are equal
                return Double.NEGATIVE_INFINITY;
            }
            case HORIZONTAL: {
                // the line segment is horizontal
                return 0;
            }
            case VERTICAL: {
                // the line segment is vertical
                return Double.POSITIVE_INFINITY;
            }
            default: {
                return Double.valueOf(that.y - y) / (that.x - x);
            }
        }
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    @Override
    public int compareTo(Point that) {
        if (y < that.y) {
            return -1;
        } else if (y > that.y) {
            return 1;
        } else {
            return Integer.compare(x, that.x);
        }
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        return new SlopeComparator();
    }

    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    // get the relative position between two points
    private RelativePosition getRelativePosition(Point that) {
        assert (that != null);

        if (this.compareTo(that) == 0) {
            return RelativePosition.OVERLAPPING;
        } else if (x == that.x) {
            return RelativePosition.VERTICAL;
        } else if (y == that.y) {
            return RelativePosition.HORIZONTAL;
        } else {
            return RelativePosition.OTHER;
        }
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        StdOut.println("[*] p1(0, 0)\n[*] p2(1, 1)\n[*] p3(2, 2)\n[*] p4(2, 1)\n[*] p5(4, 1)");
        Point p1 = new Point(0, 0);
        Point p2 = new Point(1, 1);
        Point p3 = new Point(2, 2);
        Point p4 = new Point(2, 1);
        Point p5 = new Point(4, 1);

        StdOut.println("[*] p1.compareTo(p1) is " + p1.compareTo(p2));
        StdOut.println("[*] p2.compareTo(p1) is " + p2.compareTo(p1));
        StdOut.println("[*] p1.compareTo(p1) is " + p1.compareTo(p1));

        StdOut.println("[*] p1.slopeTo(p2) is " + p1.slopeTo(p2));
        StdOut.println("[*] p1.slopeTo(p4) is " + p1.slopeTo(p4));
        StdOut.println("[*] p1.slopeTo(p1) is " + p1.slopeTo(p1));
        StdOut.println("[*] p3.slopeTo(p4) is " + p3.slopeTo(p4));
        StdOut.println("[*] p2.slopeTo(p5) is " + p2.slopeTo(p5));
    }
}
