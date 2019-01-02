package com.example.ran.ratingplayground_client.activities.manager;

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
import com.example.ran.ratingplayground_client.model.ElementTO;
import com.example.ran.ratingplayground_client.model.UserTO;
import com.example.ran.ratingplayground_client.utils.AppConstants;
import com.example.ran.ratingplayground_client.utils.HttpRequestsHandler;
import com.example.ran.ratingplayground_client.utils.InputValidation;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateElementActivity extends AppCompatActivity implements HttpRequestsHandler.ResponseListener{
    private UserTO mUser;
    private ElementTO mElement;
    private ProgressBar mProgressBar;
    private Date mExpirationDate;
    private HttpRequestsHandler mHandler;

    private Button mUpdateBtn;
    private String mType , mDescription , mName;
    private EditText descriptionText, nameText , expirationDateText,xLocationText, yLocationText;;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_element);
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
//        descriptionText = (EditText)findViewById(R.id.element_description_text_id);

        nameText = (EditText)findViewById(R.id.update_element_name_text);
        mProgressBar = (ProgressBar)findViewById(R.id.update_progress_bar_id);
        expirationDateText = (EditText)findViewById(R.id.update_element_expiration_date_text_id);
        xLocationText = (EditText)findViewById(R.id.update_element_x_text_id);
        yLocationText = (EditText)findViewById(R.id.update_element_y_text_id);

        nameText.setText(mElement.getName());
        expirationDateText.setText(mElement.getExpirationDate().toString());
        xLocationText.setText(""+mElement.getX());
        yLocationText.setText(""+mElement.getY());

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
                new DatePickerDialog(UpdateElementActivity.this , date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName = nameText.getText().toString();
                mDescription = descriptionText.getText().toString();

                boolean isValid = validateUserInput();
                if (isValid){
                    mProgressBar.setVisibility(View.VISIBLE);
                    updateElement(mElement);
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


    @Override
    public void onBackPressed() {
        goToManagerActivity();

    }

    private void updateElement(ElementTO e) {

        e.setType(mType);
        e.setName(mName);
        e.setCreatorEmail(mUser.getEmail());
        e.setExpirationDate(mExpirationDate);
        e.setLocation(0,0);
        e.setCreatorPlayground(mUser.getPlayground());
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("description",mDescription);


        String url = AppConstants.HOST + AppConstants.HTTP_ELEMENT + mUser.getPlayground() + "/" + mUser.getEmail() +"/"+ e.getPlayground() + "/" + e.getId();
        JSONObject jsonObject = e.toJson();
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
