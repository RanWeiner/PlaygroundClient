package com.example.ran.ratingplayground_client.activities.player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    private List<ActivityTO> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BillboardAdapter mAdapter;
    private UserTO mUser;
    private ElementTO mElement;
    private HttpRequestsHandler mHandler;
    private Button mAddPostBtn;
    private ActivityTO mPost , mRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billboard_element);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (UserTO)bundle.getSerializable(AppConstants.USER);
        mElement =(ElementTO)bundle.getSerializable(AppConstants.ELEMENT);
        mHandler = HttpRequestsHandler.getInstance();
        mHandler.setResponseListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.billboar_recycler_view);

        mAdapter = new BillboardAdapter(postList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mAddPostBtn = (Button)findViewById(R.id.add_post_btn_id);
        mAddPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeAndUploadPost();
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
        Date date = new Date();
        String year = "" + date.getYear();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("year",year);
        attributes.put("post" , postText);
        attributes.put("user" , mUser.getUsername());

        mPost = new ActivityTO(AppConstants.PLAYGROUND, null, mElement.getPlayground(), mElement.getId(), AppConstants.TYPE_POST_MESSAGE,
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

        mRead = new ActivityTO(AppConstants.PLAYGROUND, null, mElement.getPlayground(), mElement.getId(), AppConstants.TYPE_READ_MESSAGES,
                mUser.getPlayground(), mUser.getEmail(),null);

        String url = AppConstants.HOST + AppConstants.HTTP_ACTIVITIES + mUser.getPlayground() + "/" + mUser.getEmail();
        mHandler.getRequest(url , AppConstants.EVENT_READ_ACTIVITY);
//        ActivityEntity post;
//        post = new ActivityEntity("Aliens", "Science Fiction", "1986");
//        postList.add(post);
//
//        post = new ActivityEntity("Chicken Run", "Animation", "2000");
//        postList.add(post);
//
//        post = new ActivityEntity("Back to the Future", "Science Fiction", "1985");
//        postList.add(post);
//
//        post = new ActivityEntity("Raiders of the Lost Ark", "Action & Adventure", "1981");
//        postList.add(post);


    }

    @Override
    public void onSuccess(final String myResponse, final String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                switch (event) {
                    case AppConstants.EVENT_POST_ACTIVITY:
                        Toast.makeText(BillboardElementActivity.this , "added successfully!" , Toast.LENGTH_SHORT).show();
                        postList.add(0, mPost);
                        mAdapter.notifyDataSetChanged();
                        break;

                    case AppConstants.EVENT_READ_ACTIVITY:
                        JSONObject json = null;
                        JSONArray arr = null;
                        try {
                            arr = new JSONArray(myResponse);
                            bindPosts(arr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                        break;
                }


            }
        });
    }


    private void bindPosts(JSONArray arr) {
        try {
            for (int i = 0 ; i < arr.length() ; i++) {
                JSONObject object = arr.getJSONObject(i);
                ActivityTO activityTO = ActivityTO.fromJson(object);
                postList.add(activityTO);
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
