package com.dev.kit.basemodule.multiChildHistogram;

import java.util.List;

/**
 * Created by cuiyan on 2018/5/3.
 */
public class MultiGroupHistogramGroupData {

    private String tableName;
    private List<ChildData> childDataList;

    public String getTableName() {
        return tableName;
    }

    public List<ChildData> getChildDataList() {
        return childDataList;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setChildDataList(List<ChildData> childDataList) {
        this.childDataList = childDataList;
    }


    public class ChildData {
        private float value;
        private String suffix;

        public float getValue() {
            return value;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }
    }
}
