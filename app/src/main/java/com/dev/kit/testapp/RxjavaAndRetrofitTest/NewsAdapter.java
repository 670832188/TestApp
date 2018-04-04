package com.dev.kit.testapp.RxjavaAndRetrofitTest;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.kit.basemodule.surpport.BaseRecyclerAdapter;
import com.dev.kit.basemodule.surpport.RecyclerViewHolder;
import com.dev.kit.basemodule.util.DisplayUtil;
import com.dev.kit.basemodule.util.ImageUtil;
import com.dev.kit.testapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by cy on 2018/4/3.
 */

public class NewsAdapter extends BaseRecyclerAdapter<NewsItemInfo> {
    private int lastAnimatedPosition = -1;

    public NewsAdapter(Context context, List<NewsItemInfo> dataList) {
        super(context, dataList, R.layout.item_news);
    }

    @Override
    public void fillData(RecyclerViewHolder holder, int position) {
        runEnterAnimation(holder.getItemView(), position);
        NewsItemInfo itemInfo = getItem(position);
        ImageView ivTitle = holder.getView(R.id.iv_title);
        TextView tvTitle = holder.getView(R.id.tv_title);
        TextView tvTime = holder.getView(R.id.tv_time);
        TextView tvViewCount = holder.getView(R.id.tv_view_count);
        if (itemInfo.getImageUrls() != null && itemInfo.getImageUrls().size() > 0) {
            ImageUtil.showImg(context, itemInfo.getImageUrls().get(0), R.mipmap.ic_default_news, R.mipmap.ic_default_news, ivTitle, 1f);
        } else {
            ivTitle.setImageResource(R.mipmap.ic_default_news);
        }
        tvTitle.setText(itemInfo.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        try {
            tvTime.setText(sdf.format(new Date(itemInfo.getPublishDate() * 1000)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder("阅读" + itemInfo.getViewCount());
        if (!TextUtils.isEmpty(itemInfo.getCommentCount())) {
            sb.append("·").append("评论").append(itemInfo.getCommentCount());
        }
        tvViewCount.setText(sb.toString());
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
