import java.util.Arrays;

class Solution {
    public int matchPlayersAndTrainers(int[] players, int[] trainers) {
        // Sort both arrays to use the greedy two-pointer approach
        Arrays.sort(players);
        Arrays.sort(trainers);
        
        int playerIdx = 0;
        int trainerIdx = 0;
        int matchCount = 0;
        
        // Iterate through both arrays
        // Try to match the smallest player with the smallest possible trainer
        // that can handle their ability.
        while (playerIdx < players.length && trainerIdx < trainers.length) {
            if (players[playerIdx] <= trainers[trainerIdx]) {
                // If the current trainer can train this player, match them
                matchCount++;
                playerIdx++;
                trainerIdx++;
            } else {
                // If the trainer is too weak for the current player,
                // move to the next stronger trainer
                trainerIdx++;
            }
        }
        
        return matchCount;
    }
}