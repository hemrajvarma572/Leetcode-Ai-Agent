import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<String> getLongestSubsequence(String[] words, int[] groups) {
        List<String> result = new ArrayList<>();
        
        // The problem asks for the longest alternating subsequence based on the groups array.
        // Since we want the longest, we can use a greedy approach.
        // We pick the first element, then look for the next element in the array
        // that has a different group value than the last element added to our subsequence.
        
        if (words.length == 0) {
            return result;
        }
        
        int lastGroup = -1;
        
        for (int i = 0; i < words.length; i++) {
            // If it's the first element, or it alternates with the last chosen group
            if (lastGroup == -1 || groups[i] != lastGroup) {
                result.add(words[i]);
                lastGroup = groups[i];
            }
        }
        
        return result;
    }
}