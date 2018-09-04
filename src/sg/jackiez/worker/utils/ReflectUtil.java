package sg.jackiez.worker.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 反射工具类
 * <p/>
 * Created by zsigui on 15-8-18.
 */
@SuppressWarnings("unchecked")
public final class ReflectUtil {

    private static final String TAG = "ReflectUtil";

    /**
     * 使用反射方式调用方法
     *
     * @param methodName 方法名
     * @param paramsType 方法参数类型,无则null
     * @param instance   获取方法的对象实例
     * @param params     方法参数，无则null
     * @param <T>        指定执行方法后返回类型
     */
    public static <T> T invokeMethod(String methodName, Class<?>[] paramsType, Object instance, Object[] params) {
        T result = null;
        try {
            Method method = instance.getClass().getDeclaredMethod(methodName, paramsType);
            result = (T) method.invoke(instance, params);
        } catch (Exception e) {
            SLogUtil.e(TAG, e);
        }
        return result;
    }

    /**
     * 使用反射方式调用构造函数创建对象实例
     *
     * @param className  类名(需要包括包前缀)
     * @param paramsType 构造函数参数类型，无则null
     * @param params     构造函数参数，无则null
     * @param <T>        指定返回对象类型
     */
    public static <T> T newInstance(String className, Class<?>[] paramsType, Object[] params) {
        T result = null;
        try {
            Class<?> c = Class.forName(className);
            Constructor<?> constructor = c.getConstructor(paramsType);
            constructor.setAccessible(true);
            result = (T) constructor.newInstance(params);
        } catch (Exception e) {
            SLogUtil.e(TAG, e);
        }
        return result;
    }

    /**
     * 通过反射方式获取属性的值
     *
     * @param fieldName 属性名
     * @param instance  实例对象
     * @param <T>       获取的属性的类型
     */
    public static <T> T getField(String fieldName, Object instance) {
        T result = null;
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            result = (T) field.get(instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            SLogUtil.e(TAG, e);
        }
        return result;
    }

    /**
     * 通过反射方式设置属性的值
     *
     * @param fieldName 属性名
     * @param instance  实例对象
     * @param val       属性值
     */
    public static void setField(String fieldName, Object instance, Object val) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, val);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            SLogUtil.e(TAG, e);
        }
    }

    public static void setFieldByType(Field f, Object instance, String val)
            throws IllegalAccessException {
        f.setAccessible(true);
        Type type = f.getType();
        if (type == int.class || type == Integer.class) {
            f.setInt(instance, Integer.parseInt(val));
        } else if (type == double.class || type == Double.class) {
            f.setDouble(instance, Double.parseDouble(val));
        } else if (type == float.class || type == Float.class) {
            f.setFloat(instance, Float.parseFloat(val));
        } else if (type == boolean.class || type == Boolean.class) {
            f.setBoolean(instance, Boolean.parseBoolean(val));
        } else if (type == char.class) {
            f.setChar(instance, val.charAt(0));
        } else if (type == long.class || type == Long.class) {
            f.setLong(instance, Long.parseLong(val));
        } else if (type == byte.class || type == Byte.class) {
            f.setLong(instance, Byte.parseByte(val));
        } else if (type == short.class || type == Short.class) {
            f.setLong(instance, Short.parseShort(val));
        } else {
            f.set(instance, val);
        }
    }
}