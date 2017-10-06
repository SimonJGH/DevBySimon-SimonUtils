package com.simon.utils.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.simon.utils.R;

import org.xutils.x;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        x.view().inject(this);
    }
}
