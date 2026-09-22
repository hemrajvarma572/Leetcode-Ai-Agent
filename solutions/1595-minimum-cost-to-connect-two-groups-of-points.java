import java.util.*;

class Solution {
    public int connectTwoGroups(List<List<Integer>> cost) {
        int size1 = cost.size();
        int size2 = cost.get(0).size();
        
        // Precompute the minimum cost to connect each point in group 1 
        // to at least one point in group 2.
        int[] minCostGroup2 = new int[size2];
        for (int j = 0; j < size2; j++) {
            int minVal = Integer.MAX_VALUE;
            for (int i = 0; i < size1; i++) {
                minVal = Math.min(minVal, cost.get(i).get(j));
            }
            minCostGroup2[j] = minVal;
        }
        
        // dp[i][mask] is the minimum cost to connect the first i points of group 1,
        // with mask representing which points in group 2 are connected.
        int[][] dp = new int[size1 + 1][1 << size2];
        for (int[] row : dp) {
            Arrays.fill(row, 1000000000);
        }
        dp[0][0] = 0;
        
        for (int i = 0; i < size1; i++) {
            for (int mask = 0; mask < (1 << size2); mask++) {
                if (dp[i][mask] >= 1000000000) continue;
                
                // For point i of group 1, try connecting it to every point j of group 2
                for (int j = 0; j < size2; j++) {
                    int nextMask = mask | (1 << j);
                    dp[i + 1][nextMask] = Math.min(dp[i + 1][nextMask], dp[i][mask] + cost.get(i).get(j));
                }
            }
        }
        
        // After connecting all points in group 1, some points in group 2 might not be connected.
        // We must ensure every point in group 2 is covered.
        int[] finalDp = dp[size1];
        int[] result = new int[1 << size2];
        System.arraycopy(finalDp, 0, result, 0, 1 << size2);
        
        for (int mask = 0; mask < (1 << size2); mask++) {
            int currentCost = result[mask];
            for (int j = 0; j < size2; j++) {
                if ((mask & (1 << j)) == 0) {
                    currentCost += minCostGroup2[j];
                }
            }
            result[mask] = currentCost;
        }
        
        // The answer is the cost when all points in group 2 are connected (mask = (1 << size2) - 1)
        return result[(1 << size2) - 1];
    }
}