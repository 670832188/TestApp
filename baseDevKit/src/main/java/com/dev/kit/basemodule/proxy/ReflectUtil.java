package com.dev.kit.basemodule.proxy;

import com.dev.kit.basemodule.util.LogUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by cuiyan on 2020/4/28.
 */
public class ReflectUtil {
    private static final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    private static final ClassLoader bootClassloader = systemClassLoader.getParent();
    private static final ClassLoader appClassLoader = ReflectUtil.class.getClassLoader();
    private static HashMap<String, Class> clazzCache = new HashMap<>();

    public static Class forName(String clazzName) throws ClassNotFoundException {
        Class clazz = clazzCache.get(clazzName);
        if (clazz == null) {
            clazz = Class.forName(clazzName);
            ClassLoader cl = clazz.getClassLoader();
            if (cl == systemClassLoader || cl == appClassLoader || cl == bootClassloader) {
                clazzCache.put(clazzName, clazz);
            }
        }
        return clazz;
    }

    public static Object newInstance(String className, Class[] paramTypes, Object[] paramValues) {
        try {
            Class<?> clazz = forName(className);
            Constructor constructor = clazz.getConstructor(paramTypes);
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance(paramValues);
        } catch (Exception e) {
            LogUtil.e(getClassSimpleName(e), e);
        }
        return null;
    }

    public static Object invokeMethod(Object target, String className, String methodName, Class[] paramTypes, Object[] paramValues) {
        try {
            Class clazz = forName(className);
            return invokeMethod(target, clazz, methodName, paramTypes, paramValues);
        } catch (ClassNotFoundException e) {
            LogUtil.e("ClassNotFoundException", e);
        }
        return null;
    }

    public static Object invokeMethod(Object target, Class<?> clazz, String methodName, Class[] paramTypes, Object[] paramValues) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, paramTypes);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method.invoke(target, paramValues);
        } catch (Exception e) {
            LogUtil.e(getClassSimpleName(e), e);
        }
        return null;
    }

    public static Object getField(Object target, String className, String fieldName) {
        try {
            Class clazz = forName(className);
            return getField(target, clazz, fieldName);
        } catch (ClassNotFoundException e) {
            LogUtil.e("ClassNotFoundException", e);
        }
        return null;
    }

    public static Object getField(Object target, Class clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(target);
        } catch (Exception e) {
            LogUtil.e(getClassSimpleName(e), e);
        }
        return null;

    }

    public static void setField(Object target, String className, String fieldName, Object fieldValue) {
        try {
            Class clazz = forName(className);
            setField(target, clazz, fieldName, fieldValue);
        } catch (ClassNotFoundException e) {
            LogUtil.e("ClassNotFoundException", e);
        }
    }

    public static void setField(Object target, Class clazz, String fieldName, Object fieldValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(target, fieldValue);
        } catch (Exception e) {
            LogUtil.e(getClassSimpleName(e), e);
        }
    }

    private static String getClassSimpleName(Object o) {
        return o.getClass().getSimpleName();
    }
}
