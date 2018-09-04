package sg.jackiez.worker.utils;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sg.jackiez.worker.utils.common.CommonUtil;

public final class ModelUtil {

    private static final String TAG = "ModelUtil";

    /**
     * 读取文件并将数据转换为指定的Class类型的列表
     */
    public static <T> List<T> readModelFromCustomFile(File file, Class<T> c) {
        ArrayList<String[]> data = readCustomCSVFile(file);
        if (data == null || data.size() == 1 || data.get(0) == null) {
            // 读取不到数据或者无标题
            return null;
        }
        String[] titles = data.get(0);
        HashMap<String ,Integer> titleMap = new HashMap<>(titles.length);
        for (int i = titles.length - 1; i > -1; i--) {
            titleMap.put(titles[i], i);
        }
        return convertStringArrayToItems(data, 1, titleMap, c);
    }

    /**
     * 读取自定义格式文件，类CSV <br />
     * 每一行一条记录，每行记录用逗号(,)进行分割
     * #开头表示表示注释 <br />
     * *开头作为标题[title]，多行时第一行生效
     * @return 如果读取成功，返回形如[{"id", "name"}, {"1", "Jackie"}, {"2", "Jame"}]，第一行表示标题，无则为null
     * ，另外，无文件或者错误返回null
     */
    public static ArrayList<String[]> readCustomCSVFile(File file) {
        SLogUtil.d(TAG, "start to read path: " + (file == null ? "null" : file.getAbsolutePath()));
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            ArrayList<String[]> data = new ArrayList<>();
            data.add(null);
            while ((line = br.readLine()) != null) {
                if (CommonUtil.isEmpty(line) || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("*") && data.get(0) == null) {
                    // 标题
                    data.set(0, line.substring(1).split(","));
                } else {
                    data.add(line.split(","));
                }
            }
            return data;

        } catch (Exception e) {
            SLogUtil.e(TAG, e);
        } finally {
            IOUtil.closeIO(br);
        }
        return null;
    }

    public static <T> List<T> convertStringArrayToItems(List<String[]> data, int itemStartIndex,
                                                         HashMap<String, Integer> titleIndex, Class<T> clz) {
        try {
            int count = data.size();
            ArrayList<T> result = new ArrayList<>(count - 1);
            String[] item;
            for (int i = itemStartIndex; i < count; i++) {
                item = data.get(i);
                T obj = clz.newInstance();
                Field[] fields = obj.getClass().getDeclaredFields();
                for (Field f : fields) {
                    boolean isHandleByAnnotation = false;
                    Annotation[] annotations = f.getDeclaredAnnotations();
                    if (annotations.length > 0) {
                        for (Annotation an : annotations) {
                            if (an instanceof JsonIgnore) {
                                isHandleByAnnotation = true;
                                break;
                            } else if (an instanceof JsonProperty) {
                                String s = ((JsonProperty) an).value();
                                if (!CommonUtil.isEmpty(s) && titleIndex.containsKey(s)) {
                                    ReflectUtil.setFieldByType(f, obj, item[titleIndex.get(s)]);
                                }
                                isHandleByAnnotation = true;
                                break;
                            } else if (an instanceof JsonAlias) {
                                String[] ss = ((JsonAlias) an).value();
                                for (String s : ss) {
                                    if (!CommonUtil.isEmpty(s) && titleIndex.containsKey(s)) {
                                        ReflectUtil.setFieldByType(f, obj, item[titleIndex.get(s)]);
                                        isHandleByAnnotation = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (!isHandleByAnnotation && titleIndex.containsKey(f.getName())) {
                        ReflectUtil.setFieldByType(f,obj, item[titleIndex.get(f.getName())]);
                    }
                }
                result.add(obj);
            }
            return  result;
        } catch (Exception e) {
            SLogUtil.d(e);
        }
        return null;
    }
}
