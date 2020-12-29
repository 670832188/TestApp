package com.dev.kit.testapp.rxJavaAndRetrofitTest;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cuiyan on 16-10-18.
 */
public class NewsResult implements Serializable {
    @SerializedName("error_code")
    private String errorCode;
    private String reason;
    private Result result;
    public String getErrorCode() {
        return errorCode;
    }

    public String getReason() {
        return reason;
    }

    public Result getResult() {
        return result;
    }

    public static class Result {
        private String stat;
        private List<NewsItemInfo> data;

        public List<NewsItemInfo> getData() {
            return data;
        }
    }

    public static class NewsItemInfo implements Serializable {

        @SerializedName("uniquekey")
        private String uniqueKey;
        private String title;
        private String date;
        private String category;
        @SerializedName("author_name")
        private String authorName;
        private String url;
        @SerializedName("thumbnail_pic_s")
        private String thumbnailPic;

        public String getUniqueKey() {
            return uniqueKey;
        }

        public String getTitle() {
            return title;
        }


        public String getDate() {
            return date;
        }

        public String getCategory() {
            return category;
        }

        public String getAuthorName() {
            return authorName;
        }

        public String getUrl() {
            return url;
        }

        public String getThumbnailPic() {
            return thumbnailPic;
        }
    }
}
