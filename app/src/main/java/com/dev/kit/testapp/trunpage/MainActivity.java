package com.dev.kit.testapp.trunpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.dev.kit.basemodule.turnpage1.ImgData;
import com.dev.kit.basemodule.turnpage1.TurnPageView;
import com.dev.kit.testapp.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mainui);

        findViewById(R.id.btn1).setOnClickListener(listener);
        findViewById(R.id.btn2).setOnClickListener(listener);
        findViewById(R.id.btn3).setOnClickListener(listener);
        TurnPageView pageView = findViewById(R.id.turnPageView);
        List<ImgData> dataList = new ArrayList<>();
        dataList.add(new ImgData("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2520610827,591080718&fm=26&gp=0.jpg", R.mipmap.iv_ts1));
        dataList.add(new ImgData("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1657471424,2319316184&fm=26&gp=0.jpg", R.mipmap.iv_ts2));
        dataList.add(new ImgData("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.ewebweb.com%2Fuploads%2F20190403%2F15%2F1554278353-YgNuaCEFAS.jpg&refer=http%3A%2F%2Fimg.ewebweb.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1613726165&t=2b897bafbae89140d3b5c17db9c1a3e4", R.mipmap.iv_ts3));
        pageView.setDataList(dataList);
    }

    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(getBaseContext(), TuruPageActivity.class);
            int index = 1;
            switch (v.getId()) {
                case R.id.btn1:
                    index = 1;
                    break;

                case R.id.btn2:
                    index = 2;
                    break;

                case R.id.btn3:
                    index = 3;
                    break;
            }
            mIntent.putExtra("index", index);
            startActivity(mIntent);
        }
    };
}
