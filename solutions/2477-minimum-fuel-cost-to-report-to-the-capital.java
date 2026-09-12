import java.util.*;

class Solution {
    private long fuel = 0;
    private List<List<Integer>> adj;
    private int seats;

    public long minimumFuelCost(int[][] roads, int seats) {
        int n = roads.length + 1;
        if (n == 1) return 0;
        
        this.seats = seats;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        
        for (int[] road : roads) {
            adj.get(road[0]).add(road[1]);
            adj.get(road[1]).add(road[0]);
        }
        
        dfs(0, -1);
        return fuel;
    }

    /**
     * DFS returns the number of representatives currently at this node
     * (including representatives from its subtree).
     */
    private long dfs(int u, int p) {
        long representatives = 1; // The representative from this city itself
        
        for (int v : adj.get(u)) {
            if (v != p) {
                representatives += dfs(v, u);
            }
        }
        
        // If this node is not the capital, representatives must move to their parent.
        // The number of cars needed for 'representatives' people is ceil(representatives / seats).
        // Each car consumes 1 liter of fuel for the edge (u, p).
        if (u != 0) {
            fuel += (representatives + seats - 1) / seats;
        }
        
        return representatives;
    }
}