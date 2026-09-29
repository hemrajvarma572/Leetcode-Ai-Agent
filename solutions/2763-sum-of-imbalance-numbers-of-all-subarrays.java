import java.util.HashSet;
import java.util.Set;

public class Solution {
    /**
     * The imbalance number of an array is the count of elements x such that x+1 is not in the array,
     * excluding the minimum element of the array.
     * 
     * To solve this, we iterate over all subarrays. Since N <= 1000, an O(N^2) solution is efficient enough.
     * For each starting index i, we expand the subarray to j and maintain a set of numbers present 
     * in the current subarray nums[i...j].
     * 
     * When adding nums[j] to a set that already contains elements of nums[i...j-1]:
     * - The imbalance number changes based on whether nums[j]-1 and nums[j]+1 exist in the current set.
     * - If x is added:
     *   - If x-1 and x+1 both exist, adding x bridges a gap, decreasing imbalance by 1.
     *   - If neither x-1 nor x+1 exist, adding x creates a new gap, increasing imbalance by 1.
     *   - If only one of x-1 or x+1 exists, the imbalance remains the same.
     * 
     * Since the constraints are small (nums[i] <= 1000), we can use a boolean array to track 
     * presence in the current window for O(1) lookups.
     */
    public int sumImbalanceNumbers(int[] nums) {
        int n = nums.length;
        int totalImbalance = 0;

        for (int i = 0; i < n; i++) {
            boolean[] present = new boolean[n + 2];
            int currentImbalance = 0;
            
            // Start subarray at i, extend to j
            for (int j = i; j < n; j++) {
                int x = nums[j];
                
                if (!present[x]) {
                    // Check neighbors to see how imbalance changes
                    if (x > 1 && x <= n && present[x - 1] && present[x + 1]) {
                        currentImbalance--;
                    } else if (!(x > 1 && present[x - 1]) && !(x <= n && present[x + 1])) {
                        // If x is not the smallest element (which is handled by ignoring the 
                        // smallest element check naturally if we define imbalance carefully).
                        // Actually, the definition says: imbalance is number of s[i+1] such that s[i+1] > s[i] + 1.
                        // This is equivalent to counting x such that x+1 is not in the set, 
                        // excluding the maximum element in the set.
                        if (x != getMax(present, n)) {
                            currentImbalance++;
                        }
                    }
                    present[x] = true;
                }
                totalImbalance += currentImbalance;
            }
        }
        return totalImbalance;
    }

    private int getMax(boolean[] present, int n) {
        for (int i = n; i >= 1; i--) {
            if (present[i]) return i;
        }
        return -1;
    }
}