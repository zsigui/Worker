package sg.jackiez.worker.utils.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
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

    public static int binarySearch(int[] array, int size, int value) {
        int lo = 0;
        int hi = size - 1;

        while (lo <= hi) {
            final int mid = (lo + hi) >>> 1;
            final int midVal = array[mid];

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }

    public static int binarySearch(long[] array, int size, long value) {
        int lo = 0;
        int hi = size - 1;

        while (lo <= hi) {
            final int mid = (lo + hi) >>> 1;
            final long midVal = array[mid];

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }

    /**
     * 限制传入的列表实例不超过指定大小,如果超过的话从指定方向移除多余项
     * @param listObj 列表对象
     * @param limitSize 限制大小
     * @param fromHead 是否从头部(下标0)开始移除
     */
    public static <T> List<T> limit(List<T> listObj, int limitSize, boolean fromHead) {
        if (listObj == null) {
            return null;
        }

        int size = listObj.size();
        if (fromHead) {
            ListIterator<T> it = listObj.listIterator();
            while (it.hasNext() && size-- > limitSize) {
                it.next();
                it.remove();
            }
        } else {
            while (size-- > limitSize) {
                listObj.remove(size);
            }
        }
        return listObj;
    }
}
