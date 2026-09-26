import java.util.*;

class Solution {
    public int maxConsecutiveAnswers(String answerKey, int k) {
        // The problem asks for the maximum consecutive 'T's or 'F's
        // We can solve this using a sliding window approach.
        // We calculate the max window size for 'T's (by flipping 'F's)
        // and for 'F's (by flipping 'T's), then return the maximum of the two.
        
        return Math.max(getMaxConsecutive(answerKey, k, 'T'), getMaxConsecutive(answerKey, k, 'F'));
    }

    private int getMaxConsecutive(String answerKey, int k, char targetChar) {
        int left = 0;
        int right = 0;
        int count = 0; // count of non-target characters in current window
        int maxLen = 0;
        
        // We want to count how many 'other' characters are in our window [left, right]
        // If count exceeds k, shrink the window from the left.
        
        for (right = 0; right < answerKey.length(); right++) {
            if (answerKey.charAt(right) != targetChar) {
                count++;
            }
            
            while (count > k) {
                if (answerKey.charAt(left) != targetChar) {
                    count--;
                }
                left++;
            }
            
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }
}