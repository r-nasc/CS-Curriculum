import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;

public class KdTree {

    private class Node {
        private Point2D p;
        private Node left, right;
        private int num;
        private RectHV rect;

        public Node(Point2D p, RectHV rect, int num) {
            this.num = num;
            this.p = p;
            this.rect = rect;
            this.left = null;
            this.right = null;
        }

    }

    private Node root;
    private int size;

    public KdTree() {
        root = null;
        size = 0;
    }

    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        if (contains(p))
            return;
        if (root == null) {
            root = new Node(p, new RectHV(0, 0, p.x(), 1), 1);
            size++;
            return;
        }

        Node curr = root;
        while (true) {
            if (curr.num % 2 != 0) {
                if (p.x() < curr.p.x()) {
                    if (curr.left == null) {
                        curr.left = new Node(
                                p,
                                new RectHV(curr.rect.xmin(), curr.rect.ymin(),
                                           curr.rect.xmax(), p.y()),
                                2
                        );
                        size++;
                        return;
                    }
                    curr = curr.left;
                }
                else {
                    if (curr.right == null) {
                        curr.right = new Node(
                                p,
                                new RectHV(curr.rect.xmin(), curr.rect.ymin(),
                                           curr.rect.xmax(), p.y()),
                                2
                        );
                        size++;
                        return;
                    }
                    curr = curr.right;
                }
            }
            else {
                if (p.y() < curr.p.y()) {
                    if (curr.left == null) {
                        curr.left = new Node(
                                p,
                                new RectHV(curr.rect.xmin(), curr.rect.ymin(), p.x(),
                                           curr.rect.ymax()),
                                1
                        );
                        size++;
                        return;
                    }
                    curr = curr.left;
                }
                else {
                    if (curr.right == null) {
                        curr.right = new Node(
                                p,
                                new RectHV(curr.rect.xmin(), curr.rect.ymin(), p.x(),
                                           curr.rect.ymax()),
                                1
                        );
                        size++;
                        return;
                    }
                    curr = curr.right;
                }
            }
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException();
        return searchRange(root, new ArrayList<>(), rect);
    }

    private ArrayList<Point2D> searchRange(Node startNode, ArrayList<Point2D> list, RectHV rect) {
        if (startNode != null) {
            if (rect.contains(startNode.p))
                list.add(startNode.p);
            if (startNode.right != null && startNode.right.rect.intersects(rect)) {
                list = searchRange(startNode.right, list, rect);
                list = searchRange(startNode.left, list, rect);
            }
            else {
                list = searchRange(startNode.left, list, rect);
                list = searchRange(startNode.right, list, rect);
            }
        }
        return list;
    }

    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();
        return isEmpty() ? null : searchNearest(root, p, root).p;
    }

    private Node searchNearest(Node startNode, Point2D p, Node nearest) {
        if (startNode != null) {
            if (startNode.p.distanceSquaredTo(p) < nearest.p.distanceSquaredTo(p))
                nearest = startNode;
            if (startNode.right != null && startNode.right.rect.contains(p)) {
                nearest = searchNearest(startNode.right, p, nearest);
                nearest = searchNearest(startNode.left, p, nearest);
            }
            else {
                nearest = searchNearest(startNode.left, p, nearest);
                nearest = searchNearest(startNode.right, p, nearest);
            }

        }
        return nearest;
    }


    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        Node curr = root;
        while (curr != null) {
            if (curr.p.equals(p))
                return true;
            if (curr.num % 2 != 0)
                curr = p.x() < curr.p.x() ? curr.left : curr.right;
            else
                curr = p.y() < curr.p.y() ? curr.left : curr.right;
        }
        return false;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "KdTree{" + "root=" + root + ", size=" + size + '}';
    }

    public void draw() {
    }

    public static void main(String[] args) {
        KdTree k = new KdTree();
        k.insert(new Point2D(0.7, 0.2));
        k.insert(new Point2D(0.5, 0.4));
        k.insert(new Point2D(0.3, 0.3));
        System.out.println(k.nearest(new Point2D(0.3, 0.4)));
        System.out.println(k.range(new RectHV(0, 0, 1, 1)));
        System.out.println(k);
    }
}
