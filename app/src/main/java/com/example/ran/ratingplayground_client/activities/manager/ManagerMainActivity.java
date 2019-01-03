package com.example.ran.ratingplayground_client.activities.manager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private Button mUpdateUser , mAddElementBtn , mSearchElementsByDist,mSearchElementsByAttribute, mShowAllBtn;
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
        mSearchElementsByDist = (Button)findViewById(R.id.manager_search_elements_by_dist_btn_id);
        mSearchElementsByAttribute = (Button)findViewById(R.id.manager_search_elements_by_attr_btn_id);
        mShowAllBtn = (Button)findViewById(R.id.manager_show_all_elements_btn_id);
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
        String getElementsURL = AppConstants.HOST + AppConstants.HTTP_ELEMENT + mUser.getPlayground() + "/" +mUser.getEmail() + "/" + "all";
        mElements.clear();
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

        mSearchElementsByDist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByDistDialog();
            }
        });

        mSearchElementsByAttribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByAttributesDialog();
            }
        });

        mShowAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchElements();
            }
        });

    }

    private void searchByDistDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_search_by_distance, null);
        final EditText etLatitude = alertLayout.findViewById(R.id.et_latitude);
        final EditText etLongitude = alertLayout.findViewById(R.id.et_longitude);
        final EditText etDistance = alertLayout.findViewById(R.id.et_distance);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Search By Location");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (etLatitude.getText().toString().isEmpty() || etLatitude.getText().toString().isEmpty() || etDistance.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "You must fill all the fields... ", Toast.LENGTH_SHORT).show();
                } else {
                    double latitude = Double.parseDouble(etLatitude.getText().toString());
                    double longitude = Double.parseDouble(etLongitude.getText().toString());
                    double distance = Double.parseDouble(etDistance.getText().toString());
                    searchByDist(latitude , longitude , distance);
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void searchByDist(double x, double y , double distance) {
        String getElementsURL = AppConstants.HOST + AppConstants.HTTP_ELEMENT + mUser.getPlayground() + "/" +mUser.getEmail() + "/near/" +
                x + "/" + y + "/" + distance;
        mElements.clear();
        mHandler.getRequest(getElementsURL , AppConstants.EVENT_GET_ELEMENTS);
    }

    private void searchByAttributesDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_search_by_attributes, null);
        final EditText etAttribute = alertLayout.findViewById(R.id.et_attribute);
        final EditText etValue = alertLayout.findViewById(R.id.et_value);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Info");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String attribute = etAttribute.getText().toString();
                String value = etValue.getText().toString();

                if (attribute.isEmpty() || value.isEmpty()) {
                    Toast.makeText(getBaseContext(), "You must fill all the fields... ", Toast.LENGTH_SHORT).show();
                } else {
                    searchByAttributes(attribute , value);
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void searchByAttributes(String attribute, String value) {
        String getElementsURL = AppConstants.HOST +  AppConstants.HTTP_ELEMENT +  mUser.getPlayground() + "/" +mUser.getEmail() + "/search/" +
                attribute + "/" + value;
        mElements.clear();
        mHandler.getRequest(getElementsURL , AppConstants.EVENT_GET_ELEMENTS);
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
