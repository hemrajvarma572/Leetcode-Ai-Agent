import java.util.HashMap;
import java.util.Map;

class Solution {
    public int medianOfUniquenessArray(int[] nums) {
        long n = nums.length;
        long totalSubarrays = n * (n + 1) / 2;
        // The median is the ((totalSubarrays + 1) / 2)-th smallest element
        long target = (totalSubarrays + 1) / 2;

        int low = 1, high = (int) n;
        int ans = high;

        // Binary search for the smallest value X such that count of subarrays
        // with distinct elements <= X is at least target.
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (countSubarraysWithAtMostKDistinct(nums, mid) >= target) {
                ans = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return ans;
    }

    /**
     * Counts how many subarrays have at most k distinct elements using a sliding window.
     */
    private long countSubarraysWithAtMostKDistinct(int[] nums, int k) {
        long count = 0;
        int left = 0;
        Map<Integer, Integer> map = new HashMap<>();
        
        for (int right = 0; right < nums.length; right++) {
            map.put(nums[right], map.getOrDefault(nums[right], 0) + 1);
            
            while (map.size() > k) {
                int leftVal = nums[left];
                int freq = map.get(leftVal);
                if (freq == 1) {
                    map.remove(leftVal);
                } else {
                    map.put(leftVal, freq - 1);
                }
                left++;
            }
            // All subarrays ending at 'right' starting from [left...right] 
            // have at most k distinct elements.
            count += (right - left + 1);
        }
        return count;
    }
}