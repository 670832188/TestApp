package com.dev.kit.basemodule.videoconverter;

import android.os.AsyncTask;

/**
 * Created by xuyuming on 2018/4/25
 */
public class VideoCompressTask extends AsyncTask<Void, Float, Boolean> {

    private String srcPath;
    private String destPath;
    private IVideoCompress listener;
    private VideoConverter converter;

    public VideoCompressTask(String srcPath, String destPath, IVideoCompress listener) {
        this.srcPath = srcPath;
        this.destPath = destPath;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (isCancelled()) {
            return;
        }
        listener.onPrePared();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (isCancelled()) {
            return false;
        }
        converter = new VideoConverter();
        boolean result;
        try {
            result = converter.extractDecodeEditEncodeMux(srcPath, destPath, progressListener);
            if (result) {
                publishProgress(100f);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (isCancelled()) {
            return;
        }
        if (listener != null) {
            if (result) {
                listener.onSuccess(srcPath, destPath);
            } else {
                listener.onFail();
            }
        }
    }

    VideoConverter.CompressProgressListener progressListener = new VideoConverter.CompressProgressListener() {
        @Override
        public void onProgress(float percent) {
            publishProgress(percent);
        }
    };

    @Override
    protected void onProgressUpdate(Float... percent) {
        super.onProgressUpdate(percent);
        if (isCancelled()) {
            return;
        }
        if (listener != null) {
            listener.onProgress(percent[0].intValue());
        }
    }

    public void cancelTask() {
        cancel(true);
        if (converter != null) {
            converter.cancel();
        }
    }
}
