import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class KDTree {

    public class Node {
        long id;
        int axis;
        double x;
        double y;
        Node left;
        Node right;

        public Node(double xx, double yy, int ax, long nodeid) {
            this.id = nodeid;
            this.axis = ax;
            this.x = xx;
            this.y = yy;
            this.left = null;
            this.right = null;
        }

    }

    Node root;

    public KDTree(Collection<GraphDB.Vertex> points) {
        root = makeKDTree(points, 0);
    }
    public Node makeKDTree(Collection<GraphDB.Vertex> points, int depth) {
        if (points.isEmpty()) {
            return null;
        }
        int axis = depth % 2;
        Node node;

        List<GraphDB.Vertex> sorted1;
        if (axis == 0) {
            sorted1 = points.stream().sorted((o1, o2) -> Double.compare(o1.x, o2.x))
                    .collect(Collectors.toList());
        } else {
            sorted1 = points.stream().sorted((o1, o2) -> Double.compare(o1.y, o2.y))
                     .collect(Collectors.toList());
        }

        Collection<GraphDB.Vertex> leftpoints = new ArrayList<>();
        Collection<GraphDB.Vertex> rightpoints = new ArrayList<>();

        GraphDB.Vertex median = null;

        for (int i = 0; i < sorted1.size(); i++) {
            if (i < sorted1.size() / 2) {
                leftpoints.add(sorted1.get(i));
            } else if (i > sorted1.size() / 2) {
                rightpoints.add(sorted1.get(i));
            } else {
                median = sorted1.get(i);
            }
        }
        node = new Node(median.x, median.y, axis, median.nodeID);
        node.left = makeKDTree(leftpoints, depth + 1);
        node.right = makeKDTree(rightpoints, depth + 1);

        return node;
    }

    public Node nearestNeighbour(double destx, double desty, int depth, Node node, Node bestSeen) {
        if (node == null) {
            return bestSeen;
        }

        Node bestnew = bestSeen;

        if (edSquare(destx, desty, node.x, node.y) < edSquare(destx, desty, bestnew.x, bestnew.y)) {
            bestnew = node;
        }

        int axis = depth % 2;
        boolean isXaxis;
        if (axis == 0) {
            isXaxis = true;
        } else {
            isXaxis = false;
        }

        if (isXaxis) {
            if (destx < node.x) {
                bestnew = nearestNeighbour(destx, desty, depth + 1, node.left, bestnew);
                if (Math.pow(destx - node.x, 2) < edSquare(destx, desty, bestnew.x, bestnew.y)) {
                    bestnew = nearestNeighbour(destx, desty, depth + 1, node.right, bestnew);
                }
            } else {
                bestnew = nearestNeighbour(destx, desty, depth + 1, node.right, bestnew);
                if (Math.pow(destx - node.x, 2) < edSquare(destx, desty, bestnew.x, bestnew.y)) {
                    bestnew = nearestNeighbour(destx, desty, depth + 1, node.left, bestnew);
                }


            }
        } else {
            if (desty < node.y) {
                bestnew = nearestNeighbour(destx, desty, depth + 1, node.left, bestnew);
                if (Math.pow(desty - node.y, 2) < edSquare(destx, desty, bestnew.x, bestnew.y)) {
                    bestnew = nearestNeighbour(destx, desty, depth + 1, node.right, bestnew);
                }
            } else {
                bestnew = nearestNeighbour(destx, desty, depth + 1, node.right, bestnew);
                if (Math.pow(desty - node.y, 2) < edSquare(destx, desty, bestnew.x, bestnew.y)) {
                    bestnew = nearestNeighbour(destx, desty, depth + 1, node.left, bestnew);
                }
            }
        }

        return bestnew;
    }


    private static double edSquare(double x1, double y1, double x2, double y2) {
        return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
    }




}
