package com.dev.kit.basemodule;

import android.util.Log;

import androidx.lifecycle.ViewModel;

/**
 * @author cuiyan
 * Created on 2020/12/22.
 */
public class MainModel extends ViewModel {
    public String data = "asd";

    public void changeData() {
        data = data + "_1";
        Log.e("mytag", "data: " + data);
    }

}
