package com.example.ran.ratingplayground_client.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.model.NewUserForm;
import com.example.ran.ratingplayground_client.model.User;
import com.example.ran.ratingplayground_client.utils.AppConstants;
import com.example.ran.ratingplayground_client.utils.HttpRequestsHandler;
import com.example.ran.ratingplayground_client.utils.InputValidation;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterUserActivity extends AppCompatActivity implements HttpRequestsHandler.ResponseListener {

    private Button mRegisterBtn;
    private Button mAvatarBtn;
    private EditText mEmailText, mUsernameText;
    private ProgressBar mProgressBar;

    private String mEmail,mUsername , mAvatarImagePath = "dummy" , mRole , mUserKey;
    private NewUserForm mNewUserForm;

    private HttpRequestsHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        mRole = AppConstants.PLAYER;

        initializeUI();

        mHandler = HttpRequestsHandler.getInstance();
        mHandler.setResponseListener(this);
    }


    private void initializeUI() {

        mRegisterBtn = (Button)findViewById(R.id.register_activiy_register_btn);
        mAvatarBtn = (Button)findViewById(R.id.avatar_btn_id);
        mEmailText = (EditText)findViewById(R.id.email_text);
        mUsernameText = (EditText)findViewById(R.id.username_text);
        mProgressBar = (ProgressBar)findViewById(R.id.register_progress_bar_id);

        setListeners();
    }

    public void onRadioBtnClicked(View view) {

        boolean checked = ((RadioButton)view).isChecked();

        switch (view.getId()) {
            case R.id.manager_radio_btn:
                if (checked) {
                    mRole = AppConstants.MANAGER;
                }
                break;

            case R.id.player_radio_btn:
                if (checked) {
                    mRole = AppConstants.PLAYER;
                }
                break;
        }
    }


    private void setListeners() {
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = mEmailText.getText().toString();
                mUsername = mUsernameText.getText().toString();

                boolean isValid = validateUserInput();
                if (isValid){
                    mProgressBar.setVisibility(View.VISIBLE);
                    registerUser();
                }
            }
        });

        mAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private boolean validateUserInput() {

        //validate username
        if (!InputValidation.validateUserInput(mUsername)) {
            mUsernameText.setError("First name is required");
            mUsernameText.requestFocus();
            return false;

        }

        //validate email
        if (!InputValidation.validateUserInput(mEmail)) {
            mEmailText.setError("Email is required");
            mEmailText.requestFocus();
            return false;
        }

        if (!InputValidation.validateEmailAddress(mEmail)) {
            mEmailText.setError("Please enter a valid email");
            mEmailText.requestFocus();
            return false;
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void registerUser() {
        mUserKey = AppConstants.PLAYGROUND + "@@" + mEmail;
//        mNewUserForm = new NewUserForm(mEmail , mUsername , mAvatarImagePath, mRole);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", mUsername);
            jsonObject.put("email", mEmail);
            jsonObject.put("avatar", mAvatarImagePath);
            jsonObject.put("role", mRole);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = AppConstants.HOST + AppConstants.HTTP_CREATE_USER;
        mHandler.postRequest(url ,AppConstants.EVENT_REGISTER , jsonObject);
    }



    @Override
    public void onSuccess(final String myResponse , final String event) {

        if (event.equals(AppConstants.EVENT_VERIFY_USER)) {
            userRegisteredAndVerified(myResponse);

        } else if (event.equals(AppConstants.EVENT_REGISTER)) {
            userRegistered(myResponse);
        }


    }



    private void userRegistered(final String myResponse) {
        runOnUiThread(new Runnable() {
            JSONObject json = null;
            User user = null;
            @Override
            public void run() {
                Toast.makeText(RegisterUserActivity.this , "registered successfully!" , Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
                try {
                    json = new JSONObject(myResponse);
                    user =  User.fromJson(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                verifyUser(user);
            }
        });
    }

    private void userRegisteredAndVerified(final String myResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterUserActivity.this , "verified successfully!" , Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
                onBackPressed();
            }
        });
    }

    private void verifyUser(User user) {
        Log.i("USER CODE" , "Code: "+ user.getCode());
        showVerificationDialog(user);
    }


private void showVerificationDialog(final User user) {
    final EditText input = new EditText(RegisterUserActivity.this);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    input.setLayoutParams(lp);

    AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterUserActivity.this);
    alertDialog.setTitle("VERIFICATION")
            .setMessage("Enter the 4-digit code you have received in your Mail inbox")
            .setCancelable(true)
            .setView(input)
            .setIcon(R.drawable.ic_verify)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int code = Integer.parseInt(input.getText().toString());
                String url = AppConstants.HOST + AppConstants.HTTP_VERIFY_USER + "/" + user.getPlayground() + "/" + user.getEmail() + "/" + code;
                mHandler.getRequest(url , AppConstants.EVENT_VERIFY_USER);
            }
        });
    alertDialog.show();
}

    private void goToManagerMainActivity(User user) {
        Intent intent = new Intent(RegisterUserActivity.this , ManagerMainActivity.class);
        intent.putExtra(AppConstants.USER , user);
        startActivity(intent);
        finish();
    }

    private void goToPlayerMainActivity(User user) {
        Intent intent = new Intent(RegisterUserActivity.this , PlayerMainActivity.class);
        intent.putExtra(AppConstants.USER , user);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailed(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(RegisterUserActivity.this , error , Toast.LENGTH_LONG).show();
            }
        });
    }
}
