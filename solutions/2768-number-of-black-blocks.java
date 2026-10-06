import java.util.HashMap;
import java.util.Map;

class Solution {
    public long[] countBlackBlocks(int m, int n, int[][] coordinates) {
        // A block is defined by its top-left corner (r, c) where 0 <= r < m-1, 0 <= c < n-1.
        // A black cell at (x, y) contributes to at most 4 blocks:
        // (x-1, y-1), (x-1, y), (x, y-1), (x, y)
        // We only care about blocks that have at least one black cell.
        
        Map<Long, Integer> blockCount = new HashMap<>();
        
        for (int[] coord : coordinates) {
            int x = coord[0];
            int y = coord[1];
            
            // Check potential top-left corners of blocks that include (x, y)
            for (int i = x - 1; i <= x; i++) {
                for (int j = y - 1; j <= y; j++) {
                    if (i >= 0 && i < m - 1 && j >= 0 && j < n - 1) {
                        long key = (long) i * n + j;
                        blockCount.put(key, blockCount.getOrDefault(key, 0) + 1);
                    }
                }
            }
        }
        
        long[] result = new long[5];
        
        // Count blocks with 1 to 4 black cells
        int blocksWithBlackCells = 0;
        for (int count : blockCount.values()) {
            result[count]++;
            blocksWithBlackCells++;
        }
        
        // Total number of possible 2x2 blocks is (m-1) * (n-1)
        long totalBlocks = (long) (m - 1) * (n - 1);
        
        // Blocks with 0 black cells is the remainder
        result[0] = totalBlocks - blocksWithBlackCells;
        
        return result;
    }
}