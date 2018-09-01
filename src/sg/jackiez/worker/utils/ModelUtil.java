package sg.jackiez.worker.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ModelUtil {

    private static final String TAG = "ModelUtil";

    public <T> List<T> readModelFromCustomFile(File file, Class<T> c) {
        ArrayList<String[]> data = FileUtil.readCustomFile(file);
        if (data == null || data.size() == 1 || data.get(0) == null) {
            // 读取不到数据或者无标题
            return null;
        }
        try {
            List<T> result = new ArrayList<>(data.size() - 1);

            String[] titles = data.get(0);
            HashMap<String ,Integer> titleMap = new HashMap<>(titles.length);
            for (int i = titles.length - 1; i > -1; i--) {
                titleMap.put(titles[i], i);
            }
            int size = data.size();
            for (int i = 1; i < size; i++) {
                String[] src = data.get(i);
                T t = c.newInstance();
                Field [] fields = c.getDeclaredFields();
                if (fields.length > 0) {
                    for (Field f : fields) {
                    }
                }
            }
            return result;
        } catch (Exception e) {
            SLogUtil.e(TAG, e);
        }
        return null;
    }
}
