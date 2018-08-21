package com.dev.kit.basemodule.videoconverter;

/**
 * Created by xuyuming on 2018/4/25
 */
public interface IVideoCompress {

    void onPrePared();

    void onSuccess(String sourcePath, String newPath);

    void onFail();

    void onProgress(int percent);
}
