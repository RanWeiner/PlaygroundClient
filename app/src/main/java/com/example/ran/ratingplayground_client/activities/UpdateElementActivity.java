package com.example.ran.ratingplayground_client.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.model.Element;
import com.example.ran.ratingplayground_client.model.User;
import com.example.ran.ratingplayground_client.utils.AppConstants;

public class UpdateElementActivity extends AppCompatActivity {
    private User mUser;
    private Element mElement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_element);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (User)bundle.getSerializable(AppConstants.USER);
        mElement = (Element)bundle.getSerializable(AppConstants.ELEMENT);

        Toast.makeText(UpdateElementActivity.this , "Updates in element id = " + mElement.getId() +
        " type = " + mElement.getType() + " name = " +mElement.getName() , Toast.LENGTH_LONG).show();
    }


    @Override
    public void onBackPressed() {
        goToManagerActivity();

    }




    private void goToManagerActivity() {
        Intent intent = new Intent(UpdateElementActivity.this , ManagerMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , mUser);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
