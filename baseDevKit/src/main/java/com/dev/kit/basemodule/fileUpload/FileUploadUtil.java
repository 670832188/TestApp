package com.dev.kit.basemodule.fileUpload;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dev.kit.basemodule.R;
import com.dev.kit.basemodule.netRequest.model.BaseController;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestCallback;
import com.dev.kit.basemodule.netRequest.subscribers.NetRequestSubscriber;
import com.dev.kit.basemodule.netRequest.util.BaseServiceUtil;
import com.dev.kit.basemodule.result.BaseResult;
import com.dev.kit.basemodule.util.FileUtil;
import com.dev.kit.basemodule.util.ImageUtil;
import com.dev.kit.basemodule.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by cuiyan on 2018/8/6.
 */
public class FileUploadUtil {
    public static void uploadImg(final Context context, List<String> imgPaths, final Map<String, String> strParams) {
        ImageUtil.compressImgByPaths(context, imgPaths, new ImageUtil.CompressImgListener() {
            @Override
            public void onSuccess(List<File> compressedImgFileList) {
                actualUploadFiles(context, compressedImgFileList, strParams);
            }

            @Override
            public void onFailed() {
                ToastUtil.showToast(context, R.string.img_compress_failed);
            }
        });
    }

    private static void actualUploadFiles(Context context, @NonNull List<File> fileList, Map<String, String> strParams) {
        List<MultipartBody.Part> filePartList = new ArrayList<>();
        if (strParams != null) {
            for (String key : strParams.keySet()) {
                MultipartBody.Part strParamPart = MultipartBody.Part.createFormData(key, strParams.get(key));
                filePartList.add(strParamPart);
            }
        }
        for (File file : fileList) {
            String fileType = FileUtil.getMimeType(file.getAbsolutePath());
            MediaType mediaType = MediaType.parse(fileType);
            RequestBody fileParamBody = RequestBody.create(mediaType, file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileParamBody);
            filePartList.add(filePart);
        }
        NetRequestSubscriber<BaseResult> subscriber = new NetRequestSubscriber<>(new NetRequestCallback<BaseResult>() {
            @Override
            public void onSuccess(@NonNull BaseResult baseResult) {
                super.onSuccess(baseResult);
            }

            @Override
            public void onResultNull() {
                super.onResultNull();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        }, context, true, null);
        Observable observable = BaseServiceUtil.createService(FileUploadApiService.class).uploadFile(filePartList);
        BaseController.sendRequest(subscriber, observable);
    }
}
