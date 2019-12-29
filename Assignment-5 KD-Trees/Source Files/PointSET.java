/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/2/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.TreeSet;

public class PointSET {

    private TreeSet<Point2D> points = new TreeSet<Point2D>();

    // construct an empty set of points
    public PointSET() { }

    // is the set empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : points) {
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        if (isEmpty()) {
            return null;
        }

        ArrayList<Point2D> insidePoints = new ArrayList<Point2D>();
        for (Point2D point : points) {
            if (rect.contains(point))
                insidePoints.add(point);
        }

        return insidePoints;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        Point2D nearestPoint = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Point2D point : points) {
            double distance = point.distanceSquaredTo(p);
            if (distance < minDistance) {
                nearestPoint = point;
                minDistance = distance;
            }
        }

        return nearestPoint;
    }
}
