import java.util.*;

class Solution {
    public int countOfPairs(int[] nums) {
        int n = nums.length;
        int maxVal = 50;
        int MOD = 1_000_000_007;

        // dp[i][j] represents the number of ways to form pairs for the prefix of nums 
        // up to index i, where arr1[i] = j.
        // Since arr1[i] + arr2[i] = nums[i], arr2[i] is implicitly nums[i] - j.
        // The constraints are:
        // 1. arr1[i-1] <= arr1[i]  => j_prev <= j
        // 2. arr2[i-1] >= arr2[i]  => nums[i-1] - j_prev >= nums[i] - j 
        //                            => j_prev <= j - (nums[i] - nums[i-1])

        // So j_prev <= min(j, j - (nums[i] - nums[i-1]))
        // Let limit = j - max(0, nums[i] - nums[i-1])
        // dp[i][j] = sum(dp[i-1][k]) for 0 <= k <= limit

        int[][] dp = new int[n][maxVal + 1];

        // Base case: i = 0
        for (int j = 0; j <= nums[0]; j++) {
            dp[0][j] = 1;
        }

        for (int i = 1; i < n; i++) {
            // Precompute prefix sums of the previous DP row to optimize inner loop
            int[] prefixSum = new int[maxVal + 2];
            for (int j = 0; j <= maxVal; j++) {
                prefixSum[j + 1] = (prefixSum[j] + dp[i - 1][j]) % MOD;
            }

            for (int j = 0; j <= nums[i]; j++) {
                int diff = nums[i] - nums[i - 1];
                int limit = j - Math.max(0, diff);
                
                if (limit >= 0) {
                    // We need sum of dp[i-1][0...limit]
                    // Which is stored in prefixSum[limit + 1]
                    dp[i][j] = prefixSum[Math.min(limit + 1, maxVal + 1)];
                } else {
                    dp[i][j] = 0;
                }
            }
        }

        int total = 0;
        for (int j = 0; j <= maxVal; j++) {
            total = (total + dp[n - 1][j]) % MOD;
        }

        return total;
    }
}