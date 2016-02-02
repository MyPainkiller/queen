package com.github.chenqihong.queenexample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Button mLogButton;
    private TextView mMessageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void initView(){
        mLogButton = (Button)findViewById(R.id.main_log_button);
        mMessageView = (TextView)findViewById(R.id.main_message_view);
    }
}
