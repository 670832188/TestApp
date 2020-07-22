package com.dev.kit.basemodule.fileUpload;

import com.dev.kit.basemodule.netRequest.Configs.ApiConstants;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by cuiyan on 2018/8/6.
 */
public interface FileUploadApiService {
    @Multipart
    @POST(ApiConstants.QQ_SPORT_API)
    Observable<String> uploadFile(@Part List<MultipartBody.Part> filePartList);
}
