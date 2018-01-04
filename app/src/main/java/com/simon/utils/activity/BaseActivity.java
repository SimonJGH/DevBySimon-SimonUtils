package com.simon.utils.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.simon.utils.R;
import com.simon.utils.utils.singtonutils.ToastUtils;

import org.xutils.x;

public class BaseActivity extends AppCompatActivity {

    public Context mContext;
    public ToastUtils mToastUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mContext = BaseActivity.this;
        mToastUtil=ToastUtils.getInstance();
        x.view().inject(this);
    }
}
