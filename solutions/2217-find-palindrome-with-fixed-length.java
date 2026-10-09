import java.util.*;

class Solution {
    public long[] kthPalindrome(int[] queries, int intLength) {
        int n = queries.length;
        long[] result = new long[n];
        
        // A palindrome of length L is determined by its first half of length (L + 1) / 2.
        // For example, if intLength = 3, half length = 2. The smallest is 10 (101), next is 11 (111).
        // If intLength = 4, half length = 2. The smallest is 10 (1001), next is 11 (1111).
        int halfLen = (intLength + 1) / 2;
        long start = (long) Math.pow(10, halfLen - 1);
        long end = (long) Math.pow(10, halfLen) - 1;
        
        long maxCount = end - start + 1;
        
        for (int i = 0; i < n; i++) {
            if (queries[i] > maxCount) {
                result[i] = -1;
            } else {
                long firstHalf = start + (queries[i] - 1);
                String s = Long.toString(firstHalf);
                StringBuilder sb = new StringBuilder(s);
                
                // If intLength is odd, the middle digit is not repeated.
                // If intLength is even, the entire first half is mirrored.
                String suffix = new StringBuilder(s.substring(0, intLength / 2)).reverse().toString();
                String palindromeStr = s + suffix;
                
                result[i] = Long.parseLong(palindromeStr);
            }
        }
        
        return result;
    }
}