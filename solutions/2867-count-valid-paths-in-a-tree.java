import java.util.*;

class Solution {
    private List<Integer>[] adj;
    private boolean[] isPrime;
    private long totalValidPaths = 0;

    public long countPaths(int n, int[][] edges) {
        isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;
        for (int p = 2; p * p <= n; p++) {
            if (isPrime[p]) {
                for (int i = p * p; i <= n; i += p)
                    isPrime[i] = false;
            }
        }

        adj = new ArrayList[n + 1];
        for (int i = 1; i <= n; i++) adj[i] = new ArrayList<>();
        for (int[] edge : edges) {
            adj[edge[0]].add(edge[1]);
            adj[edge[1]].add(edge[0]);
        }

        boolean[] removed = new boolean[n + 1];
        decompose(1, n, removed);
        return totalValidPaths;
    }

    private void decompose(int node, int n, boolean[] removed) {
        int[] sz = new int[n + 1];
        int totalNodes = getSizes(node, -1, sz, removed);
        int centroid = findCentroid(node, -1, totalNodes, sz, removed);

        removed[centroid] = true;
        
        if (isPrime[centroid]) {
            Map<Integer, Long> compositeSubtreeCounts = new HashMap<>();
            compositeSubtreeCounts.put(0, 1L);
            
            for (int neighbor : adj[centroid]) {
                if (!removed[neighbor]) {
                    Map<Integer, Long> currentSubtree = new HashMap<>();
                    dfsCount(neighbor, centroid, 0, currentSubtree, removed);
                    
                    for (Map.Entry<Integer, Long> entry : currentSubtree.entrySet()) {
                        totalValidPaths += entry.getValue() * compositeSubtreeCounts.getOrDefault(0, 0L);
                    }
                    for (Map.Entry<Integer, Long> entry : currentSubtree.entrySet()) {
                        compositeSubtreeCounts.put(0, compositeSubtreeCounts.getOrDefault(0, 0L) + entry.getValue());
                    }
                }
            }
        } else {
            Map<Integer, Long> primeCount = new HashMap<>();
            Map<Integer, Long> compositeCount = new HashMap<>();
            compositeCount.put(0, 1L);
            
            for (int neighbor : adj[centroid]) {
                if (!removed[neighbor]) {
                    Map<Integer, Long> subtreePrimes = new HashMap<>();
                    Map<Integer, Long> subtreeComposites = new HashMap<>();
                    dfsCountPrime(neighbor, centroid, 0, subtreePrimes, subtreeComposites, removed);
                    
                    for (Map.Entry<Integer, Long> entry : subtreePrimes.entrySet()) {
                        totalValidPaths += entry.getValue() * compositeCount.getOrDefault(0, 0L);
                    }
                    for (Map.Entry<Integer, Long> entry : subtreeComposites.entrySet()) {
                        totalValidPaths += entry.getValue() * primeCount.getOrDefault(1, 0L);
                    }
                    for (Map.Entry<Integer, Long> entry : subtreePrimes.entrySet()) {
                        primeCount.put(1, primeCount.getOrDefault(1, 0L) + entry.getValue());
                    }
                    for (Map.Entry<Integer, Long> entry : subtreeComposites.entrySet()) {
                        compositeCount.put(0, compositeCount.getOrDefault(0, 0L) + entry.getValue());
                    }
                }
            }
        }

        for (int neighbor : adj[centroid]) {
            if (!removed[neighbor]) {
                decompose(neighbor, n, removed);
            }
        }
    }

    private int getSizes(int u, int p, int[] sz, boolean[] removed) {
        sz[u] = 1;
        for (int v : adj[u]) if (v != p && !removed[v]) sz[u] += getSizes(v, u, sz, removed);
        return sz[u];
    }

    private int findCentroid(int u, int p, int n, int[] sz, boolean[] removed) {
        for (int v : adj[u]) if (v != p && !removed[v] && sz[v] > n / 2) return findCentroid(v, u, n, sz, removed);
        return u;
    }

    private void dfsCount(int u, int p, int count, Map<Integer, Long> map, boolean[] removed) {
        if (isPrime[u]) return;
        map.put(0, map.getOrDefault(0, 0L) + 1);
        for (int v : adj[u]) if (v != p && !removed[v]) dfsCount(v, u, count, map, removed);
    }

    private void dfsCountPrime(int u, int p, int count, Map<Integer, Long> pMap, Map<Integer, Long> cMap, boolean[] removed) {
        int nextCount = count + (isPrime[u] ? 1 : 0);
        if (nextCount > 1) return;
        if (nextCount == 1) pMap.put(1, pMap.getOrDefault(1, 0L) + 1);
        else cMap.put(0, cMap.getOrDefault(0, 0L) + 1);
        for (int v : adj[u]) if (v != p && !removed[v]) dfsCountPrime(v, u, nextCount, pMap, cMap, removed);
    }
}