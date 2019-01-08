package com.example.ran.ratingplayground_client.activities.player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.activities.manager.CreateElementActivity;
import com.example.ran.ratingplayground_client.model.ActivityTO;
import com.example.ran.ratingplayground_client.model.ElementTO;
import com.example.ran.ratingplayground_client.model.UserTO;
import com.example.ran.ratingplayground_client.utils.AppConstants;
import com.example.ran.ratingplayground_client.utils.HttpRequestsHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillboardElementActivity extends AppCompatActivity implements HttpRequestsHandler.ResponseListener{
    public static final int MAX_POST_PER_PAGE = 10;
    private List<ActivityTO> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BillboardAdapter mAdapter;
    private TextView pageNumberTextView;
    private UserTO mUser;
    private ElementTO mElement;
    private HttpRequestsHandler mHandler;
    private ImageButton mAddPostBtn , mLeftPageBtn , mRightPageBtn;
    private ActivityTO mPost , mRead;
    private int mCurrentPage;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billboard_element);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (UserTO)bundle.getSerializable(AppConstants.USER);
        mElement =(ElementTO)bundle.getSerializable(AppConstants.ELEMENT);
        mCurrentPage = 0;
        initializeUI();
        mHandler = HttpRequestsHandler.getInstance();
        mHandler.setResponseListener(this);
    }


    private void initializeUI() {
        mAddPostBtn = (ImageButton)findViewById(R.id.add_post_btn_id);
        mLeftPageBtn = (ImageButton)findViewById(R.id.left_page_btn_id);
        mRightPageBtn = (ImageButton)findViewById(R.id.right_page_btn_id);
        progressBar = (ProgressBar)findViewById(R.id.billboard_progress_bar_id);
        pageNumberTextView = (TextView) findViewById(R.id.page_number_txt);
        recyclerView = (RecyclerView) findViewById(R.id.billboar_recycler_view);

        pageNumberTextView.setText("" + (mCurrentPage + 1) + "/" + MAX_POST_PER_PAGE);
        mAdapter = new BillboardAdapter(postList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        setListeners();
    }

    private void setListeners() {
        mRightPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postList.size() < MAX_POST_PER_PAGE) {
                    Toast.makeText(BillboardElementActivity.this , "No more messages" , Toast.LENGTH_SHORT).show();
                    return;
                }
                mCurrentPage++;
                pageNumberTextView.setText("" + (mCurrentPage + 1) + "/" + MAX_POST_PER_PAGE);
                fetchPosts();
            }
        });

        mAddPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeAndUploadPost();
            }
        });

        mLeftPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage <= 0) {
                    Toast.makeText(BillboardElementActivity.this , "You are in the first page", Toast.LENGTH_SHORT).show();
                    return;
                }
                mCurrentPage--;
                pageNumberTextView.setText("" + (mCurrentPage+1) + "/" + MAX_POST_PER_PAGE);
                fetchPosts();
            }
        });


    }

    private void writeAndUploadPost() {
        final EditText input = new EditText(BillboardElementActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BillboardElementActivity.this);
        alertDialog.setTitle("NEW POST")
                .setMessage("Write your post")
                .setView(input)
                .setIcon(R.drawable.ic_edit)
                .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String postText = input.getText().toString();
                        if (!postText.isEmpty()) {
                            uploadPost(postText);
                        }
                    }
                });
        alertDialog.show();
    }

    private void uploadPost(String postText) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AppConstants.MESSAGE , postText);

        mPost = new ActivityTO(AppConstants.PLAYGROUND, mElement.getPlayground(), mElement.getId(), AppConstants.TYPE_POST_MESSAGE,
                mUser.getPlayground(), mUser.getEmail(),attributes);

        String url = AppConstants.HOST + AppConstants.HTTP_ACTIVITIES + mUser.getPlayground() + "/" + mUser.getEmail();
        JSONObject jsonObject = mPost.toJson();
        mHandler.postRequest(url ,AppConstants.EVENT_POST_ACTIVITY , jsonObject);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchPosts();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BillboardElementActivity.this, PlayerMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , mUser);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void fetchPosts() {

        progressBar.setVisibility(View.VISIBLE);
        postList.clear();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AppConstants.PAGE, mCurrentPage);
        attributes.put(AppConstants.SIZE, MAX_POST_PER_PAGE);

        mRead = new ActivityTO(AppConstants.PLAYGROUND, mElement.getPlayground(), mElement.getId(), AppConstants.TYPE_READ_MESSAGES,
                mUser.getPlayground(), mUser.getEmail(),attributes);

        JSONObject jsonObject = mRead.toJson();
        String url = AppConstants.HOST + AppConstants.HTTP_ACTIVITIES + mUser.getPlayground() + "/" + mUser.getEmail();

        mHandler.postRequest(url , AppConstants.EVENT_READ_ACTIVITY , jsonObject);
    }

    @Override
    public void onSuccess(final String myResponse, final String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                switch (event) {
                    case AppConstants.EVENT_POST_ACTIVITY:
                        Toast.makeText(BillboardElementActivity.this , "added successfully!" , Toast.LENGTH_SHORT).show();
                        postList.add(0, mPost);
                        fetchPosts();
                        break;

                    case AppConstants.EVENT_READ_ACTIVITY:
                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            bindPosts(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                        break;
                }


            }
        });

    }


    private void bindPosts(JSONObject jsonObject) {
        try {
            JSONObject attributes = jsonObject.getJSONObject(AppConstants.ATTRIBUTES);
            JSONArray arr = attributes.getJSONArray(AppConstants.MESSAGES);

            for (int i = 0 ; i < arr.length() ; i++) {
                JSONObject object = arr.getJSONObject(i);
                ActivityTO activityTO = ActivityTO.fromJson(object);
                postList.add(0,activityTO);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onFailed(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BillboardElementActivity.this , error , Toast.LENGTH_LONG).show();
            }
        });
    }
}
