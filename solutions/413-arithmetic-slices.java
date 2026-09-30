class Solution {
    public int numberOfArithmeticSlices(int[] nums) {
        if (nums == null || nums.length < 3) {
            return 0;
        }

        int n = nums.length;
        int totalSlices = 0;
        int currentCount = 0;

        // An arithmetic slice must have at least 3 elements.
        // We iterate through the array, checking if nums[i] - nums[i-1] == nums[i-1] - nums[i-2].
        // If they are equal, it extends the previous arithmetic sequence.
        // If a sequence of length k ends at i, it adds (k-2) new arithmetic subarrays ending at i.
        // For example:
        // [1, 2, 3] -> 1 slice (dp[2] = 1)
        // [1, 2, 3, 4] -> 2 new slices ([2,3,4], [1,2,3,4]) (dp[3] = dp[2] + 1 = 2)
        // Total = 1 + 2 = 3.
        
        for (int i = 2; i < n; i++) {
            if (nums[i] - nums[i - 1] == nums[i - 1] - nums[i - 2]) {
                currentCount++;
                totalSlices += currentCount;
            } else {
                currentCount = 0;
            }
        }

        return totalSlices;
    }
}