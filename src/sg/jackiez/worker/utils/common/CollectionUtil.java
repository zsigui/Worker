package sg.jackiez.worker.utils.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CollectionUtil {

    public static HashMap<String, String> getExtraMap(String... keyVal) {
        if (keyVal == null || keyVal.length == 0) {
            return null;
        }
        int size = keyVal.length;
        HashMap<String, String> extra = new HashMap<>(keyVal.length / 2);
        for (int i = 0; i < size; i += 2) {
            extra.put(keyVal[i], keyVal[i + 1]);
        }
        return extra;
    }

    public static Map<String,String> singletonMap(String key, String val) {
        return Collections.singletonMap(key, val);
    }
}
