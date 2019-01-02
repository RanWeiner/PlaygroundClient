package com.example.ran.ratingplayground_client.activities.manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.RecyclerViewAdapter;
import com.example.ran.ratingplayground_client.activities.common.LoginActivity;
import com.example.ran.ratingplayground_client.activities.common.UpdateUserActivity;
import com.example.ran.ratingplayground_client.model.ElementTO;
import com.example.ran.ratingplayground_client.model.UserTO;
import com.example.ran.ratingplayground_client.utils.AppConstants;
import com.example.ran.ratingplayground_client.utils.HttpRequestsHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ManagerMainActivity extends AppCompatActivity  implements HttpRequestsHandler.ResponseListener {

    private Button mUpdateUser , mAddElementBtn , mSearchElements;
    private UserTO mUser;
    private List<ElementTO> mElements = new ArrayList<>();
    private RecyclerViewAdapter mAdapter;
    private HttpRequestsHandler mHandler;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (UserTO)bundle.getSerializable(AppConstants.USER);
        mHandler = HttpRequestsHandler.getInstance();
        mHandler.setResponseListener(this);
        initializeUI();

    }



    public void setUpImageGrid(){

        RecyclerView recyclerView = findViewById(R.id.manager_recycler_id);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecyclerViewAdapter(getApplicationContext(), new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onElementClicked(int position) {
                Toast.makeText(ManagerMainActivity.this, "ElementTO: " + mElements.get(position).getType(),Toast.LENGTH_SHORT).show();
                openOptionsDialog( mElements.get(position));

            }
        });

        recyclerView.setAdapter(mAdapter);
    }

    private void openOptionsDialog(final ElementTO element) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(ManagerMainActivity.this , UpdateElementActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(AppConstants.USER , mUser);
                        bundle.putSerializable(AppConstants.ELEMENT , element);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ManagerMainActivity.this);
        builder.setTitle("Update ElementTO?").setMessage("Are you sure you want to update this ElementTO? ")
                .setPositiveButton("Yes", dialogClickListener).show();

    }


    private void initializeUI() {
        mUpdateUser = (Button)findViewById(R.id.manager_update_user_btn_id);
        mAddElementBtn = (Button)findViewById(R.id.create_element_btn_id);
        mSearchElements = (Button)findViewById(R.id.manager_search_elements_btn_id);
        mProgressBar = (ProgressBar)findViewById(R.id.manager_progress_bar_id);
        setUpImageGrid();
        setListeners();
    }




    @Override
    protected void onStart() {
        super.onStart();
        fetchElements();
    }


    private void fetchElements() {
        String getElementsURL = AppConstants.HOST + AppConstants.HTTP_ELEMENT + "/" + mUser.getPlayground() + "/" +mUser.getEmail() + "/" + "all";
        mHandler.getRequest(getElementsURL , AppConstants.EVENT_GET_ELEMENTS);
    }


    private void bindElements(JSONArray jsonArray) {
        try {

            for (int i = 0 ; i < jsonArray.length() ; i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ElementTO e = ElementTO.fromJson(object);
                mElements.add(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter.bindElements(mElements);
    }


    private void setListeners() {
        mUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this , UpdateUserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(AppConstants.USER , mUser);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });


        mAddElementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this , CreateElementActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(AppConstants.USER , mUser);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mSearchElements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }


    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(ManagerMainActivity.this , LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ManagerMainActivity.this);
        builder.setTitle("Log Out").setMessage("Are you sure you want to log out? ")
                .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

    }

    @Override
    public void onSuccess(final String myResponse , final String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
                JSONObject json = null;
                JSONArray arr = null;
                try {
                    arr = new JSONArray(myResponse);
                    bindElements(arr);
                    } catch (JSONException e) {
                    e.printStackTrace();
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
                Toast.makeText(ManagerMainActivity.this , error , Toast.LENGTH_LONG).show();
            }
        });
    }
}
