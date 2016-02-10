package com.github.chenqihong.queenexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import com.github.chenqihong.queen.Queen;
import com.github.chenqihong.queen.Watcher.IQueenWatcher;

/**
 * Define an example to run Queen. Please note that if you want to make this example work,
 * you should define url of your server to collect data.
 */
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

        /**
         * Get Queen's instance.
         */
        mQueen = Queen.getInstance();

        /**
         * Queen's initiation.
         */
        mQueen.init("type your server's domain", getApplication());

        /**
         * Set url to make Queen run on the phone.
         */
        mQueen.setUrl("type your server's url");
        mStringBuilder = new StringBuilder();

        /**
         * Watcher to watch the data gained by queen.
         */
        mWatcher = new IQueenWatcher() {
            @Override
            public void update(String str) {
                mStringBuilder.append(str + "\n\n");
                mMessageView.setText(mStringBuilder.toString());
            }
        };
        mQueen.registerObserver(mWatcher);

    }

    @Override
    public void onResume(){
        super.onResume();

        /**
         * collect activity's status, when it is open.
         */
        mQueen.getInstance().activityDataCollect(this.toString(), null, null, true, this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        /**
         * collect activity's status, when it is closed.
         */
        mQueen.activityDataCollect(this.toString(), null, null, false, this);
        mQueen.appExitSend(this);
        mQueen.unregisterObserver(mWatcher);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        /**
         * collect view interaction data, i.e. button clicked.
         */
        mQueen.getInstance()
                .recognizeViewEvent(ev, this.getWindow()
                        .getDecorView(), this);
        return super.dispatchTouchEvent(ev);
    }

}
