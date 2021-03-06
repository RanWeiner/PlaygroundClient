package com.example.ran.ratingplayground_client.activities.common;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.activities.manager.ManagerMainActivity;
import com.example.ran.ratingplayground_client.activities.player.PlayerMainActivity;
import com.example.ran.ratingplayground_client.model.UserTO;
import com.example.ran.ratingplayground_client.utils.AppConstants;
import com.example.ran.ratingplayground_client.utils.HttpRequestsHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateUserActivity extends AppCompatActivity implements HttpRequestsHandler.ResponseListener {
    private String updatedUserName, updatedRole , updatedAvatar;
    private UserTO mUser;
    private Button mApplyBtn, mCancelBtn;
    private ImageButton mUpdateAvatarBtn;
    private EditText mUsernameText;
    private RadioButton mManagerRadioBtn , mPlayerRadioBtn;
    private ProgressBar mProgressBar;
    private HttpRequestsHandler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (UserTO)bundle.getSerializable(AppConstants.USER);

        updatedRole = mUser.getRole();
        updatedAvatar = mUser.getAvatar();
        updatedUserName = mUser.getUsername();

        initializeUI();

        mHandler = HttpRequestsHandler.getInstance();
        mHandler.setResponseListener(this);
    }


    private void initializeUI() {
        mManagerRadioBtn = (RadioButton)findViewById(R.id.update_manager_radio_btn);
        mPlayerRadioBtn = (RadioButton)findViewById(R.id.update_player_radio_btn);
        mUsernameText = (EditText)findViewById(R.id.update_username_text);
        mProgressBar = (ProgressBar)findViewById(R.id.update_user_progress_bar_id);
        mApplyBtn = (Button) findViewById(R.id.apply_changes_btn_id);
        mCancelBtn = (Button) findViewById(R.id.cancel_changes_btn_id);
        mUpdateAvatarBtn = (ImageButton)findViewById(R.id.update_avatar_btn_id);
        mUsernameText.setText(mUser.getUsername());
        setRadioButtons();
        setButtonListeners();
    }

    private void setButtonListeners() {
        mApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                updateUser();
            }
        });


        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLastActivity();
            }
        });
    }




    private void goToManagerMainActivity() {
        Intent intent = new Intent(UpdateUserActivity.this , ManagerMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , mUser);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }



    private void goToPlayerMainActivity() {
        Intent intent = new Intent(UpdateUserActivity.this , PlayerMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , mUser);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }



    @Override
    public void onBackPressed() {
        goToLastActivity();
    }




    private void updateUser() {
        boolean dirty = false;
        updatedUserName = mUsernameText.getText().toString();

        if (!updatedUserName.isEmpty() && !mUser.getUsername().equals(updatedUserName)) {
            mUser.setUsername(updatedUserName);
            dirty = true;
        }

        if (!mUser.getRole().equals(updatedRole)) {
            mUser.setRole(updatedRole);
            dirty = true;
        }

        if (!updatedAvatar.isEmpty() && !mUser.getAvatar().equals(updatedAvatar)) {
            mUser.setAvatar(updatedAvatar);
            dirty = true;
        }


        if (dirty == true) {
            JSONObject jsonObject = mUser.toJson();
            String url =  AppConstants.HOST + AppConstants.HTTP_USER + mUser.getPlayground() + "/" + mUser.getEmail();
            mHandler.putRequest(url , AppConstants.EVENT_UPDATE_USER , jsonObject);
        } else {
            goToLastActivity();
        }

    }


    private void setRadioButtons() {
        if (mUser.getRole().equals(AppConstants.PLAYER)) {
            mPlayerRadioBtn.setChecked(true);
            updatedRole = AppConstants.PLAYER;
        } else {
            mManagerRadioBtn.setChecked(true);
            updatedRole = AppConstants.MANAGER;
        }
    }


    public void onRoleRadioBtnClicked(View view) {

        boolean checked = ((RadioButton)view).isChecked();

        switch (view.getId()) {
            case R.id.update_manager_radio_btn:
                if (checked) {
                    updatedRole = AppConstants.MANAGER;
                }
                break;

            case R.id.update_player_radio_btn:
                if (checked) {
                    updatedRole = AppConstants.PLAYER;
                }
                break;
        }
    }



    @Override
    public void onSuccess(String myResponse, String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UpdateUserActivity.this , "updated successfully!" , Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
                goToLastActivity();
            }
        });
    }



    private void goToLastActivity() {
        if (mUser.getRole().equals(AppConstants.PLAYER)) {
            goToPlayerMainActivity();
        } else {
            goToManagerMainActivity();
        }
    }


    @Override
    public void onFailed(final String error , String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(UpdateUserActivity.this , error , Toast.LENGTH_LONG).show();
            }
        });
    }
}
