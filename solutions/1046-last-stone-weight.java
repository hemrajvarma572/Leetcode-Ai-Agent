import java.util.PriorityQueue;
import java.util.Collections;

class Solution {
    public int lastStoneWeight(int[] stones) {
        // Use a PriorityQueue with a reverse order comparator to act as a Max-Heap
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        
        // Add all stones into the Max-Heap
        for (int stone : stones) {
            maxHeap.add(stone);
        }
        
        // While there is more than one stone, perform the smash operation
        while (maxHeap.size() > 1) {
            int y = maxHeap.poll(); // Heaviest
            int x = maxHeap.poll(); // Second heaviest
            
            // If they are not equal, the result of the smash is (y - x)
            if (y > x) {
                maxHeap.add(y - x);
            }
            // If they are equal, both are destroyed, so we don't add anything back
        }
        
        // If the heap is empty, return 0, otherwise return the last remaining stone
        return maxHeap.isEmpty() ? 0 : maxHeap.peek();
    }
}