package com.simon.utils.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.simon.utils.R;
import com.simon.utils.adapter.ListViewAdapter;
import com.simon.utils.widget.recycler.ListViewDecoration;
import com.simon.utils.widget.recycler.SDRecyclerView;
import com.simon.utils.widget.recycler.SwipeMenu;
import com.simon.utils.widget.recycler.SwipeMenuItem;
import com.simon.utils.widget.recycler.interfaces.Closeable;
import com.simon.utils.widget.recycler.interfaces.OnItemClickListener;
import com.simon.utils.widget.recycler.interfaces.OnSwipeMenuItemClickListener;
import com.simon.utils.widget.recycler.interfaces.SwipeMenuCreator;
import com.simon.utils.widget.recycler.touch.OnItemMoveListener;
import com.simon.utils.widget.recycler.touch.OnItemStateChangedListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("all")
@ContentView(R.layout.activity_list_view)
public class ListViewActivity extends BaseActivity {

    @ViewInject(R.id.srl_listview)
    SwipeRefreshLayout mSrl_listview;

    @ViewInject(R.id.sdrv_listview)
    SDRecyclerView mSdrv_listview;

    private ListViewAdapter mAdapter;
    private List<String> mList = new ArrayList<>();

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.swipe_item_width);
            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = getResources().getDimensionPixelSize(R.dimen.swipe_item_height);
            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_delete)
                        // .setText("删除") // 文字，还可以设置文字颜色，大小
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
            }
        }
    };

    /**
     * swipe菜单点击监听。
     */
    private OnSwipeMenuItemClickListener mMenuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView
         *                        #RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。
            if (direction == SDRecyclerView.RIGHT_DIRECTION) {
                // Toast.makeText(mContext, "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SDRecyclerView.LEFT_DIRECTION) {
                // Toast.makeText(mContext, "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }
            // TODO 推荐调用Adapter.notifyItemRemoved(position)，也可以Adapter.notifyDataSetChanged();
            if (menuPosition == 0) {
                // 删除按钮被点击。
                mToastUtil.showShortToast("删除按钮被点击");
            }
        }
    };

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
            mSdrv_listview.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initData();
                    mSrl_listview.setRefreshing(false);
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
        mSrl_listview.setOnRefreshListener(onRefreshListener);
        // 上拉加载
        mSdrv_listview.addOnScrollListener(mOnScrollListener);

        // 布局管理器
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mSdrv_listview.setLayoutManager(llm);
        // 如果Item够简单，高度是确定的，打开FixSize将提高性能
        mSdrv_listview.setHasFixedSize(true);
        // 设置Item默认动画
        mSdrv_listview.setItemAnimator(new DefaultItemAnimator());
        // 添加分割线
        mSdrv_listview.addItemDecoration(new ListViewDecoration(mContext));

        // 设置菜单创建器。
        mSdrv_listview.setSwipeMenuCreator(mSwipeMenuCreator);
        // 设置菜单Item点击监听。
        mSdrv_listview.setSwipeMenuItemClickListener(mMenuItemClickListener);
        // 开启拖拽
        mSdrv_listview.setLongPressDragEnabled(true);
        // 监听拖拽，更新UI。
        mSdrv_listview.setOnItemMoveListener(onItemMoveListener);
        mSdrv_listview.setOnItemStateChangedListener(mOnItemStateChangedListener);

        mAdapter = new ListViewAdapter(mList);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mSdrv_listview.setAdapter(mAdapter);
    }
}
