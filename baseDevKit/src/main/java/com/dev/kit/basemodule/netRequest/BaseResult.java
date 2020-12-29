package com.dev.kit.basemodule.netRequest;

/**
 * Created by cuiyan on 16/6/3 11:36
 * Email: cuiyan@rqb.com
 */
public class BaseResult<T> {
    private static final int START_CODE = 0x100000;
    private static final int ERROR_CODE = 0x100001;
    private static final int CANCEL_CODE = 0x100002;


    private int code;
    /**
     * 返回结果提示
     */
    private String msg;
    /**
     * 返回结果数据
     */
    private T data;

    private Throwable throwable;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public boolean isRequestError() {
        return code == ERROR_CODE;
    }

    public boolean isRequestCancel() {
        return code == CANCEL_CODE;
    }

    public static <T> BaseResult<T> generateErrorResult(Throwable throwable) {
        BaseResult<T> result = new BaseResult<>();
        result.code = ERROR_CODE;
        result.throwable = throwable;
        return result;
    }

    public static <T> BaseResult<T> generateCancelResult() {
        BaseResult<T> result = new BaseResult<>();
        result.code = CANCEL_CODE;
        return result;
    }

    public static <T> BaseResult<T> generateStartResult() {
        BaseResult<T> result = new BaseResult<>();
        result.code = START_CODE;
        return result;
    }
}
