import java.util.HashMap;
import java.util.Map;

class Solution {
    public int[] numberOfPairs(int[] nums) {
        // Since the numbers are between 0 and 100, we can use an array
        // as a frequency map for efficiency, or a HashMap.
        int[] counts = new int[101];
        for (int num : nums) {
            counts[num]++;
        }

        int pairs = 0;
        int leftovers = 0;

        for (int count : counts) {
            // For each number, count / 2 is the number of pairs
            // count % 2 is the number of leftover elements
            pairs += count / 2;
            leftovers += count % 2;
        }

        return new int[]{pairs, leftovers};
    }
}