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

public class BruteCollinearPoints {

    // store line segments
    private final ArrayList<LineSegment> lineSegmentList;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        Point[] sortedPoints = checkAndSortPoints(points);

        lineSegmentList = new ArrayList<LineSegment>();
        int count = sortedPoints.length;
        for (int dot1 = 0; dot1 <= count - 4; ++dot1) {
            for (int dot2 = dot1 + 1; dot2 <= count - 3; ++dot2) {
                // get the slope between point-1 and point-2
                double slope12 = sortedPoints[dot2].slopeTo(sortedPoints[dot1]);

                for (int dot3 = dot2 + 1; dot3 <= count - 2; ++dot3) {
                    // get the slope between point-1 and point-3
                    double slope13 = sortedPoints[dot3].slopeTo(sortedPoints[dot1]);
                    if (slope12 != slope13) {
                        continue;
                    }

                    for (int dot4 = dot3 + 1; dot4 <= count - 1; ++dot4) {
                        // get the slope between point-1 and point-4
                        double slope14 = sortedPoints[dot4].slopeTo(sortedPoints[dot1]);
                        if (slope13 != slope14) {
                            continue;
                        }

                        // add a new line segment
                        lineSegmentList.add(new LineSegment(sortedPoints[dot1], sortedPoints[dot4]));
                    }
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
