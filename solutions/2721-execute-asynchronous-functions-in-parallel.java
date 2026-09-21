import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * In Java, asynchronous operations are typically handled using CompletableFuture.
 * This implementation simulates the behavior of Promise.all by running functions in parallel
 * and aggregating results or handling the first rejection.
 */
public class Solution {

    /**
     * Executes an array of asynchronous functions (represented by Suppliers returning CompletableFutures)
     * in parallel.
     * 
     * @param functions An array of Suppliers, where each returns a CompletableFuture.
     * @return A CompletableFuture that resolves with an array of results or rejects with the first error.
     */
    public CompletableFuture<Object[]> promiseAll(Supplier<CompletableFuture<Object>>[] functions) {
        int n = functions.length;
        CompletableFuture<Object[]> resultFuture = new CompletableFuture<>();
        
        if (n == 0) {
            resultFuture.complete(new Object[0]);
            return resultFuture;
        }

        Object[] results = new Object[n];
        int[] completedCount = {0};
        boolean[] rejected = {false};

        for (int i = 0; i < n; i++) {
            final int index = i;
            functions[i].get().thenAccept(value -> {
                synchronized (resultFuture) {
                    if (rejected[0]) return;
                    
                    results[index] = value;
                    completedCount[0]++;
                    
                    if (completedCount[0] == n) {
                        resultFuture.complete(results);
                    }
                }
            }).exceptionally(ex -> {
                synchronized (resultFuture) {
                    if (!rejected[0]) {
                        rejected[0] = true;
                        // Unpack the CompletionException to get the original cause
                        resultFuture.completeExceptionally(ex.getCause() != null ? ex.getCause() : ex);
                    }
                }
                return null;
            });
        }

        return resultFuture;
    }
}