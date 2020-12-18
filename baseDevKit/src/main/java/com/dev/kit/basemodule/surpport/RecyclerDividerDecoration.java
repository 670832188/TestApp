package com.dev.kit.basemodule.surpport;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.dev.kit.basemodule.util.DisplayUtil;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by cuiyan on 2018/5/15.
 */
public class RecyclerDividerDecoration extends RecyclerView.ItemDecoration {
    private int thickness;
    private Drawable divider;
    private int layoutType;
    public static final int LAYOUT_TYPE_HORIZONTAL = 0;
    public static final int LAYOUT_TYPE_VERTICAL = 1;
    public static final int LAYOUT_TYPE_GRID = 2;

    public RecyclerDividerDecoration(int layoutType) {
        this(layoutType, Integer.MIN_VALUE, DisplayUtil.dp2px(5));
    }

    public RecyclerDividerDecoration(int layoutType, int dividerColor, int thickness) {
        if (dividerColor != Integer.MIN_VALUE) {
            divider = new ColorDrawable(dividerColor);
        }
        this.thickness = thickness;
        setDividerType(layoutType);
    }

    private void setDividerType(int layoutType) {
        if (layoutType != LAYOUT_TYPE_HORIZONTAL && layoutType != LAYOUT_TYPE_VERTICAL && layoutType != LAYOUT_TYPE_GRID) {
            throw new IllegalArgumentException("invalid layout type");
        }
        this.layoutType = layoutType;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (divider == null) {
            return;
        }
        switch (layoutType) {
            case LAYOUT_TYPE_HORIZONTAL: {
                drawVerticalDivider(c, parent);
                break;
            }
            case LAYOUT_TYPE_VERTICAL: {
                drawHorizontalDivider(c, parent);
                break;
            }
            case LAYOUT_TYPE_GRID: {
                drawGridDivider(c, parent);
                break;
            }
        }
    }

    //画横线, 这里的parent其实是显示在屏幕显示的这部分
    private void drawHorizontalDivider(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getLeft();
            int right = child.getWidth() + left;
            int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + thickness;
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    //画竖线
    private void drawVerticalDivider(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getTop();
            int bottom = child.getHeight() + top;
            int left = child.getRight() + params.rightMargin;
            int right = left + thickness;
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);

        }
    }

    private void drawGridDivider(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            int left = child.getLeft();
            int right = left + child.getWidth() + thickness;
            int top = child.getBottom();
            int bottom = top + thickness;
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);

            left = child.getRight();
            right = left + thickness;
            top = child.getTop();
            bottom = child.getHeight() + top + thickness;
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        int childCount = parent.getAdapter().getItemCount();
        int position = manager.getPosition(view);
        switch (layoutType) {
            case LAYOUT_TYPE_HORIZONTAL: {
                if (position != childCount - 1) {
                    outRect.right = thickness;
                }
                break;
            }
            case LAYOUT_TYPE_VERTICAL: {
                if (position != childCount - 1) {
                    outRect.bottom = thickness;
                }
                break;
            }
            case LAYOUT_TYPE_GRID: {
                childCount = parent.getAdapter().getItemCount();
                int spanCount = ((GridLayoutManager) manager).getSpanCount();
                int column = position % spanCount;
                outRect.left = column * thickness / spanCount;
                outRect.right = thickness - (column + 1) * thickness / spanCount;
                int totalRow = childCount / spanCount + (childCount % spanCount > 0 ? 1 : 0);
                int row = position / spanCount + 1;
                if (row != totalRow) {
                    outRect.bottom = thickness;
                }
                break;
            }
        }
    }
}
