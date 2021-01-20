package com.dev.kit.testapp.trunpage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;

import com.dev.kit.basemodule.turnpage.BlackSquareFadeAway;
import com.dev.kit.basemodule.turnpage.BlackSquareZoomIn;
import com.dev.kit.basemodule.turnpage.ShutterDown2Up;
import com.dev.kit.basemodule.turnpage.ShutterLeft2Right;
import com.dev.kit.basemodule.turnpage.ShutterRight2Left;
import com.dev.kit.basemodule.turnpage.ShutterUp2Down;
import com.dev.kit.basemodule.turnpage.TranslateLeft;
import com.dev.kit.basemodule.turnpage.TranslateRight;
import com.dev.kit.basemodule.turnpage.util.BitmapUtil;
import com.dev.kit.basemodule.turnpage.widget.IFillingEvent;
import com.dev.kit.basemodule.turnpage.widget.TurnPageView;
import com.dev.kit.testapp.R;


public class TuruPageActivity extends Activity {

    private TurnPageView mTurnPageView = null;
    private Bitmap[] mBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mTurnPageView = new TurnPageView(getApplicationContext());
        int length = 2;
        mBitmaps = new Bitmap[length];
        for (int i = 0; i < length; i++) {
            mBitmaps[i] = BitmapUtil.getFitBitmapFromResource(getResources(), R.drawable.img_1 + i,
                    SysUtil.getScreenWidth(getApplicationContext()), SysUtil.getScreenHeight(getApplicationContext()) - SysUtil.getStatusBarHeight(getApplicationContext()));
        }
        setContentView(mTurnPageView);
    }

    @Override
    protected void onResume() {

        int index = getIntent().getIntExtra("index", 1);
        if (index == 1) {
            trunPage1();
        } else if (index == 2) {
            trunPage2();
        } else if (index == 3) {
            trunPage3();
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        for (int i = 0; i < mBitmaps.length; i++) {
            if (null != mBitmaps[i] && !mBitmaps[i].isRecycled()) {
                mBitmaps[i].recycle();
            }
        }
        super.onDestroy();
    }


    int curBitmapIndex = 0;

    private void prePage() {
        if (curBitmapIndex > 0) {
            curBitmapIndex--;
        } else {
            curBitmapIndex = 0;
        }
        mTurnPageView.setBitmaps(new Bitmap[]{mBitmaps[curBitmapIndex]});
    }

    private void nextPage() {
        if (curBitmapIndex < mBitmaps.length - 1) {
            curBitmapIndex++;
        }
        mTurnPageView.setBitmaps(new Bitmap[]{mBitmaps[curBitmapIndex]});
    }

    private void trunPage1() {
        IFillingEvent mFillingListener = new IFillingEvent() {

            @Override
            public void onFlingLeft() {
                nextPage();
                mTurnPageView.setTurnPageStyle(new ShutterRight2Left());

            }

            @Override
            public void onFlingRight() {
                prePage();
                mTurnPageView.setTurnPageStyle(new ShutterLeft2Right());

            }

            @Override
            public void onFlingUp() {
                nextPage();
                mTurnPageView.setTurnPageStyle(new ShutterDown2Up());

            }

            @Override
            public void onFlingDown() {
                prePage();
                mTurnPageView.setTurnPageStyle(new ShutterUp2Down());
            }

        };

        mTurnPageView.setOnFillingListener(mFillingListener);
        mTurnPageView.setTurnPageStyle(new ShutterLeft2Right());
        mTurnPageView.setBitmaps(new Bitmap[]{mBitmaps[curBitmapIndex]});
    }

    private void trunPage2() {
        IFillingEvent mFillingListener = new IFillingEvent() {

            @Override
            public void onFlingLeft() {
                nextPage();
                mTurnPageView.setTurnPageStyle(new BlackSquareZoomIn());

            }

            @Override
            public void onFlingRight() {
                prePage();
                mTurnPageView.setTurnPageStyle(new BlackSquareFadeAway());

            }

            @Override
            public void onFlingUp() {
                nextPage();
                mTurnPageView.setTurnPageStyle(new BlackSquareZoomIn());

            }

            @Override
            public void onFlingDown() {
                prePage();
                mTurnPageView.setTurnPageStyle(new BlackSquareFadeAway());
            }

        };

        mTurnPageView.setOnFillingListener(mFillingListener);
        mTurnPageView.setTurnPageStyle(new BlackSquareZoomIn());
        mTurnPageView.setBitmaps(new Bitmap[]{mBitmaps[curBitmapIndex]});
    }

    private void trunPage3() {
        IFillingEvent mFillingListener = new IFillingEvent() {

            @Override
            public void onFlingLeft() {
                nextPage3();
                mTurnPageView.setTurnPageStyle(new TranslateLeft());

            }

            @Override
            public void onFlingRight() {
                prePage3();
                mTurnPageView.setTurnPageStyle(new TranslateRight());

            }

            @Override
            public void onFlingUp() {
                nextPage3();
                mTurnPageView.setTurnPageStyle(new TranslateRight());

            }

            @Override
            public void onFlingDown() {
                prePage3();
                mTurnPageView.setTurnPageStyle(new TranslateLeft());
            }

        };

        mTurnPageView.setOnFillingListener(mFillingListener);
        mTurnPageView.setTurnPageStyle(new TranslateLeft());
        mTurnPageView.setBitmaps(new Bitmap[]{mBitmaps[curBitmapIndex], mBitmaps[curBitmapIndex + 1]});
    }

    private void prePage3() {
        if (curBitmapIndex > 0) {
            curBitmapIndex--;
        } else {
            curBitmapIndex = 0;
        }
        mTurnPageView.setBitmaps(new Bitmap[]{mBitmaps[curBitmapIndex], mBitmaps[curBitmapIndex + 1]});
    }

    private void nextPage3() {
        if (curBitmapIndex < mBitmaps.length - 1) {
            curBitmapIndex++;
        }
        mTurnPageView.setBitmaps(new Bitmap[]{mBitmaps[curBitmapIndex - 1], mBitmaps[curBitmapIndex]});
    }

}
