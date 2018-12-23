package com.example.ran.ratingplayground_client.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.model.User;
import com.example.ran.ratingplayground_client.utils.AppConstants;

public class PlayerMainActivity extends AppCompatActivity {

    TextView t1 , t2 , t3 , t4 , t5;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_main);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (User)bundle.getSerializable(AppConstants.USER);

        initalizeUI();
    }

    private void initalizeUI() {
        t1 = (TextView)findViewById(R.id.player_text1_id);
        t2 = (TextView)findViewById(R.id.player_text2_id);
        t3 = (TextView)findViewById(R.id.player_text3_id);
        t4 = (TextView)findViewById(R.id.player_text4_id);
        t5 = (TextView)findViewById(R.id.player_text5_id);

        t1.setText("email: " + mUser.getEmail());
        t2.setText("name: "+ mUser.getUsername());
        t3.setText("play: " + mUser.getPlayground());
        t4.setText("role: " + mUser.getRole());
        t5.setText("code: " + mUser.getCode());

    }
}
