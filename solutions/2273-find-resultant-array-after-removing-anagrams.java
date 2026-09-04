import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    public List<String> removeAnagrams(String[] words) {
        List<String> result = new ArrayList<>();
        if (words == null || words.length == 0) {
            return result;
        }

        // Add the first word as it can never be removed
        result.add(words[0]);
        String lastSorted = sortString(words[0]);

        for (int i = 1; i < words.length; i++) {
            String currentSorted = sortString(words[i]);
            // If the current word is not an anagram of the last kept word, keep it
            if (!currentSorted.equals(lastSorted)) {
                result.add(words[i]);
                lastSorted = currentSorted;
            }
        }

        return result;
    }

    private String sortString(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}