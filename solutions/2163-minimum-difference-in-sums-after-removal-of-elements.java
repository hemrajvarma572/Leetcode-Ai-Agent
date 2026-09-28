import java.util.PriorityQueue;
import java.util.Collections;

class Solution {
    public long minimumDifference(int[] nums) {
        int n = nums.length / 3;
        
        // leftMin[i] will store the minimum sum of n elements chosen from the first i elements.
        // We use a max-heap to keep track of the n smallest elements seen so far.
        long[] leftMin = new long[3 * n];
        PriorityQueue<Long> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        long currentSum = 0;
        
        for (int i = 0; i < 2 * n; i++) {
            currentSum += nums[i];
            maxHeap.offer((long) nums[i]);
            if (maxHeap.size() > n) {
                currentSum -= maxHeap.poll();
            }
            if (maxHeap.size() == n) {
                leftMin[i] = currentSum;
            }
        }
        
        // rightMax[i] will store the maximum sum of n elements chosen from the last i elements.
        // We use a min-heap to keep track of the n largest elements seen so far.
        long[] rightMax = new long[3 * n];
        PriorityQueue<Long> minHeap = new PriorityQueue<>();
        currentSum = 0;
        
        for (int i = 3 * n - 1; i >= n; i--) {
            currentSum += nums[i];
            minHeap.offer((long) nums[i]);
            if (minHeap.size() > n) {
                currentSum -= minHeap.poll();
            }
            if (minHeap.size() == n) {
                rightMax[i] = currentSum;
            }
        }
        
        // The split point k can range from n-1 to 2n-1.
        // The left part uses elements from index 0 to k, right part from k+1 to 3n-1.
        long minDiff = Long.MAX_VALUE;
        for (int k = n - 1; k < 2 * n; k++) {
            minDiff = Math.min(minDiff, leftMin[k] - rightMax[k + 1]);
        }
        
        return minDiff;
    }
}