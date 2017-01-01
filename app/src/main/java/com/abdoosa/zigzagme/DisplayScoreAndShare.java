package com.abdoosa.zigzagme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.ProfilePictureView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DisplayScoreAndShare extends Activity {

    TextView username, scoreSetting, myScore;
    ProfilePictureView picId;
    File imagePath;
    ImageView shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initializing Facebook SDK
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.setting_activity);
        // initiating the views
        initiateView();

        // setting the fonts to the textView
        myScore.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Hanken-Book.ttf"));
        scoreSetting.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Hanken-Book.ttf"));
        username.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Hanken-Book.ttf"));

        // getting the array #data passed from the previous activity
        Bundle bundle = getIntent().getExtras();
        String[] data = bundle.getStringArray("key");

        username.setText(data != null ? data[0] : null);
        picId.setPresetSize(ProfilePictureView.NORMAL);
        picId.setProfileId(data != null ? data[1] : null);

        // this is to store count in phones memory
        SharedPreferences keyValues = getApplicationContext().getSharedPreferences("counter", 0);
        scoreSetting.setText("<" + keyValues.getInt("counter", 0) + "/>");

        // taking screenshot and share it on social media
        shareButton = (ImageView) findViewById(R.id.share_btn);
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap);
                shareIt();
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLangDialog();
            }
        });
    }

    // method to initiate views
    public void initiateView() {
        picId = (ProfilePictureView) findViewById(R.id.profile_pic);
        username = (TextView) findViewById(R.id.name);
        scoreSetting = (TextView) findViewById(R.id.score_in_setting);
        myScore = (TextView) findViewById(R.id.my_score);
    }

    // take a screenshot
    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    //then save it in phones memory
    public void saveBitmap(Bitmap bitmap) {
        imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    // then share it on social media
    private void shareIt() {
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "Oleyy...My Highest Score In ZigZagMe";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My ZigZagMe Score");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void showChangeLangDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.editname);

        dialogBuilder.setTitle("Username");
        dialogBuilder.setMessage("Enter Your Name");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edt.getText().toString().length() != 0)
                    username.setText(edt.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
