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


    private ImageView imageViewTop;
    private ImageView imageViewDown;
    private Button menuBtn;
    private TextView pointsTextView , totalPointsTextView;
    private int points;
    private long totalPoints;
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
        totalPoints = bundle.getLong("totalPoints");

        mUser.setPoints(totalPoints);
        menuBtn = (Button)findViewById(R.id.go_back_btn_id);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainMenu();
            }
        });
        LinearLayout layout = (LinearLayout) findViewById(R.id.end_game_layout);
        pointsTextView = (TextView) findViewById(R.id.final_points_id);
        pointsTextView.setText("You have gained " + points + " points!");

        totalPointsTextView = (TextView) findViewById(R.id.final_total_points_id);
        totalPointsTextView.setText("Total Points: " + totalPoints);

        imageViewTop =  (ImageView)findViewById(R.id.endGamePictureTop);
        imageViewDown = (ImageView)findViewById(R.id.endGamePictureDown);

        if (points  >= 6) {
            layout.setBackgroundColor(getColor(R.color.green));
            imageViewTop.setImageResource(R.drawable.well_done);
            imageViewDown.setImageResource(R.drawable.thumbs);
            pointsTextView.setTextColor(getColor(R.color.black));
            menuBtn.setTextColor(getColor(R.color.black));
        } else {
            layout.setBackgroundColor(getColor(R.color.red));
            imageViewTop.setImageResource(R.drawable.not_enough);
            imageViewDown.setImageResource(R.drawable.thumbs_down);
            pointsTextView.setTextColor(getColor(R.color.white));
            menuBtn.setTextColor(getColor(R.color.white));
        }

    }



    public void goToMainMenu(){
        Intent intent = new Intent(EndRatingActivity.this, PlayerMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , mUser);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


}
