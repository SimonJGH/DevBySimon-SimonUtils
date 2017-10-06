package com.simon.utils.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.simon.utils.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by Simon on 2017/10/5.
 */

@ContentView(R.layout.fragment_home_right)
public class RightHomeFragment extends BaseFragment {

    @ViewInject(R.id.vp_right_home)
    ViewPager mVp_right_home;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
