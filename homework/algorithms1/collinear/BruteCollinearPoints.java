import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {
    private ArrayList<LineSegment> segments = new ArrayList<>();

    /* Finds all line segments containing 4 points */
    public BruteCollinearPoints(Point[] tmpPoints) {
        if (tmpPoints == null)
            throw new IllegalArgumentException("null argument");

        Point[] points = tmpPoints.clone();
        Arrays.sort(points);

        if (hasNullOrDuplicate(points))
            throw new IllegalArgumentException("null or duplicate points");

        fillSegments(points);
    }

    /* Returns number of segments */
    public int numberOfSegments() {
        return segments.size();
    }

    /* Returns array of segments */
    public LineSegment[] segments() {
        return segments.toArray(new LineSegment[0]);
    }

    /* Checks if sorted array of points has null or duplicate points */
    private boolean hasNullOrDuplicate(Point[] sortedPoints) {
        int len = sortedPoints.length;
        for (int i = 0; i < len - 1; i++) {
            if (sortedPoints[i] == null || sortedPoints[i + 1] == null)
                return true;
            if (sortedPoints[i].compareTo(sortedPoints[i + 1]) == 0)
                return true;
        }
        return len > 0 && sortedPoints[0] == null;
    }

    /* Fills segments based on sorted array of points */
    private void fillSegments(Point[] points) {
        int len = points.length;
        for (int i1 = 0; i1 < len - 3; i1++) {
            for (int i2 = i1 + 1; i2 < len - 2; i2++) {
                double slope12 = points[i1].slopeTo(points[i2]);
                for (int i3 = i2 + 1; i3 < len - 1; i3++) {
                    double slope13 = points[i1].slopeTo(points[i3]);
                    if (slope12 != slope13)
                        continue;
                    for (int i4 = i3 + 1; i4 < len; i4++) {
                        double slope14 = points[i1].slopeTo(points[i4]);
                        if (slope12 == slope14)
                            segments.add(
                                    new LineSegment(points[i1], points[i4])
                            );
                    }
                }
            }
        }
    }
}