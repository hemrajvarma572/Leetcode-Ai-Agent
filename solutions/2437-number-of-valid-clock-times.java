class Solution {
    public int countTime(String time) {
        int hourWays = 0;
        int minuteWays = 0;

        // Calculate valid possibilities for the hours part (hh)
        for (int h = 0; h < 24; h++) {
            String hh = String.format("%02d", h);
            if (matches(hh, time.substring(0, 2))) {
                hourWays++;
            }
        }

        // Calculate valid possibilities for the minutes part (mm)
        for (int m = 0; m < 60; m++) {
            String mm = String.format("%02d", m);
            if (matches(mm, time.substring(3, 5))) {
                minuteWays++;
            }
        }

        return hourWays * minuteWays;
    }

    private boolean matches(String val, String pattern) {
        for (int i = 0; i < 2; i++) {
            if (pattern.charAt(i) != '?' && pattern.charAt(i) != val.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}