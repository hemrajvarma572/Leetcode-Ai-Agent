import java.util.*;

class Solution {
    public int minimumDiameterAfterMerge(int[][] edges1, int[][] edges2) {
        int d1 = getDiameter(edges1);
        int d2 = getDiameter(edges2);
        
        // Let radius1 = ceil(d1/2), radius2 = ceil(d2/2)
        // Connecting the centers of the two trees results in a new diameter of:
        // max(d1, d2, radius1 + radius2 + 1)
        int r1 = (d1 + 1) / 2;
        int r2 = (d2 + 1) / 2;
        
        return Math.max(d1, Math.max(d2, r1 + r2 + 1));
    }

    private int getDiameter(int[][] edges) {
        int n = edges.length + 1;
        if (n <= 1) return 0;
        
        List<List<Integer>> adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }
        
        // First BFS to find the furthest node from an arbitrary node (0)
        int[] res1 = bfs(0, n, adj);
        // Second BFS to find the diameter from that furthest node
        int[] res2 = bfs(res1[0], n, adj);
        
        return res2[1];
    }

    private int[] bfs(int start, int n, List<List<Integer>> adj) {
        int[] dist = new int[n];
        Arrays.fill(dist, -1);
        Queue<Integer> q = new LinkedList<>();
        
        q.add(start);
        dist[start] = 0;
        
        int furthestNode = start;
        int maxDist = 0;
        
        while (!q.isEmpty()) {
            int u = q.poll();
            if (dist[u] > maxDist) {
                maxDist = dist[u];
                furthestNode = u;
            }
            
            for (int v : adj.get(u)) {
                if (dist[v] == -1) {
                    dist[v] = dist[u] + 1;
                    q.add(v);
                }
            }
        }
        return new int[]{furthestNode, maxDist};
    }
}