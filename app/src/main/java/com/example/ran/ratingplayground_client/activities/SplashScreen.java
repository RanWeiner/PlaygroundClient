package com.example.ran.ratingplayground_client.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.ran.ratingplayground_client.R;

public class SplashScreen extends AppCompatActivity {

    private ImageView mLogo;
    private Animation mAnimAlpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        setContentView(R.layout.activity_splash_screen);

        mAnimAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_splash_screen);
        mLogo = (ImageView)findViewById(R.id.logo_screen);

        new CountDownTimer(4000, 4000) {
            public void onTick(long millisUntilFinished) {
                mLogo.setVisibility(View.VISIBLE);
                mLogo.setAnimation(mAnimAlpha);
            }
            public void onFinish() {
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
