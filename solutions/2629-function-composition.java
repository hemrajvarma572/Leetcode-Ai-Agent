import java.util.function.Function;

public class Solution {
    /**
     * Given an array of functions, returns a function that represents their composition.
     * The composition f(g(h(x))) is evaluated from right to left (h, then g, then f).
     * 
     * @param functions An array of functions where each function maps an Integer to an Integer.
     * @return A single function representing the composition.
     */
    public Function<Integer, Integer> compose(Function<Integer, Integer>[] functions) {
        return x -> {
            int result = x;
            // Iterate backwards through the array to apply functions from right to left.
            // f(g(h(x))) means h is applied first, then g, then f.
            for (int i = functions.length - 1; i >= 0; i--) {
                result = functions[i].apply(result);
            }
            return result;
        };
    }
}