package com.example.ran.ratingplayground_client.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.model.User;
import com.example.ran.ratingplayground_client.utils.AppConstants;

public class PlayerMainActivity extends AppCompatActivity {
    private Button mUpdateUser , mLogout , mSearchElements;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_main);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (User)bundle.getSerializable(AppConstants.USER);

        initializeUI();
    }

    private void initializeUI() {
        mUpdateUser = (Button)findViewById(R.id.player_update_user_btn_id);
        mLogout = (Button)findViewById(R.id.player_logout_btn_id);
        mSearchElements = (Button)findViewById(R.id.player_search_elements_btn_id);

        setListeners();
    }


    private void setListeners() {
        mUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerMainActivity.this , UpdateUserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(AppConstants.USER , mUser);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });


        mSearchElements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerMainActivity.this , LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    private void openOptionsDialog() {

        CharSequence[] customElementActivities = {"Guess Rank" , "See Rank" , "Post Review" , "See Review"};

        CharSequence[] billboardActivities = {"Post" , "View"};

        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerMainActivity.this);
        builder.setTitle("Player Actions");
        builder.setItems(new CharSequence[]{"Update", "button 2", "button 3", "button 4"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Toast.makeText(PlayerMainActivity.this, "clicked 1", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(PlayerMainActivity.this, "clicked 2", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(PlayerMainActivity.this, "clicked 3", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(PlayerMainActivity.this, "clicked 4", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        builder.create().show();
    }
}
