import java.util.TreeSet;

class Solution {
    public int secondHighest(String s) {
        TreeSet<Integer> digits = new TreeSet<>();
        
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                digits.add(c - '0');
            }
        }
        
        if (digits.size() < 2) {
            return -1;
        }
        
        // Remove the largest element to get the second largest
        digits.pollLast();
        return digits.last();
    }
}