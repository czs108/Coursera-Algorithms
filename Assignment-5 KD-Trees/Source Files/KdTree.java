/* *****************************************************************************
 *  Name:               Chen Zhenshuo
 *  GitHub:             https://github.com/czs108
 *  Last modified:      12/2/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.Collection;

public class KdTree {

    private enum Direction {
        VERTICAL,
        HORIZONTAL
    }

    private static final class Node {

        public final Point2D point;

        public final RectHV rect;

        public final Direction direction;

        public Node leftChild, rightChild;

        public Node(Point2D point, RectHV rect, Direction direction) {
            this.point = point;
            this.rect = rect;
            this.direction = direction;
        }
    }

    private static final double PLANE_HEIGHT = 1;
    private static final double PLANE_WIDTH = 1;

    private Node root;

    private int size = 0;

    // construct an empty set of points
    public KdTree() { }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        if (!contains(p)) {
            root = insert(root, null, p);
            ++size;
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        return contains(root, p);
    }

    // draw all points to standard draw
    public void draw() {
        draw(root);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        if (isEmpty()) {
            return null;
        }

        ArrayList<Point2D> points = new ArrayList<Point2D>();
        range(root, rect, points);
        return points;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("[!] The argument can not be null");
        }

        Node nearestNode = nearest(root, null, p);
        return nearestNode != null ? nearestNode.point : null;
    }

    /*
     * if the node vertically divide the plane into two halfplanes,
     * points left of p are less than p, points right of p are bigger than p
     *
     * if the node horizontally divide the plane,
     * points below of p are less than p, points above of p are bigger than p
     */
    private static int compare(Node node, Point2D p) {
        assert (node != null && p != null);

        if (node.point.equals(p)) {
            return 0;
        }

        if (node.direction == Direction.VERTICAL) {
            int cmp = Double.compare(node.point.x(), p.x());
            if (cmp > 0) {
                return 1;
            } else if (cmp < 0) {
                return -1;
            } else {
                return Double.compare(node.point.y(), p.y());
            }
        } else {
            int cmp = Double.compare(node.point.y(), p.y());
            if (cmp > 0) {
                return 1;
            } else if (cmp < 0) {
                return -1;
            } else {
                return Double.compare(node.point.x(), p.x());
            }
        }
    }

    private static Node insert(Node node, Node parent, Point2D p) {
        assert (p != null);

        if (node == null) {
            // the tree is empty
            if (parent == null) {
                RectHV plane = new RectHV(0, 0, PLANE_WIDTH, PLANE_HEIGHT);
                return new Node(p, plane, Direction.VERTICAL);
            }

            RectHV plane = null;
            RectHV parentPlane = parent.rect;
            Direction direction;
            int cmp = compare(parent, p);
            if (parent.direction == Direction.VERTICAL) {
                direction = Direction.HORIZONTAL;
                // insert the new node to the left or right of the parent node
                if (cmp > 0) {
                    plane = new RectHV(parentPlane.xmin(), parentPlane.ymin(),
                                       parent.point.x(), parentPlane.ymax());
                } else if (cmp < 0) {
                    plane = new RectHV(parent.point.x(), parentPlane.ymin(),
                                       parentPlane.xmax(), parentPlane.ymax());
                }
            } else {
                direction = Direction.VERTICAL;
                // insert the new node below or above the parent node
                if (cmp > 0) {
                    plane = new RectHV(parentPlane.xmin(), parentPlane.ymin(),
                                       parentPlane.xmax(), parent.point.y());
                } else if (cmp < 0) {
                    plane = new RectHV(parentPlane.xmin(), parent.point.y(),
                                       parentPlane.xmax(), parentPlane.ymax());
                }
            }

            return new Node(p, plane, direction);

        } else {
            int cmp = compare(node, p);
            if (cmp > 0) {
                node.leftChild = insert(node.leftChild, node, p);
            } else if (cmp < 0) {
                node.rightChild = insert(node.rightChild, node, p);
            }

            return node;
        }
    }

    private static Node nearest(Node node, Node nearestNode, Point2D p) {
        assert (p != null);

        if (node == null) {
            return nearestNode;
        }

        double minDistance = Double.POSITIVE_INFINITY;
        if (nearestNode != null) {
            minDistance = p.distanceSquaredTo(nearestNode.point);
        }

        double distance = p.distanceSquaredTo(node.point);
        if (distance < minDistance) {
            nearestNode = node;
            minDistance = distance;
        }

        Node targetSubTree = null, oppositeSubTree = null;
        int cmp = compare(node, p);
        if (cmp > 0) {
            targetSubTree = node.leftChild;
            oppositeSubTree = node.rightChild;
        } else if (cmp < 0) {
            targetSubTree = node.rightChild;
            oppositeSubTree = node.leftChild;
        }

        // find the nearest point in the subtree
        nearestNode = nearest(targetSubTree, nearestNode, p);
        minDistance = p.distanceSquaredTo(nearestNode.point);
        if (oppositeSubTree != null) {
            // only when the vertical distance between the target point and the opposite subplane
            // is less than the distance between the target point and the nearest point,
            // the opposite subplane may have a point that is nearer to the target point
            double rectDistance = oppositeSubTree.rect.distanceSquaredTo(p);
            if (rectDistance < minDistance) {
                nearestNode = nearest(oppositeSubTree, nearestNode, p);
            }
        }

        return nearestNode;
    }

    private static void range(Node node, RectHV rect, Collection<Point2D> points) {
        assert (rect != null && points != null);

        if (node == null) {
            return;
        }

        if (rect.contains(node.point)) {
            points.add(node.point);
        }

        Node leftChild = node.leftChild;
        if (leftChild != null && leftChild.rect.intersects(rect)) {
            range(leftChild, rect, points);
        }

        Node rightChild = node.rightChild;
        if (rightChild != null && rightChild.rect.intersects(rect)) {
            range(rightChild, rect, points);
        }
    }

    private static boolean contains(Node node, Point2D p) {
        assert (p != null);

        while (node != null) {
            int cmp = compare(node, p);
            if (cmp > 0) {
                node = node.leftChild;
            } else if (cmp < 0) {
                node = node.rightChild;
            } else {
                return true;
            }
        }

        return false;
    }

    private static void draw(Node node) {
        if (node == null) {
            return;
        }

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.point.draw();

        // draw the splitting line segment
        StdDraw.setPenRadius();
        Point2D point = node.point;
        RectHV rect = node.rect;
        if (node.direction == Direction.VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(point.x(), rect.ymin(), point.x(), rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rect.xmin(), point.y(), rect.xmax(), point.y());
        }

        draw(node.leftChild);
        draw(node.rightChild);
    }
}
