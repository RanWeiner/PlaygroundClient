package com.example.ran.ratingplayground_client.activities.player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ran.ratingplayground_client.R;
import com.example.ran.ratingplayground_client.model.ElementTO;
import com.example.ran.ratingplayground_client.model.UserTO;
import com.example.ran.ratingplayground_client.utils.AppConstants;

public class RankingActivity extends AppCompatActivity {

    public TextView txtView;
    private UserTO mUser;
    private ElementTO mElement;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mUser = (UserTO)bundle.getSerializable(AppConstants.USER);
        mElement =(ElementTO)bundle.getSerializable(AppConstants.ELEMENT);

        txtView = (TextView)findViewById(R.id.textView1); // TextView
        final Button btn1 = (Button) findViewById(R.id.rank_btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowDialog();
            }
        });
    }
    public void ShowDialog()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seek = new SeekBar(this);
        seek.setMax(100);
        popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("Please Select Rank 1-100 ");
        popDialog.setView(seek);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                txtView.setText("Value of : " + progress);

            }
            public void onStartTrackingTouch(SeekBar arg0) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        popDialog.setPositiveButton("OK",
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        popDialog.create();
        popDialog.show();

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
}
