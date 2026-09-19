import java.util.HashMap;
import java.util.Map;

class Solution {
    public String entityParser(String text) {
        Map<String, String> map = new HashMap<>();
        map.put("&quot;", "\"");
        map.put("&apos;", "'");
        map.put("&amp;", "&");
        map.put("&gt;", ">");
        map.put("&lt;", "<");
        map.put("&frasl;", "/");

        StringBuilder sb = new StringBuilder();
        int n = text.length();
        int i = 0;
        while (i < n) {
            if (text.charAt(i) == '&') {
                int semiIndex = text.indexOf(';', i);
                if (semiIndex != -1 && (semiIndex - i) <= 7) {
                    String entity = text.substring(i, semiIndex + 1);
                    if (map.containsKey(entity)) {
                        sb.append(map.get(entity));
                        i = semiIndex + 1;
                        continue;
                    }
                }
            }
            sb.append(text.charAt(i));
            i++;
        }
        return sb.toString();
    }
}