package com.simon.utils.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.simon.utils.MainActivity;
import com.simon.utils.R;
import com.simon.utils.activity.GIFActivity;
import com.simon.utils.activity.GestureImageViewActivity;
import com.simon.utils.activity.GridViewActivity;
import com.simon.utils.activity.ListViewActivity;
import com.simon.utils.adapter.LeftMenuAdapter;
import com.simon.utils.widget.recycler.ListViewDecoration;
import com.simon.utils.widget.recycler.SDRecyclerView;
import com.simon.utils.widget.recycler.interfaces.OnItemClickListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Simon on 2017/10/5.
 */

@ContentView(R.layout.fragment_menu_left)
public class LeftMenuFragment extends BaseFragment implements OnItemClickListener {

    @ViewInject(R.id.srl_left_layout)
    SwipeRefreshLayout mSrl_left_layout;
    @ViewInject(R.id.rv_left_menu)
    SDRecyclerView mRv_left_menu;

    private LeftMenuAdapter mMenuAdapter;
    private String[] stringArray;
    private List<String> asList;
    private MainActivity mActivity;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initView();
    }

    private void initView() {
        // 布局管理器
        mRv_left_menu.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 如果Item够简单，高度是确定的，打开FixSize将提高性能
        mRv_left_menu.setHasFixedSize(true);
        // 设置Item默认动画，加也行，不加也行
        mRv_left_menu.setItemAnimator(new DefaultItemAnimator());
        // 添加分割线
        mRv_left_menu.addItemDecoration(new ListViewDecoration(getActivity()));

        mMenuAdapter = new LeftMenuAdapter(asList);
        mMenuAdapter.setOnItemClickListener(this);
        mRv_left_menu.setAdapter(mMenuAdapter);
    }

    private void initData() {
        mActivity = (MainActivity)getActivity();

        stringArray = getResources().getStringArray(R.array.left_menu);
        asList = Arrays.asList(stringArray);
    }

    @Override
    public void onItemClick(int position) {
        mActivity.slidingMenuToggle();
        Intent intent = new Intent();
        switch (position) {
            case 0:
                startActivity(intent.setClass(getContext(), GIFActivity.class));
                break;
            case 1:
                startActivity(intent.setClass(getContext(), GestureImageViewActivity.class));
                break;
            case 2:
                startActivity(intent.setClass(getContext(), ListViewActivity.class));
                break;
            case 3:
                startActivity(intent.setClass(getContext(), GridViewActivity.class));
                break;
        }
    }

}
