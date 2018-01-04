package com.simon.utils.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simon.utils.R;
import com.simon.utils.widget.recycler.SwipeMenuAdapter;
import com.simon.utils.widget.recycler.interfaces.OnItemClickListener;

import java.util.List;

/**
 * gridview
 * Created by Administrator on 2018/1/4.
 */
@SuppressWarnings("all")
public class GridViewAdapter extends SwipeMenuAdapter<GridViewAdapter.DefaultViewHolder> {

    private List<String> mList;
    private OnItemClickListener mOnItemClickListener;

    public GridViewAdapter(List<String> list) {
        this.mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.gridview_item, parent, false);
    }

    @Override
    public DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        holder.mTv_item_msg.setText(mList.get(position) + "");
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTv_item_msg;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTv_item_msg = (TextView) itemView.findViewById(R.id.tv_item_msg);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
