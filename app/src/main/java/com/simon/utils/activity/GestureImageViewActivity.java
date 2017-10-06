package com.simon.utils.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.simon.utils.R;

/**
 * 导入布局
 * <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
 * xmlns:tools="http://schemas.android.com/tools"
 * xmlns:gesture-image="http://schemas.polites.com/android"
 * android:id="@+id/activity_gesture_image_view"
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * tools:context="com.simon.utils.activity.GestureImageViewActivity">
 * <p>
 * <com.simon.gesture.GestureImageView
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * android:src="@mipmap/damimi"
 * gesture-image:min-scale="0.75"
 * gesture-image:max-scale="10.0"/>
 * <p>
 * </RelativeLayout>
 */
public class GestureImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_image_view);
    }
}
