package com.simon.utils.customview;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.simon.utils.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 模拟打字机效果
 * <p>
 * <com.simon.printstyle.PrintTextView
 * android:id="@+id/ptv_style"
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * android:gravity="center_horizontal"
 * android:lines="4"
 * android:textSize="20sp" />
 * <p>
 * private PrintTextView ptv_style;
 * private String PRINT_DATA = "有道翻译是网易公司开发的一款翻译软件，其最大特色在于翻译引擎是基于搜索引擎，网络释义的，也就是说它所翻译的词释义都是来自网络。";
 * <p>
 * ptv_style = findViewById(R.id.ptv_style);
 * ptv_style.setOnPrintTextViewListener(new PrintTextView.PrintTextViewListener() {
 *
 * @Override public void onTypeStart() {
 * Log.i("Simon", "Print Start");
 * }
 * @Override public void onTypeOver() {
 * Log.i("Simon", "Print Over");
 * }
 * });
 * ptv_style.start(PRINT_DATA);
 * <p>
 * ptv_style.setOnClickListener(new View.OnClickListener() {
 * @Override public void onClick(View v) {
 * ptv_style.start(PRINT_DATA);
 * }
 * });
 */
@SuppressWarnings("all")
public class PrintTextView extends android.support.v7.widget.AppCompatTextView {
    private Context mContext = null;
    private MediaPlayer mMediaPlayer = null;
    private String mShowTextString = null;
    private Timer mPrintTimer = null;
    private PrintTextViewListener mPrintTextViewListener = null;
    private static final int PRINT_TIME_DELAY = 80;
    private int mPrintTimeDelay = PRINT_TIME_DELAY; // 打字间隔

    public PrintTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public PrintTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PrintTextView(Context context) {
        super(context);
        mContext = context;
    }

    public void setOnPrintTextViewListener(PrintTextViewListener listener) {
        mPrintTextViewListener = listener;
    }

    /**
     * 简单开始打印
     */
    public void start(final String textString) {
        start(textString, PRINT_TIME_DELAY);
    }

    /**
     * 带延时效果的开始打印
     */
    public void start(final String textString, final int printDelay) {
        if (TextUtils.isEmpty(textString) || printDelay < 0) {
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
                mShowTextString = textString;
                mPrintTimeDelay = printDelay;
                setText("");
                startTypeTimer();
                if (null != mPrintTextViewListener) {
                    mPrintTextViewListener.onTypeStart();
                }
            }
        });
    }

    /**
     * 结束打印效果
     */
    public void stop() {
        stopTypeTimer();
        stopAudio();
    }

    private void startTypeTimer() {
        stopTypeTimer();
        mPrintTimer = new Timer();
        mPrintTimer.schedule(new PrintTimerTask(), mPrintTimeDelay);
    }

    private void stopTypeTimer() {
        if (null != mPrintTimer) {
            mPrintTimer.cancel();
            mPrintTimer = null;
        }
    }

    private void startAudioPlayer() {
        stopAudio();
        playAudio(R.raw.print);
    }

    private void playAudio(int audioResId) {
        try {
            stopAudio();
            mMediaPlayer = MediaPlayer.create(mContext, audioResId);
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAudio() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    class PrintTimerTask extends TimerTask {
        @Override
        public void run() {
            post(new Runnable() {
                @Override
                public void run() {
                    if (getText().toString().length() < mShowTextString.length()) {
                        setText(mShowTextString.substring(0, getText().toString().length() + 1));
                        startAudioPlayer();
                        startTypeTimer();
                    } else {
                        stopTypeTimer();
                        if (null != mPrintTextViewListener) {
                            mPrintTextViewListener.onTypeOver();
                        }
                    }
                }
            });
        }
    }

    /**
     * 打印文字监听器
     */
    public interface PrintTextViewListener {
        public void onTypeStart();

        public void onTypeOver();
    }
}