package com.example.ran.ratingplayground_client.activities.player;

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
import android.widget.TextView;
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

public class PlayerMainActivity extends AppCompatActivity implements HttpRequestsHandler.ResponseListener{
    private Button mUpdateUser , mSearchElements;
    private UserTO mUser;
    private List<ElementTO> mElements = new ArrayList<>();
    private RecyclerViewAdapter mAdapter;
    private HttpRequestsHandler mHandler;
    private ProgressBar mProgressBar;
    private TextView mPlayerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_main);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (UserTO)bundle.getSerializable(AppConstants.USER);
        mHandler = HttpRequestsHandler.getInstance();
        mHandler.setResponseListener(this);
        initializeUI();
    }





    private void openOptionsDialog() {

        CharSequence[] customElementActivities = {"Guess Rank" , "See Rank" , "Post Review" , "See Review"};

        CharSequence[] billboardActivities = {"Post" , "View"};

        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerMainActivity.this);
        builder.setTitle("Player Actions");
        builder.setItems(new CharSequence[]{"Update", "button 2", "button 3", "button 4"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Toast.makeText(PlayerMainActivity.this, "clicked 1", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(PlayerMainActivity.this, "clicked 2", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(PlayerMainActivity.this, "clicked 3", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(PlayerMainActivity.this, "clicked 4", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        builder.create().show();
    }



    public void setUpImageGrid(){

        RecyclerView recyclerView = findViewById(R.id.player_recycler_id);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecyclerViewAdapter(getApplicationContext(), new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onElementClicked(int position) {

                if (mElements.get(position).getType().equals(AppConstants.BILLBOARD_TYPE)) {
                    goToBillboard(position);
                } else {
                    goToRatingActivity(position);
                }


            }
        });

        recyclerView.setAdapter(mAdapter);
    }

    private void goToBillboard(int position) {
        Intent intent = new Intent(PlayerMainActivity.this , BillboardElementActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , mUser);
        bundle.putSerializable(AppConstants.ELEMENT , mElements.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void goToRatingActivity(int position) {
//        Intent intent = new Intent(PlayerMainActivity.this , BillboardElementActivity.class);
//        startActivity(intent);
    }

    private void initializeUI() {
        mUpdateUser = (Button)findViewById(R.id.player_update_user_btn_id);
        mSearchElements = (Button)findViewById(R.id.player_search_elements_btn_id);
        mProgressBar = (ProgressBar)findViewById(R.id.player_progress_bar_id);
        mPlayerPoints = (TextView)findViewById(R.id.player_point_txt);
        mPlayerPoints.setText("Points: " +mUser.getPoints());
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
                ElementTO e = new ElementTO();
                JSONObject object = jsonArray.getJSONObject(i);
                e.setName(object.getString("name"));
                e.setId(object.getString("id"));
                e.setType(object.getString("type"));
                e.setPlayground(object.getString("playground"));
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
                Intent intent = new Intent(PlayerMainActivity.this, UpdateUserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(AppConstants.USER, mUser);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
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
                        Intent intent = new Intent(PlayerMainActivity.this , LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerMainActivity.this);
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
                Toast.makeText(PlayerMainActivity.this , error , Toast.LENGTH_LONG).show();
            }
        });
    }
}
