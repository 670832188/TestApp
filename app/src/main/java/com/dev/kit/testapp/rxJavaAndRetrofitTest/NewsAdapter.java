package com.dev.kit.testapp.rxJavaAndRetrofitTest;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.kit.basemodule.surpport.BaseRecyclerAdapter;
import com.dev.kit.basemodule.surpport.RecyclerViewHolder;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.basemodule.util.ImageUtil;
import com.dev.kit.testapp.R;

import java.util.List;

/**
 * Created by cy on 2018/4/3.
 */

public class NewsAdapter extends BaseRecyclerAdapter<NewsResult.NewsItemInfo> {
    private int lastAnimatedPosition = -1;

    public NewsAdapter(Context context, List<NewsResult.NewsItemInfo> dataList) {
        super(context, dataList, R.layout.item_news);
    }

    @Override
    public void fillData(RecyclerViewHolder holder, int position) {
        Log.e("mytag", "pos: " +position);
        runEnterAnimation(holder.getItemView(), position);
        NewsResult.NewsItemInfo itemInfo = getItem(position);
        ImageView ivTitle = holder.getView(R.id.iv_title);
        TextView tvTitle = holder.getView(R.id.tv_title);
        TextView tvTime = holder.getView(R.id.tv_time);
        TextView tvAuthor = holder.getView(R.id.tv_author);
        ImageUtil.loadImage(context, itemInfo.getThumbnailPic(), ImageUtil.SCALE_TYPE.CENTER_CROP, R.mipmap.ic_default_news, R.mipmap.ic_default_news, ivTitle, 5);
        tvTitle.setText(itemInfo.getTitle());
        tvTime.setText(itemInfo.getDate());
        tvAuthor.setText(itemInfo.getAuthorName());
    }

    private void runEnterAnimation(View view, int position) {
        view.measure(1, 1);
        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            int translationY = view.getMeasuredHeight() * 2;
            if (translationY > DisplayUtil.getScreenHeight() / 2) {
                translationY = DisplayUtil.getScreenHeight() / 2;
            }

            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", translationY, 0);
            animator.setDuration(500);
            animator.start();
        }
    }
}
