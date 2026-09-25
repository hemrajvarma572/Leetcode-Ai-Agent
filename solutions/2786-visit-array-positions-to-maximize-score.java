class Solution {
    public long maxScore(int[] nums, int x) {
        int n = nums.length;
        // dp[0] stores the max score ending with an even number
        // dp[1] stores the max score ending with an odd number
        // Initialize with a very small value to represent unreachable states
        long[] dp = new long[2];
        dp[0] = -1000000000000L;
        dp[1] = -1000000000000L;

        // Base case: starting at index 0
        int parity = nums[0] % 2;
        dp[parity] = nums[0];

        for (int i = 1; i < n; i++) {
            int currentParity = nums[i] % 2;
            int otherParity = 1 - currentParity;

            // Option 1: Move from the same parity (no penalty)
            // Option 2: Move from the different parity (penalty x)
            long same = dp[currentParity] + nums[i];
            long diff = dp[otherParity] + nums[i] - x;

            dp[currentParity] = Math.max(same, diff);
        }

        return Math.max(dp[0], dp[1]);
    }
}