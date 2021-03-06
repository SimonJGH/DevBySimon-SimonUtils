package com.simon.utils.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.GridLayout;

import com.simon.utils.R;
import com.simon.utils.adapter.GridViewAdapter;
import com.simon.utils.widget.recycler.GridViewDecoration;
import com.simon.utils.widget.recycler.SDRecyclerView;
import com.simon.utils.widget.recycler.interfaces.OnItemClickListener;
import com.simon.utils.widget.recycler.touch.OnItemMoveListener;
import com.simon.utils.widget.recycler.touch.OnItemStateChangedListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("all")
@ContentView(R.layout.activity_grid_view)
public class GridViewActivity extends BaseActivity {

    @ViewInject(R.id.srl_gridview)
    SwipeRefreshLayout mSrl_gridview;

    @ViewInject(R.id.sdrv_gridview)
    SDRecyclerView mSdrv_gridview;

    private GridViewAdapter mAdapter;
    private List<String> mList = new ArrayList<>();

    /**
     * Item拖拽事件监听
     */
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            // 当Item被拖拽的时候。
            Collections.swap(mList, fromPosition, toPosition);
            mAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;// 返回true表示处理了，返回false表示你没有处理。
        }

        @Override
        public void onItemDismiss(int position) {
            // 当Item被滑动删除掉的时候，在这里是无效的，因为这里没有启用这个功能。
            // 使用Menu时就不用使用这个侧滑删除，两个是冲突的。
        }
    };

    /**
     * Item的拖拽/侧滑删除时，手指状态发生变化监听。
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = new OnItemStateChangedListener() {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, @ActionStateMode int actionState) {
            if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
                //"状态：拖拽";
                // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white_pressed));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
                //"状态：滑动删除";
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
                //"状态：手指松开";
                // 在手松开的时候还原背景。
                ViewCompat.setBackground(viewHolder.itemView, ContextCompat.getDrawable(mContext, R.drawable.select_white));
            }
        }
    };

    /**
     * 刷新监听
     */
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mSdrv_gridview.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initData();
                    mSrl_gridview.setRefreshing(false);
                }
            }, 2000);
        }
    };

    /**
     * 加载更多
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            // TODO 手指不能向上滑动了
            if (!recyclerView.canScrollVertically(1)) {
                // TODO 这里有个注意的地方，如果你刚进来时没有数据，但是设置了适配器，这个时候就会触发加载更多，需要开发者判断下是否有数据，如果有数据才去加载更多。
                mToastUtil.showShortToast("加载更多...");
                initData();
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * item单击事件监听。
     */
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            mToastUtil.showShortToast("第 " + position + " 条数据!");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initView();
    }

    private void initData() {
        /*
        if (mList != null && !mList.isEmpty()) {
            mList.clear();
        }
        */
        for (int i = 0; i < 20; i++) {
            mList.add("第 " + i + " 条数据!");
        }
    }

    private void initView() {
        // 下拉刷新
        mSrl_gridview.setOnRefreshListener(onRefreshListener);
        // 上拉加载
        mSdrv_gridview.addOnScrollListener(mOnScrollListener);

        // 布局管理器
        GridLayoutManager glm = new GridLayoutManager(this, 3);
        glm.setOrientation(GridLayoutManager.VERTICAL);
        mSdrv_gridview.setLayoutManager(glm);
        // 如果Item够简单，高度是确定的，打开FixSize将提高性能
        mSdrv_gridview.setHasFixedSize(true);
        // 设置Item默认动画
        mSdrv_gridview.setItemAnimator(new DefaultItemAnimator());
        // 添加分割线
        mSdrv_gridview.addItemDecoration(new GridViewDecoration(this, GridLayout.VERTICAL));

        // 开启拖拽
        mSdrv_gridview.setLongPressDragEnabled(true);
        // 监听拖拽，更新UI。
        mSdrv_gridview.setOnItemMoveListener(onItemMoveListener);
        mSdrv_gridview.setOnItemStateChangedListener(mOnItemStateChangedListener);

        mAdapter = new GridViewAdapter(mList);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mSdrv_gridview.setAdapter(mAdapter);
    }
}
