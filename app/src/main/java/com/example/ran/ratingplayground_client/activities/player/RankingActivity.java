package com.example.ran.ratingplayground_client.activities.player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.model.ActivityTO;
import com.example.ran.ratingplayground_client.model.ElementTO;
import com.example.ran.ratingplayground_client.model.UserTO;
import com.example.ran.ratingplayground_client.utils.AppConstants;
import com.example.ran.ratingplayground_client.utils.HttpRequestsHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RankingActivity extends AppCompatActivity  implements HttpRequestsHandler.ResponseListener{

    private TextView elementNameTextView;
    private Button rankBtn , showRanking;
    private UserTO mUser;
    private ElementTO mElement;
    private ImageView mElementImageView;
    private HttpRequestsHandler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (UserTO)bundle.getSerializable(AppConstants.USER);
        mElement =(ElementTO)bundle.getSerializable(AppConstants.ELEMENT);
        mElementImageView = (ImageView)findViewById(R.id.element_image_id);
        elementNameTextView = (TextView)findViewById(R.id.element_rank_name_id);
        elementNameTextView.setText(mElement.getName());
        setImage();

        showRanking = (Button)findViewById(R.id.show_ranking_btn);
        showRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRankingDialog();
            }
        });
        rankBtn = (Button) findViewById(R.id.rank_btn);
        rankBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowDialog();
            }
        });
        mHandler = HttpRequestsHandler.getInstance();
        mHandler.setResponseListener(this);
    }

    private void showRankingDialog() {

        ActivityTO rate = new ActivityTO(AppConstants.PLAYGROUND, mElement.getPlayground(), mElement.getId(), AppConstants.TYPE_SHOW_RATING,
                mUser.getPlayground(), mUser.getEmail(),new HashMap<String, Object>());

        String url = AppConstants.HOST + AppConstants.HTTP_ACTIVITIES + mUser.getPlayground() + "/" + mUser.getEmail();
        JSONObject jsonObject = rate.toJson();
        mHandler.postRequest(url ,AppConstants.TYPE_SHOW_RATING , jsonObject);

    }

    private void setImage() {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.image_not_found)
                .priority(Priority.HIGH);

        Glide.with(this).load(mElement.getAttributes().get("image"))
                .apply(options)
                .into(mElementImageView);
    }

    public void ShowDialog()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View viewLayout = inflater.inflate(R.layout.layout_activity_rank_dialog, (ViewGroup)findViewById(R.id.layout_rank_dialog));
        final TextView rankText = (TextView)viewLayout.findViewById(R.id.rank_text_id);

        final SeekBar seek = (SeekBar)viewLayout.findViewById(R.id.rank_seek_bar_id);
        seek.setMax(10);
        popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("Select Rate 1-10");
        popDialog.setView(viewLayout);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                rankText.setText("Rate: " + progress);
            }
            public void onStartTrackingTouch(SeekBar arg0) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        popDialog.setPositiveButton("OK",
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                rateMovie(seek.getProgress());
                dialog.dismiss();
            }
        });

        popDialog.create();
        popDialog.show();

    }

    public void rateMovie(int progress) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AppConstants.RATING , progress);

        ActivityTO rate = new ActivityTO(AppConstants.PLAYGROUND, mElement.getPlayground(), mElement.getId(), AppConstants.TYPE_RATING,
                mUser.getPlayground(), mUser.getEmail(),attributes);

        String url = AppConstants.HOST + AppConstants.HTTP_ACTIVITIES + mUser.getPlayground() + "/" + mUser.getEmail();
        JSONObject jsonObject = rate.toJson();
        mHandler.postRequest(url ,AppConstants.TYPE_RATING , jsonObject);
    }

    @Override
    public void onSuccess(final String myResponse,final String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event.equals(AppConstants.TYPE_RATING)) {
                    handleRateResponse(myResponse);

                } else if (event.equals(AppConstants.TYPE_SHOW_RATING)) {
                    handleShowRatingResponse(myResponse);
                }
            }
        });
    }


        private void handleRateResponse(String myResponse) {
                JSONObject jsonObject = null;
                JSONObject content = null;
                try {
                    jsonObject = new JSONObject(myResponse);
                    JSONObject attributes = jsonObject.getJSONObject(AppConstants.ATTRIBUTES);
                    content = attributes.getJSONObject("content");
                    int points = content.getInt("Points");
                    long totalPoints = content.getLong("TotalPoints");
                    goToEndRatingActivity(points , totalPoints);
                } catch (JSONException e) {
                    String message = "You already ranked this element.";
                    goToFailDialog(message);
                    e.printStackTrace();
                }
    }

    private void handleShowRatingResponse(final String myResponse) {
        JSONObject jsonObject = null;
        JSONObject content = null;
        try {
            jsonObject = new JSONObject(myResponse);
            JSONObject attributes = jsonObject.getJSONObject(AppConstants.ATTRIBUTES);
            content = attributes.getJSONObject("content");
            int rating = content.getInt("Rating");
            double avg = content.getDouble("Average");
            showRankingDetailsDialog(rating , avg);
        } catch (JSONException e) {
            String message = "You must rank the element before.";
            goToFailDialog(message);
            e.printStackTrace();
        }
    }

    private void showRankingDetailsDialog(int rating, double avg) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(RankingActivity.this);
        builder.setTitle("Rating").setMessage("The average rating is: " + String.format("%.2f" , avg)
            + "\nYour rating: " + rating)
                .setPositiveButton("OK", dialogClickListener).show();
    }

    @Override
    public void onBackPressed() {
        goToMainMenu();
    }

    private void goToFailDialog(String message) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(RankingActivity.this);
        builder.setTitle("Oops...").setMessage(message)
                .setPositiveButton("OK", dialogClickListener).show();

    }

    private void goToMainMenu() {
        Intent intent = new Intent(RankingActivity.this , PlayerMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , mUser);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFailed(final String error ,final String event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event.equals(AppConstants.TYPE_RATING)) {
                    String message = "You already ranked this element.";
                    goToFailDialog(message);
                }

                else {
                    String message = "You must rank the element before.";
                    goToFailDialog(message);
                }
            }
        });
    }


    public void goToEndRatingActivity(int points , long totalPoints){
        Intent intent = new Intent(RankingActivity.this , EndRatingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.USER , mUser);
        bundle.putInt("points" , points);
        bundle.putLong("totalPoints" , totalPoints);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

}
