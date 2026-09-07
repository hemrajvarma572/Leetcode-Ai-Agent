class Solution {
    public double findMaxAverage(int[] nums, int k) {
        long currentSum = 0;
        
        // Calculate the sum of the first k elements
        for (int i = 0; i < k; i++) {
            currentSum += nums[i];
        }
        
        long maxSum = currentSum;
        
        // Use sliding window to find the maximum sum of k consecutive elements
        for (int i = k; i < nums.length; i++) {
            currentSum = currentSum - nums[i - k] + nums[i];
            if (currentSum > maxSum) {
                maxSum = currentSum;
            }
        }
        
        // The maximum average is the maximum sum divided by k
        return (double) maxSum / k;
    }
}