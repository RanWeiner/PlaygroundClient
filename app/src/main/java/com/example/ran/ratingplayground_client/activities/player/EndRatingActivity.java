package com.example.ran.ratingplayground_client.activities.player;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.model.UserTO;
import com.example.ran.ratingplayground_client.utils.AppConstants;

public class EndRatingActivity extends AppCompatActivity {

    final static int POINTS_MIN_BAR = 10;
//    private MediaPlayer mPlayer;
    private ImageView imageViewTop;
    private ImageView imageViewDown;
    private Button menuBtn;
    private TextView pointsTextView;
    private int points;
    private UserTO mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_rating);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (UserTO)bundle.getSerializable(AppConstants.USER);
        points = bundle.getInt("points");
        menuBtn = (Button)findViewById(R.id.go_back_btn_id);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainMenu();
            }
        });
        LinearLayout layout = (LinearLayout) findViewById(R.id.end_game_layout);
        pointsTextView = (TextView) findViewById(R.id.final_points_id);
        pointsTextView.setText("Your Points: " + points);
        imageViewTop =  (ImageView)findViewById(R.id.endGamePictureTop);
        imageViewDown = (ImageView)findViewById(R.id.endGamePictureDown);

        if (points  >= 10) {
            layout.setBackgroundColor(getColor(R.color.blue));
            imageViewTop.setImageResource(R.drawable.congrats);
            imageViewDown.setImageResource(R.drawable.tressure);
//            playWinMusic();
        } else {
            layout.setBackgroundColor(getColor(R.color.black));
            imageViewTop.setImageResource(R.drawable.you_lose);
            imageViewDown.setImageResource(R.drawable.lost);
//            playLoseMusic();
        }

    }



//    public void playWinMusic() {
//        try {
//            if (mPlayer != null) {
//                mPlayer.release();
//                mPlayer = null;
//            }
//
//            mPlayer = MediaPlayer.create(EndRatingActivity.this, R.raw.win);
//            mPlayer.start();
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void playLoseMusic() {
//
//        try {
//            if (mPlayer != null) {
//                mPlayer.release();
//                mPlayer = null;
//            }
//
//            mPlayer = MediaPlayer.create(EndRatingActivity.this, R.raw.lost);
//            mPlayer.start();
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    public void goToMainMenu(){
        Intent intent = new Intent(EndRatingActivity.this, PlayerMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , mUser);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


}
