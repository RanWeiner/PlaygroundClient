package com.example.ran.ratingplayground_client.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ran.ratingplayground_client.model.User;
import com.example.ran.ratingplayground_client.utils.AppConstants;
import com.example.ran.ratingplayground_client.utils.HttpRequestsHandler;
import com.example.ran.ratingplayground_client.utils.InputValidation;
import com.example.ran.ratingplayground_client.utils.NetworkStatus;
import com.example.ran.ratingplayground_client.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;


public class LoginActivity extends AppCompatActivity implements HttpRequestsHandler.ResponseListener {

    private Button mSignIn, mRegister;
    private EditText mEmailText , mPlaygroundText;
    private ProgressBar mProgressBar;
    private NetworkStatus mNetworkStatus;



    /////////////////////// new Shit ///////////////////////
    private HttpRequestsHandler mHandler;
    ////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initNetworkStatus();
        initializeUI();

        mHandler = HttpRequestsHandler.getInstance();
        mHandler.setResponseListener(this);
    }

    private void initializeUI() {

        mSignIn = (Button) findViewById(R.id.sign_in_button);
        mRegister = (Button) findViewById(R.id.login_activiy_register_btn);
        mEmailText = (EditText) findViewById(R.id.email_login_text);
        mPlaygroundText = (EditText)findViewById(R.id.playground_login_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_id);
        mProgressBar.setVisibility(View.INVISIBLE);
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



    private void goToManagerActivity(User user) {
        Intent intent = new Intent(LoginActivity.this , ManagerMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , user);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }



    private void goToPlayerActivity(User user) {
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
                JSONObject json = null;
                User user = null;
                mProgressBar.setVisibility(View.INVISIBLE);
                try {
                    json = new JSONObject(myResponse);
                    user =  User.fromJson(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (user != null) {
                    switch (user.getRole()) {
                        case AppConstants.PLAYER:
                            goToPlayerActivity(user);
                            break;

                        case AppConstants.MANAGER:
                            goToManagerActivity(user);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onFailed(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this , error , Toast.LENGTH_LONG).show();
            }
        });
    }
}