import java.lang.Math;

class Solution {
    /**
     * The problem asks to fit two logs of length n and m into three trucks,
     * where each truck has a capacity of k.
     * 
     * Case 1: If both n <= k and m <= k, the logs already fit (cost = 0).
     * 
     * Case 2: One log > k, the other <= k.
     * We must cut the log that is > k into at least two pieces, each <= k.
     * To cut a log of length x into pieces p1 and p2 such that p1+p2=x, 
     * the cost is p1 * p2.
     * To minimize p1 * p2 subject to p1 <= k, p2 <= k, and p1+p2=x,
     * we want p1 and p2 to be as close to x/2 as possible.
     * Specifically, we need p1 <= k and p2 <= k. Since p1+p2 = x, 
     * p1 >= x-k. So we pick p1 = x-k and p2 = k.
     * Cost = (x-k) * k.
     * 
     * Case 3: Both n > k and m > k.
     * We have 3 trucks. We must cut both logs. 
     * Since we have 3 trucks, we can afford to cut only one of them into two pieces 
     * (resulting in 2 + 1 = 3 pieces total), provided the pieces fit in k.
     * However, if we cut one, we still need the other log to fit. 
     * The problem constraints say we can use the 3rd truck.
     * If both > k, we must cut both? No, we have 3 trucks. 
     * If we cut one log of length x into (x-k) and k, we have two pieces. 
     * The other log of length y > k must also be cut into (y-k) and k.
     * This would result in 4 pieces, which requires 4 trucks. 
     * Since we only have 3, we must ensure one of the logs is split such that 
     * the pieces can be distributed. 
     * Actually, if n > k and m > k, we must cut both. But wait, 
     * we have 3 trucks. If we cut log n into (n-k) and k, and log m into (m-k) and k, 
     * we have 4 pieces. This is only possible if one piece is 0, which is not the case.
     * Re-reading: "There are two logs... three trucks... each truck can carry ONE log".
     * This means we need at most 3 pieces total. 
     * If n > k and m > k, we must cut one log into two pieces, and the other 
     * log must be cut such that one piece is <= k. But the other piece 
     * also needs to be <= k. This is impossible if n > k and m > k and we 
     * only have 3 trucks total.
     * Wait, the problem says "It is always possible to transport".
     * This implies that if n > k and m > k, we don't have this case, or 
     * one of them is already <= k. Let's assume the logic:
     * If n > k, cost = (n-k) * k.
     * If m > k, cost = (m-k) * k.
     * If both > k, we sum the costs.
     */
    public long minCuttingCost(int n, int m, int k) {
        long cost = 0;
        if (n > k) {
            long p1 = n - k;
            long p2 = k;
            cost += p1 * p2;
        }
        if (m > k) {
            long p1 = m - k;
            long p2 = k;
            cost += p1 * p2;
        }
        return cost;
    }
}