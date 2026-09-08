import java.util.*;

class Solution {
    public long maxScore(int[] points, int m) {
        long low = 0;
        long high = 2_000_000_000_000_000L; // Sufficiently large upper bound
        long ans = 0;

        while (low <= high) {
            long mid = low + (high - low) / 2;
            if (check(mid, points, m)) {
                ans = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return ans;
    }

    private boolean check(long target, int[] points, int m) {
        int n = points.length;
        long[] needed = new long[n];
        for (int i = 0; i < n; i++) {
            needed[i] = (target + points[i] - 1) / points[i];
        }

        // We need to cover needed[i] visits to index i.
        // Let x[i] be the number of times we stop at index i.
        // A visit to i helps index i, i-1, and i+1.
        // Specifically, stopping at i contributes 1 to score[i], 1 to score[i-1], 1 to score[i+1].
        // This is a variation of the interval covering problem.
        // Greedy approach: to satisfy needed[i], we prioritize moves that affect i and subsequent indices.
        
        long moves = 0;
        long last_i = 0; // Number of times we visited i
        long last_last_i = 0; // Number of times we visited i-1
        
        for (int i = 0; i < n; i++) {
            // Score at i is last_last_i + last_i + current_i
            long current_i = Math.max(0, needed[i] - last_last_i - last_i);
            
            // Each stop at i counts as a move.
            // Moving between i and i+1 takes 1 step. 
            // The total moves = (position of last visit) + 2 * (number of moves back)
            // A more direct greedy:
            if (current_i > 0) {
                moves += current_i;
                if (i > 0) moves += current_i; // To go back and forth
            }
            
            last_last_i = last_i;
            last_i = current_i;
        }

        // The logic above is a simplification; for exact pathing:
        // Let x[i] be number of times we end turn at index i.
        // Total moves = max(i such that x[i]>0) + 2 * sum(moves where we move left)
        int lastPos = -1;
        for (int i = n - 1; i >= 0; i--) {
            if (needed[i] > 0) {
                lastPos = i;
                break;
            }
        }
        
        if (lastPos == -1) return true;
        
        long totalMoves = lastPos + 1;
        long[] x = new long[n];
        long current = 0;
        for (int i = 0; i <= lastPos; i++) {
            long neededNow = Math.max(0, needed[i] - current);
            x[i] = neededNow;
            totalMoves += 2 * neededNow;
            current = neededNow + (i > 0 ? x[i-1] : 0);
        }
        
        // Subtract extra moves for the last position
        totalMoves -= x[lastPos];
        
        return totalMoves <= m;
    }
}