package com.example.ran.ratingplayground_client.activities.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ran.ratingplayground_client.activities.manager.ManagerMainActivity;
import com.example.ran.ratingplayground_client.activities.player.PlayerMainActivity;
import com.example.ran.ratingplayground_client.model.UserTO;
import com.example.ran.ratingplayground_client.utils.AppConstants;
import com.example.ran.ratingplayground_client.utils.HttpRequestsHandler;
import com.example.ran.ratingplayground_client.utils.InputValidation;
import com.example.ran.ratingplayground_client.utils.NetworkStatus;
import com.example.ran.ratingplayground_client.R;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity implements HttpRequestsHandler.ResponseListener {

    private Button mSignIn, mRegister;
    private EditText mEmailText , mPlaygroundText;
    private ProgressBar mProgressBar;
    private NetworkStatus mNetworkStatus;
    private HttpRequestsHandler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        initializeUI();
        initNetworkStatus();
        mHandler = HttpRequestsHandler.getInstance();
        mHandler.setResponseListener(this);
    }

    private void initializeUI() {

        mSignIn = (Button) findViewById(R.id.sign_in_button);
        mRegister = (Button) findViewById(R.id.login_activiy_register_btn);
        mEmailText = (EditText) findViewById(R.id.email_login_text);
        mPlaygroundText = (EditText)findViewById(R.id.playground_login_text);
        mProgressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
        mProgressBar.setVisibility(View.INVISIBLE);

//        //just for DEBUG
//        mEmailText.setText("ratingtest123@walla.com");
        mPlaygroundText.setText("ratingplayground");

        setButtonListeners();
    }

    public void initNetworkStatus() {
        mNetworkStatus = new NetworkStatus(this);

        if (!mNetworkStatus.isConnected()){
            showNetworkErrorMessage();
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
        }
    }


    public void showNetworkErrorMessage() {
        mProgressBar.setVisibility(View.INVISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sorry, this application required an Internet connection.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void setButtonListeners() {

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mNetworkStatus.isConnected()){
                    showNetworkErrorMessage();
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(LoginActivity.this, RegisterUserActivity.class);
                startActivity(intent);
                finish();

            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mNetworkStatus.isConnected()){
                    showNetworkErrorMessage();
                    return;
                }

                boolean isValid = validateUserInput();
                if (isValid) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    signIn(mEmailText.getText().toString() , mPlaygroundText.getText().toString());
                }

            }
        });
    }



    private void signIn(String email , String playground) {
        String url = AppConstants.HOST + AppConstants.HTTP_GET_USER + "/" + playground + "/" + email;
        mHandler.getRequest(url , AppConstants.EVENT_SIGN_IN);
        }



    private void goToManagerActivity(UserTO user) {
        Intent intent = new Intent(LoginActivity.this , ManagerMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , user);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }



    private void goToPlayerActivity(UserTO user) {
        Intent intent = new Intent(LoginActivity.this , PlayerMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , user);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


    public boolean validateUserInput() {

        String email = mEmailText.getText().toString();
        String playground = mPlaygroundText.getText().toString();

        if (!InputValidation.validateUserInput(email)) {
            mEmailText.setError("Email is required");
            mEmailText.requestFocus();
            return false;
        }

        if (!InputValidation.validateUserInput(playground)) {
            mPlaygroundText.setError("Playground name is required");
            mPlaygroundText.requestFocus();
            return false;
        }

        if (!InputValidation.validateEmailAddress(email)) {
            mEmailText.setError("Please enter a valid email");
            mEmailText.requestFocus();
            return false;
        }
        return true;
    }




    @Override
    public void onSuccess(final String myResponse , final String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
                    JSONObject json = null;
                    UserTO user = null;
                    try {
                        json = new JSONObject(myResponse);
                        user = UserTO.fromJson(json);

                        switch (user.getRole()) {
                            case AppConstants.PLAYER:
                                goToPlayerActivity(user);
                                break;

                            case AppConstants.MANAGER:
                                goToManagerActivity(user);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        });
        }


    @Override
    public void onFailed(final String error, String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
                Log.i("Login failed" , error );
                Toast.makeText(LoginActivity.this , "User Not Registered",  Toast.LENGTH_LONG).show();
            }
        });
    }
}
