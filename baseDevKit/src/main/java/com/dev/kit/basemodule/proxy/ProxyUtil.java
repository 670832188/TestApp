package com.dev.kit.basemodule.proxy;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by cuiyan on 2020/4/28.
 */
public class ProxyUtil {

    public static Object createProxy(Object target, MethodHandler delegate) {
        delegate.setTarget(target);
        Class<?> clazz = target.getClass();
        List<Class<?>> interfaces = getAllInterfaces(clazz);
        int size = interfaces == null ? 0 : interfaces.size();
        Class[] ifs;
        if (size == 0) {
            ifs = new Class[0];
        } else {
            ifs = interfaces.toArray(new Class[size]);
        }
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), ifs, delegate);
    }

    private static List<Class<?>> getAllInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }
        final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
        getAllInterfaces(cls, interfacesFound);
        return new ArrayList<>(interfacesFound);
    }

    private static void getAllInterfaces(Class<?> cls, final HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            final Class<?>[] interfaces = cls.getInterfaces();
            for (final Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }
            cls = cls.getSuperclass();
        }
    }
}
