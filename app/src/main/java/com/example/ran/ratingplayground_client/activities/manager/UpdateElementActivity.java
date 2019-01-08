package com.example.ran.ratingplayground_client.activities.manager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.model.ElementTO;
import com.example.ran.ratingplayground_client.model.UserTO;
import com.example.ran.ratingplayground_client.utils.AppConstants;
import com.example.ran.ratingplayground_client.utils.HttpRequestsHandler;
import com.example.ran.ratingplayground_client.utils.InputValidation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdateElementActivity extends AppCompatActivity implements HttpRequestsHandler.ResponseListener{
    private UserTO mUser;
    private ElementTO mElement;
    private ProgressBar mProgressBar;
    private HttpRequestsHandler mHandler;
    private Button mUpdateBtn;
    private String mType , mName;
    private double x , y;
    private EditText nameText ,xLocationText, yLocationText;;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_element);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (UserTO)bundle.getSerializable(AppConstants.USER);
        mElement = (ElementTO)bundle.getSerializable(AppConstants.ELEMENT);

        initializeUI();

        mHandler = HttpRequestsHandler.getInstance();
        mHandler.setResponseListener(this);
    }



    public void onUpdateTypeRadioBtnClicked(View view) {
        boolean checked = ((RadioButton)view).isChecked();

        switch (view.getId()) {
            case R.id.update_book_radio_btn:
                if (checked) {
                    mType = AppConstants.BOOK_TYPE;
                }
                break;

            case R.id.update_movie_radio_btn:
                if (checked) {
                    mType = AppConstants.MOVIE_TYPE;
                }
                break;

            case R.id.update_billboard_radio_btn:
                if (checked) {
                    mType = AppConstants.BILLBOARD_TYPE;
                }
                break;
        }
    }


    private void initializeUI() {
        mUpdateBtn = (Button)findViewById(R.id.update_element_btn_id);

        nameText = (EditText)findViewById(R.id.update_element_name_text);
        mProgressBar = (ProgressBar)findViewById(R.id.update_progress_bar_id);
        xLocationText = (EditText)findViewById(R.id.update_element_x_text_id);
        yLocationText = (EditText)findViewById(R.id.update_element_y_text_id);

        mType = AppConstants.BILLBOARD_TYPE;
        nameText.setText(mElement.getName());
        mName = mElement.getName();
        x = mElement.getX();
        y = mElement.getY();
        xLocationText.setText(""+mElement.getX());
        yLocationText.setText(""+mElement.getY());

        setListeners();
    }


    private void setListeners() {
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);

                if (!nameText.getText().toString().isEmpty() && !nameText.getText().toString().equals(mName)) {
                    mName = nameText.getText().toString();
                }

                if (!xLocationText.getText().toString().isEmpty()) {
                    x = Double.parseDouble(xLocationText.getText().toString());
                }
                if (!yLocationText.getText().toString().isEmpty()) {
                    y = Double.parseDouble(yLocationText.getText().toString());
                }

                updateElement();
            }
        });
        }





    @Override
    public void onBackPressed() {
        goToManagerActivity();

    }

    private void updateElement() {

        Log.i("UPDATEXXX" , "user P = " + mUser.getPlayground() +" userEmail=" + mUser.getEmail() + ", Ele P= " + mElement.getPlayground() + " , Ele ID= " + mElement.getId());
        String url = AppConstants.HOST + AppConstants.HTTP_ELEMENT + mUser.getPlayground() + "/" + mUser.getEmail() +"/"+ mElement.getPlayground() + "/" + mElement.getId();
        JSONObject jsonObject = getJSONObject();
        mHandler.putRequest(url , "updateElement",  jsonObject);
    }


    private void goToManagerActivity() {
        Intent intent = new Intent(UpdateElementActivity.this , ManagerMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , mUser);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public JSONObject getJSONObject() {
        Map<String, Object> attributes = new HashMap<>();
        if (mType.equals(AppConstants.MOVIE_TYPE)) {
            attributes.put("image" , AppConstants.MOVIE_IMAGE_URL);
        } else if (mType.equals(AppConstants.BOOK_TYPE)) {
            attributes.put("image" , AppConstants.BOOK_IMAGE_URL);
        } else {
            attributes.put("image" , AppConstants.BILLBOARD_IMAGE_URL);
        }

        JSONObject jsonObject = new JSONObject();
        JSONObject locationObject = new JSONObject();
        try {
            jsonObject.put("id" , mElement.getId());
            jsonObject.put("playground" , mElement.getPlayground());
            jsonObject.put("creatorPlayground" , mUser.getPlayground());
            jsonObject.put("name", mName);
            jsonObject.put("type", mType);
            locationObject.put("x" , x);
            locationObject.put("y" , y);
            jsonObject.put("location" , locationObject);
            JSONObject json = new JSONObject(attributes);
            jsonObject.put("attributes",json );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    @Override
    public void onSuccess(final String myResponse, String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UpdateElementActivity.this , "updated successfully!" , Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
                goToManagerActivity();
            }
        });
    }

    @Override
    public void onFailed(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(UpdateElementActivity.this , error , Toast.LENGTH_LONG).show();
            }
        });
    }
}
