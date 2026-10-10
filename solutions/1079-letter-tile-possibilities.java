import java.util.Arrays;

class Solution {
    public int numTilePossibilities(String tiles) {
        int[] counts = new int[26];
        for (char c : tiles.toCharArray()) {
            counts[c - 'A']++;
        }
        return backtrack(counts);
    }

    private int backtrack(int[] counts) {
        int sum = 0;
        for (int i = 0; i < 26; i++) {
            if (counts[i] > 0) {
                // We choose this letter
                sum++;
                // Use the letter and recurse
                counts[i]--;
                sum += backtrack(counts);
                // Backtrack: restore the count
                counts[i]++;
            }
        }
        return sum;
    }
}