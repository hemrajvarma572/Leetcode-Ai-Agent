import java.util.*;

class Solution {
    private static final int MOD = 1000000007;

    public int makeStringSorted(String s) {
        int n = s.length();
        int[] count = new int[26];
        for (char c : s.toCharArray()) {
            count[c - 'a']++;
        }

        long[] fact = new long[n + 1];
        long[] invFact = new long[n + 1];
        fact[0] = 1;
        invFact[0] = 1;
        for (int i = 1; i <= n; i++) {
            fact[i] = (fact[i - 1] * i) % MOD;
        }
        invFact[n] = power(fact[n], MOD - 2);
        for (int i = n - 1; i >= 1; i--) {
            invFact[i] = (invFact[i + 1] * (i + 1)) % MOD;
        }

        long ans = 0;
        for (int i = 0; i < n; i++) {
            int current = s.charAt(i) - 'a';
            
            // Count number of smaller characters that can be placed at current position
            for (int j = 0; j < current; j++) {
                if (count[j] > 0) {
                    count[j]--;
                    
                    // Number of permutations of remaining characters:
                    // (n - 1 - i)! / (c0! * c1! * ... * c25!)
                    long ways = fact[n - 1 - i];
                    for (int k = 0; k < 26; k++) {
                        if (count[k] > 1) {
                            ways = (ways * invFact[count[k]]) % MOD;
                        }
                    }
                    
                    ans = (ans + ways) % MOD;
                    count[j]++;
                }
            }
            count[current]--;
        }

        return (int) ans;
    }

    private long power(long base, long exp) {
        long res = 1;
        base %= MOD;
        while (exp > 0) {
            if (exp % 2 == 1) res = (res * base) % MOD;
            base = (base * base) % MOD;
            exp /= 2;
        }
        return res;
    }
}