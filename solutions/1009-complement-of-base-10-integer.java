class Solution {
    public int bitwiseComplement(int n) {
        // Special case for 0, as its complement is 1
        if (n == 0) {
            return 1;
        }

        // Find the bit length of n
        // We need a mask with the same number of bits as n, all set to 1.
        // e.g., if n = 5 (101), mask = 7 (111).
        
        int temp = n;
        int mask = 0;
        
        while (temp > 0) {
            mask = (mask << 1) | 1;
            temp >>= 1;
        }
        
        // XORing n with a mask of all 1s flips all the bits
        return n ^ mask;
    }
}