package com.dev.kit.basemodule.surpport;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import com.dev.kit.basemodule.util.ToastUtil;

import java.util.List;

/**
 * BaseRecyclerAdapter
 * Created by cuiyan on 16-10-20.
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<T> dataList;
    protected Context context;
    private int itemViewLayoutId;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public BaseRecyclerAdapter(Context context, List<T> dataList, int itemViewLayoutId) {
        this.context = context;
        this.dataList = dataList;
        this.itemViewLayoutId = itemViewLayoutId;
    }

    @NonNull
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return RecyclerViewHolder.getViewHolder(context, parent, itemViewLayoutId);
    }

    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        fillData(holder, position);
        if (onItemClickListener != null) {
            holder.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            });
        }
        if (onItemLongClickListener != null) {
            holder.getItemView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.OnItemLongClick(v, holder.getAdapterPosition());
                    return true;
                }
            });
        }
    }

    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public T getItem(int position) {
        return dataList.get(position);
    }

    public abstract void fillData(RecyclerViewHolder holder, int position);

    public void appendData(List<T> expendedData) {
        if (dataList != null && expendedData != null && !expendedData.isEmpty()) {
            dataList.addAll(expendedData);
            notifyDataSetChanged();
        }
    }

    public void appendItem(T item, boolean updateSingleItem) {
        if (dataList != null) {
            dataList.add(item);
            if (updateSingleItem) {
                notifyItemInserted(dataList.size() - 1);
            } else {
                notifyDataSetChanged();
            }
        }
    }

    public void updateDataList(List<T> dataList) {
        if (dataList != null) {
            this.dataList.clear();
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    public void removeItem(T item, boolean updateSingleItem) {
        int itemPos = -1;
        if (updateSingleItem) {
            itemPos = dataList.indexOf(item);
        }
        if (dataList.remove(item)) {
            if (itemPos > 0) {
                notifyItemRemoved(itemPos);
            } else {
                notifyDataSetChanged();
            }
        }
    }

    public void clearData() {
        if (dataList != null && !dataList.isEmpty()) {
            dataList.clear();
            notifyDataSetChanged();
        }
    }

    public void replaceData(int index, T data) {
        if (dataList != null && dataList.size() >= index + 1 && data != null) {
            dataList.remove(index);
            dataList.add(index, data);
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.onItemClickListener = clickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.onItemLongClickListener = longClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public interface OnItemLongClickListener {
        void OnItemLongClick(View v, int position);
    }

    public List<T> getDataList() {
        return dataList;
    }

    void showToast(final String msg) {
        ToastUtil.showToast(context, msg);
    }

    void showToast(final int msgResourceId) {
        ToastUtil.showToast(context, msgResourceId);
    }
}
