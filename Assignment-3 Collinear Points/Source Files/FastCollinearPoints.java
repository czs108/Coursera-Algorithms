/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/2/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {

    private static final int COLLINEAR_LIMIT = 4;

    // store line segments
    private final ArrayList<LineSegment> lineSegmentList;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        Point[] sortedPoints = checkAndSortPoints(points);

        lineSegmentList = new ArrayList<LineSegment>();
        if (sortedPoints.length < 2) {
            return;
        }

        for (int i = 0; i != sortedPoints.length; ++i) {
            Point originPoint = sortedPoints[i];

            // copy other points
            Point[] otherPoints = new Point[sortedPoints.length - 1];
            for (int j = 0; j != sortedPoints.length; ++j) {
                if (j < i) {
                    otherPoints[j] = sortedPoints[j];
                } else if (j > i) {
                    otherPoints[j - 1] = sortedPoints[j];
                }
            }

            // sort stablely the points according to the slopes they make with origin
            Arrays.sort(otherPoints, originPoint.slopeOrder());

            // the origin point and another point are initially collinear
            int collinearCount = 2;
            for (int j = 1; j != otherPoints.length; ++j) {
                double slope1 = otherPoints[j - 1].slopeTo(originPoint);
                double slope2 = otherPoints[j].slopeTo(originPoint);

                if (slope1 == slope2) {
                    // find a new collinear point
                    ++collinearCount;

                    // get the last point
                    if (j == otherPoints.length - 1 && collinearCount >= COLLINEAR_LIMIT) {
                        // the last point is collinear with previous points
                        Point secondPoint = otherPoints[j - collinearCount + 2];
                        Point lastPoint = otherPoints[j];
                        addLineSegment(originPoint, secondPoint, lastPoint);
                    }
                } else {
                    if (collinearCount >= COLLINEAR_LIMIT) {
                        // get a line segment containing at least 4 points
                        Point secondPoint = otherPoints[j - collinearCount + 1];
                        Point lastPoint = otherPoints[j - 1];
                        addLineSegment(originPoint, secondPoint, lastPoint);
                    }

                    collinearCount = 2;
                }
            }
        }
    }

    // get the number of line segments
    public int numberOfSegments() {
        return lineSegmentList.size();
    }

    // get the line segments
    public LineSegment[] segments() {
        LineSegment[] segments = new LineSegment[lineSegmentList.size()];
        for (int i = 0; i != segments.length; ++i) {
            segments[i] = lineSegmentList.get(i);
        }

        return segments;
    }

    // check if the origin point is the smallest in a sorted set of collinear points
    // if it is true, add a new line segment
    private void addLineSegment(Point origin, Point second, Point last) {
        assert (origin != null && second != null && last != null);

        // repeated line segments will not be added to the list
        if (origin.compareTo(second) < 0) {
            lineSegmentList.add(new LineSegment(origin, last));
        }
    }

    // check and sort the input points
    private static Point[] checkAndSortPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        for (Point point : points) {
            if (point == null) {
                throw new IllegalArgumentException("[!] The argument can not be null");
            }
        }

        Point[] sortedPoints = points.clone();
        Arrays.sort(sortedPoints);
        for (int i = 1; i != sortedPoints.length; ++i) {
            if (sortedPoints[i].compareTo(sortedPoints[i - 1]) == 0) {
                throw new IllegalArgumentException("[!] The same points are not allowed");
            }
        }

        return sortedPoints;
    }

    // sample client
    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        }

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
