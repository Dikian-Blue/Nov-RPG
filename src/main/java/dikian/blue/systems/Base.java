package dikian.blue.systems;

import java.util.ArrayList;
import java.util.List;

public class Base {

    public static String chatColor(String str) {
        List<String> codes1 = new ArrayList<>();
        List<String> codes2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            codes1.add("&" + i);
            codes2.add("§" + i);
        }
        for (char i = 'a'; i <= 'f'; i++) {
            codes1.add("&" + i);
            codes2.add("§" + i);
        }
        for (char i = 'l'; i <= 'o'; i++) {
            codes1.add("&" + i);
            codes2.add("§" + i);
        }
        for (int i = 0; i <= 18; i++) {
            str = str.replace(codes1.get(i), codes2.get(i));
        }
        return str;
    }
}
