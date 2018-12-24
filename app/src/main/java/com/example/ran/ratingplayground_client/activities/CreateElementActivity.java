package com.example.ran.ratingplayground_client.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.model.Element;
import com.example.ran.ratingplayground_client.model.User;
import com.example.ran.ratingplayground_client.utils.AppConstants;
import com.example.ran.ratingplayground_client.utils.HttpRequestsHandler;
import com.example.ran.ratingplayground_client.utils.InputValidation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateElementActivity extends AppCompatActivity implements HttpRequestsHandler.ResponseListener{
    private Button mCreateBtn;
    private User mUser;
    private String mType , mDescription , mName;
    private EditText descriptionText, nameText , expirationDateText;
    private ProgressBar mProgressBar;
    private Date mExpirationDate;

    private HttpRequestsHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_element);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (User)bundle.getSerializable(AppConstants.USER);
        mType = AppConstants.BILLBOARD_TYPE;
        initializeUI();

        mHandler = HttpRequestsHandler.getInstance();
        mHandler.setResponseListener(this);
    }



    public void onTypeRadioBtnClicked(View view) {
        boolean checked = ((RadioButton)view).isChecked();

        switch (view.getId()) {
            case R.id.create_book_radio_btn:
                if (checked) {
                    mType = AppConstants.BOOK_TYPE;
                }
                break;

            case R.id.create_movie_radio_btn:
                if (checked) {
                    mType = AppConstants.MOVIE_TYPE;
                }
                break;

            case R.id.create_billboard_radio_btn:
                if (checked) {
                    mType = AppConstants.BILLBOARD_TYPE;
                }
                break;
        }
    }

    private void initializeUI() {
        mCreateBtn = (Button)findViewById(R.id.create_btn_id);
        descriptionText = (EditText)findViewById(R.id.element_description_text_id);
        nameText = (EditText)findViewById(R.id.element_name_text_id);
        mProgressBar = (ProgressBar)findViewById(R.id.create_progress_bar_id);
        expirationDateText = (EditText)findViewById(R.id.element_expiration_date_text_id);
        setListeners();
    }

    private void setListeners() {

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR , year);
                calendar.set(Calendar.MONTH , month);
                calendar.set(Calendar.DAY_OF_MONTH , dayOfMonth);

                mExpirationDate = calendar.getTime();
                expirationDateText.setText(new SimpleDateFormat("yyyy-mm-dd").format(mExpirationDate));
            }
        };

        expirationDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateElementActivity.this , date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName = nameText.getText().toString();
                mDescription = descriptionText.getText().toString();

                boolean isValid = validateUserInput();
                if (isValid){
                    mProgressBar.setVisibility(View.VISIBLE);
                    createNewElement();
                }
            }
        });
    }


    private boolean validateUserInput() {

        //validate title
        if (!InputValidation.validateUserInput(mName)) {
            nameText.setError("Title is required");
            nameText.requestFocus();
            return false;
        }

        //validate date
        if (!InputValidation.validateUserInput(expirationDateText.getText().toString())) {
            expirationDateText.setError("Date is required");
            expirationDateText.requestFocus();
            return false;
        }

        return true;
    }


    private void createNewElement() {

        Element e = new Element();
        e.setType(mType);
        e.setName(mName);
        e.setCreatorEmail(mUser.getEmail());
        e.setExpirationDate(mExpirationDate);
        e.setLocation(0,0);
        e.setCreatorPlayground(mUser.getPlayground());
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("description",mDescription);
        e.setAttributes(attributes);

        String url = AppConstants.HOST + AppConstants.HTTP_ELEMENT + mUser.getPlayground() + "/" + mUser.getEmail();
        JSONObject jsonObject = e.toJson();
        mHandler.postRequest(url , "createNewElement",  jsonObject);
    }


    @Override
    public void onBackPressed() {
        goToManagerMainActivity();
    }

    private void goToManagerMainActivity() {
        Intent intent = new Intent(CreateElementActivity.this , ManagerMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , mUser);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }

    @Override
    public void onSuccess(final String myResponse, String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateElementActivity.this , "created successfully!" , Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
                goToManagerMainActivity();
//                try {
//                    json = new JSONObject(myResponse);
//                    element =  Element.fromJson(json);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    @Override
    public void onFailed(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(CreateElementActivity.this , error , Toast.LENGTH_LONG).show();
            }
        });
    }
}
