import java.util.HashMap;
import java.util.Map;

class Solution {
    public int numberOfGoodPartitions(int[] nums) {
        int n = nums.length;
        Map<Integer, Integer> lastOccurrence = new HashMap<>();
        for (int i = 0; i < n; i++) {
            lastOccurrence.put(nums[i], i);
        }

        // We identify points where we can split the array.
        // A partition can be split at index i if for all subarrays before or at i, 
        // no element's last occurrence appears after i.
        int partitions = 0;
        int maxLastIndex = 0;
        for (int i = 0; i < n - 1; i++) {
            maxLastIndex = Math.max(maxLastIndex, lastOccurrence.get(nums[i]));
            
            // If the current index i is the end of a block where all elements' 
            // last occurrences are <= i, we can make a cut here.
            if (maxLastIndex == i) {
                partitions++;
            }
        }

        // If there are 'partitions' possible cut locations, we can choose any 
        // subset of these locations to cut. Each cut location acts as a binary 
        // choice (cut or no cut).
        // Total ways = 2^(number of possible cut locations).
        return power(2, partitions);
    }

    private int power(long base, int exp) {
        long res = 1;
        long mod = 1_000_000_007;
        base %= mod;
        while (exp > 0) {
            if (exp % 2 == 1) res = (res * base) % mod;
            base = (base * base) % mod;
            exp /= 2;
        }
        return (int) res;
    }
}