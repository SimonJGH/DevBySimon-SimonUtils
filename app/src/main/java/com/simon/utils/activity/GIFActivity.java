package com.simon.utils.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.simon.utils.R;
import com.simon.utils.customview.GifView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.io.InputStream;

@ContentView(R.layout.activity_gif)
public class GIFActivity extends BaseActivity {

    @ViewInject(R.id.gif_show)
    GifView mGif_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 我这里就简写了 最好不要再主线程进行流操作
         */

        try {
            InputStream inputStream = getAssets().open("verygood.gif");
            mGif_show.setGifStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
