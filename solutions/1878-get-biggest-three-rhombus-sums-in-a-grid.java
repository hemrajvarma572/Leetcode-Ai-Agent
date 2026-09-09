import java.util.*;

class Solution {
    public int[] getBiggestThree(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        TreeSet<Integer> topThree = new TreeSet<>(Collections.reverseOrder());

        // Iterate over every cell as the top corner of a rhombus
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                // Size 0 rhombus (just the cell itself)
                addSum(topThree, grid[r][c]);

                // Size k rhombus: distance from top corner to left/right corner
                for (int k = 1; ; k++) {
                    // Check if the rhombus can fit
                    if (r + 2 * k >= m || c - k < 0 || c + k >= n) break;

                    int sum = 0;
                    // Top-left side
                    for (int i = 0; i < k; i++) sum += grid[r + i][c - i];
                    // Top-right side
                    for (int i = 0; i < k; i++) sum += grid[r + i][c + i];
                    // Bottom-left side
                    for (int i = 0; i < k; i++) sum += grid[r + 2 * k - i][c - k + i];
                    // Bottom-right side
                    for (int i = 0; i < k; i++) sum += grid[r + 2 * k - i][c + k - i];
                    
                    addSum(topThree, sum);
                }
            }
        }

        int[] result = new int[Math.min(topThree.size(), 3)];
        int idx = 0;
        for (int val : topThree) {
            result[idx++] = val;
            if (idx == 3) break;
        }
        return result;
    }

    private void addSum(TreeSet<Integer> set, int sum) {
        set.add(sum);
        if (set.size() > 3) {
            set.pollLast();
        }
    }
}