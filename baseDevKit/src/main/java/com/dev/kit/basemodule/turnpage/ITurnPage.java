package com.dev.kit.basemodule.turnpage;

import android.graphics.Bitmap;
import android.view.SurfaceHolder;

/**
 * 翻页效果的接口
 * @author yanglonghui
 *
 */
public interface ITurnPage {

	public abstract void onCreate();
	
	public abstract void onTurnPageDraw(SurfaceHolder holder,Bitmap[] bitmap,int maxWidth,int maxHeight);
	
	public abstract void onDestory();
}
