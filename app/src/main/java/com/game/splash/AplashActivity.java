package com.game.splash;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.game.index.IndexActivity;

/**
 * 起始引导页
 * */

public class AplashActivity extends AppCompatActivity {

    private static final long DELAY_TIME = 5000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_aplash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //5秒后跳到主页
                startActivity(new Intent(AplashActivity.this,IndexActivity.class));
                finish();
            }
        },DELAY_TIME);

    }
}
