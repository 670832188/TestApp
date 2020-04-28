package com.dev.kit.basemodule.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by cuiyan on 2020/4/28.
 */
public abstract class MethodHandler implements InvocationHandler {
    private Object target;

    public MethodHandler() {
    }

    public MethodHandler(Object target) {
        this.target = target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return delegateInvoke(target, method, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract Object delegateInvoke(Object target, Method method, Object[] args) throws Exception;
}
