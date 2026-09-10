class Solution {
    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int num : nums) {
            sum += num;
        }

        // If the sum is odd, it's impossible to split into two equal integer subsets
        if (sum % 2 != 0) {
            return false;
        }

        int target = sum / 2;
        
        // This is a variation of the 0/1 Knapsack problem.
        // We want to know if there exists a subset with sum equal to target.
        // dp[i] will be true if a sum of i is possible.
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;

        for (int num : nums) {
            // Traverse backwards to ensure each number is used only once per subset
            for (int i = target; i >= num; i--) {
                if (dp[i - num]) {
                    dp[i] = true;
                }
            }
            
            // Optimization: If we found the target, we can return early
            if (dp[target]) {
                return true;
            }
        }

        return dp[target];
    }
}