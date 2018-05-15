package com.dev.kit.basemodule.surpport;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dev.kit.basemodule.util.DisplayUtil;


/**
 * Created by cuiyan on 2018/5/15.
 */
public class RecyclerDividerDecoration extends RecyclerView.ItemDecoration {
    private boolean useSystemDefaultDivider;
    private int thickness = DisplayUtil.dp2px(1);
    private Drawable divider;
    private int dividerType;
    // 水平分割线
    public static final int DIVIDER_TYPE_HORIZONTAL = 0;
    // 垂直分割线
    public static final int DIVIDER_TYPE_VERTICAL = 1;
    // Grid类型分割线
    public static final int DIVIDER_TYPE_GRID = 2;

    //使用系统属性中的listDivider来添加，在app的AppTheme中设置
    public static final int[] ATRRS = new int[]{
            android.R.attr.listDivider
    };

    public RecyclerDividerDecoration(Context context, int layoutType) {
        useSystemDefaultDivider = true;
        TypedArray ta = context.obtainStyledAttributes(ATRRS);
        divider = ta.getDrawable(0);
        ta.recycle();
        setLayoutType(layoutType);
    }

    public RecyclerDividerDecoration(int layoutType, int dividerColor, int thickness) {
        divider = new ColorDrawable(dividerColor);
        this.thickness = thickness;
        setLayoutType(layoutType);
    }

    private void setLayoutType(int dividerType) {
        switch (dividerType) {
            case DIVIDER_TYPE_HORIZONTAL: {
                break;
            }
            case DIVIDER_TYPE_VERTICAL: {
                break;
            }
            case DIVIDER_TYPE_GRID: {
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid orientation");
            }
        }
        this.dividerType = dividerType;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        switch (dividerType) {
            case DIVIDER_TYPE_HORIZONTAL: {
                drawHorizontalLine(c, parent, state);
                break;
            }
            case DIVIDER_TYPE_VERTICAL: {
                drawVerticalLine(c, parent, state);
                break;
            }
            case DIVIDER_TYPE_GRID: {
                break;
            }
        }
    }

    //画横线, 这里的parent其实是显示在屏幕显示的这部分
    public void drawHorizontalLine(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            //获得child的布局信息
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int thickness = useSystemDefaultDivider ? divider.getIntrinsicHeight() : this.thickness;
            final int bottom = top + thickness;
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
            //Log.d("wnw", left + " " + top + " "+right+"   "+bottom+" "+i);
        }
    }

    //画竖线
    public void drawVerticalLine(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            //获得child的布局信息
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            int thickness = useSystemDefaultDivider ? divider.getIntrinsicWidth() : this.thickness;
            final int right = left + thickness;
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    //由于Divider也有长宽高，每一个Item需要向下或者向右偏移
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        switch (dividerType) {
            case DIVIDER_TYPE_HORIZONTAL: {
                int thickness = useSystemDefaultDivider ? divider.getIntrinsicHeight() : this.thickness;
                outRect.set(0, 0, 0, thickness);
                break;
            }
            case DIVIDER_TYPE_VERTICAL: {
                int thickness = useSystemDefaultDivider ? divider.getIntrinsicWidth() : this.thickness;
                outRect.set(0, 0, thickness, 0);
                break;
            }
            case DIVIDER_TYPE_GRID: {
                break;
            }
        }
    }
}
