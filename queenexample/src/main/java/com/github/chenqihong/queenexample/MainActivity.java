package com.github.chenqihong.queenexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import com.github.chenqihong.queen.Queen;
import com.github.chenqihong.queen.Watcher.IQueenWatcher;

public class MainActivity extends Activity {

    private Button mLogButton;
    private TextView mMessageView;
    private Queen mQueen;
    private IQueenWatcher mWatcher;
    private StringBuilder mStringBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        mLogButton = (Button)findViewById(R.id.main_log_button);
        mMessageView = (TextView)findViewById(R.id.main_message_view);
        mQueen = Queen.getInstance();
        mQueen.init("www.baidu.com", getApplication());
        mQueen.setUrl("www.baidu.com");
        mStringBuilder = new StringBuilder();
        mWatcher = new IQueenWatcher() {
            @Override
            public void update(String str) {
                mStringBuilder.append(str + "\n\n");
                mMessageView.setText(mStringBuilder.toString());
            }
        };
        mQueen.registerObserver(mWatcher);
        mQueen.activityDataCollect(this.toString(), null, null, true, this);

    }

    @Override
    public void onResume(){
        super.onResume();
        mQueen.getInstance().activityDataCollect(this.toString(), null, null, true, this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mQueen.activityDataCollect(this.toString(), null, null, false, this);
        mQueen.appExitSend(this);
        mQueen.unregisterObserver(mWatcher);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mQueen.getInstance()
                .recognizeViewEvent(ev, this.getWindow()
                        .getDecorView(), this);
        return super.dispatchTouchEvent(ev);
    }

}
