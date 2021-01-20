package com.dev.kit.testapp.trunpage;

import java.lang.reflect.Field;

import android.content.Context;
import android.view.WindowManager;

public class SysUtil {
	
	static int screenWidth;  
	static int screenHeight;  
	static int statusBarHeight;
    
    public static int getScreenWidth(Context context)
    {
		if(screenWidth==0)
		{
			WindowManager mWindowManager=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			screenWidth =mWindowManager.getDefaultDisplay().getWidth();
			screenHeight=mWindowManager.getDefaultDisplay().getHeight();
		}
    	return screenWidth;
    }
    
    public static int getScreenHeight(Context context)
    {
    	if(screenHeight==0)
    	{
    		WindowManager mWindowManager=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    		screenWidth =mWindowManager.getDefaultDisplay().getWidth();
    		screenHeight=mWindowManager.getDefaultDisplay().getHeight();
    	}
    	return screenHeight;
    }
    
	public static int getStatusBarHeight(Context context) 
    {
        if (statusBarHeight == 0) 
        {  
            try {  
                Class<?> c = Class.forName("com.android.internal.R$dimen");  
                Object o = c.newInstance();  
                Field field = c.getField("status_bar_height");  
                int x = (Integer) field.get(o);  
                statusBarHeight = context.getResources().getDimensionPixelSize(x);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return statusBarHeight;  
    } 

}
