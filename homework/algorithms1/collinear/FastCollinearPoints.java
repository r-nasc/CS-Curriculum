/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {
    private ArrayList<LineSegment> segments = new ArrayList<>();

    /* finds all line segments containing 4 or more points */
    public FastCollinearPoints(Point[] tmpPoints) {
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
    private void fillSegments(Point[] sortedPoints) {
        int len = sortedPoints.length;
        for (Point p : sortedPoints) {
            // Sort the points according to the slopes they make with p
            Point[] points = sortedPoints.clone();
            Arrays.sort(points, p.slopeOrder());

            // Check if any 3+ adjacent points have equal slopes
            for (int i1 = 1, iN = 2; iN < len; iN++) {
                // find last collinear point in current segment
                if (Double.compare(p.slopeTo(points[i1]),
                                   p.slopeTo(points[iN])) == 0)
                    continue;

                // if at least 3 elements and segment is unique
                if (iN - i1 >= 3 && p.compareTo(points[i1]) < 0) {
                    segments.add(new LineSegment(p, points[iN - 1]));
                }

                // next segment
                i1 = iN;
            }
        }
    }
}
