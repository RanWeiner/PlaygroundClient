package com.example.ran.ratingplayground_client.activities.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.model.NewUserForm;
import com.example.ran.ratingplayground_client.model.UserTO;
import com.example.ran.ratingplayground_client.utils.AppConstants;
import com.example.ran.ratingplayground_client.utils.HttpRequestsHandler;
import com.example.ran.ratingplayground_client.utils.InputValidation;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterUserActivity extends AppCompatActivity implements HttpRequestsHandler.ResponseListener {

    private Button mRegisterBtn;
    private ImageButton  mAvatarBtn;
    private EditText mEmailText, mUsernameText;
    private ProgressBar mProgressBar;
    private AlertDialog mAlertDialog;
    private String mEmail,mUsername , mAvatarImagePath = "dummy" , mRole ;
    private NewUserForm mNewUserForm;
    private Button mVerifyCodeBtn;
    private HttpRequestsHandler mHandler;
    private UserTO mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        mRole = AppConstants.PLAYER;

        initializeUI();

        mHandler = HttpRequestsHandler.getInstance();
        mHandler.setResponseListener(this);
    }


    private void initializeUI() {

        mRegisterBtn = (Button)findViewById(R.id.register_activiy_register_btn);
        mAvatarBtn = (ImageButton)findViewById(R.id.avatar_btn_id);
        mEmailText = (EditText)findViewById(R.id.email_text);
        mUsernameText = (EditText)findViewById(R.id.username_text);
        mProgressBar = (ProgressBar)findViewById(R.id.register_progress_bar_id);
        mVerifyCodeBtn = (Button)findViewById(R.id.verify_code_btn);
        setListeners();
    }

    public void onRoleRadioBtnClicked(View view) {

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

        mVerifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser != null && mEmailText.getText().toString().equals(mUser.getEmail())){
                    showVerificationDialog(mUser);
                }
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
        mNewUserForm = new NewUserForm(mEmail , mUsername , mAvatarImagePath, mRole);
        JSONObject jsonObject = mNewUserForm.toJson();
        String url = AppConstants.HOST + AppConstants.HTTP_USER;
        mHandler.postRequest(url ,AppConstants.EVENT_REGISTER , jsonObject);
    }



    @Override
    public void onSuccess(final String myResponse , final String event) {

        if (event.equals(AppConstants.EVENT_VERIFY_USER)) {
            mAlertDialog.dismiss();
            userRegisteredAndVerified(myResponse);

        } else if (event.equals(AppConstants.EVENT_REGISTER)) {
            userRegistered(myResponse);
        }



    }



    private void userRegistered(final String myResponse) {
        runOnUiThread(new Runnable() {
            JSONObject json = null;
            @Override
            public void run() {
                Toast.makeText(RegisterUserActivity.this , "registered successfully!" , Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
                try {
                    json = new JSONObject(myResponse);
                    mUser =  UserTO.fromJson(json);
                    verifyUser(mUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void verifyUser(UserTO user) {
        Log.i("USER CODE" , "Code: "+ user.getCode());
        showVerificationDialog(user);
    }



    private void showVerificationDialog(final UserTO user) {
        final EditText input = new EditText(RegisterUserActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);

        mAlertDialog = builder.setTitle("VERIFICATION")
                .setMessage("Enter the 4-digit code you have received in your Mail inbox")
                .setCancelable(true)
                .setView(input)
                .setIcon(R.drawable.ic_verify)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        mAlertDialog.show();

        Button theButton = mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int code = Integer.parseInt(input.getText().toString());
                String url = AppConstants.HOST + AppConstants.HTTP_VERIFY_USER + "/" + user.getPlayground() + "/" + user.getEmail() + "/" + code;
                mHandler.getRequest(url , AppConstants.EVENT_VERIFY_USER);
            }
        });
    }

    @Override
    public void onFailed(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
                Log.i("Register Error" , error);
                Toast.makeText(RegisterUserActivity.this , "Sorry, something went wrong... try again" , Toast.LENGTH_LONG).show();
            }
        });
    }

}
