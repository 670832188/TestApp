package com.dev.kit.basemodule.result;

/**
 * Created by cuiyan on 16/6/3 11:36
 * Email: cuiyan@rqb.com
 */
public class BaseResult<T> {
    /**
     * 返回状态值
     * 200表示成功
     */
    private String code;
    /**
     * 返回结果提示
     */
    private String msg;
    /**
     * 返回结果数据
     */
    private T data;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
