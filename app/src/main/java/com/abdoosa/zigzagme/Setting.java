package com.abdoosa.zigzagme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.ProfilePictureView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class Setting extends Activity {
    TextView username;
    TextView scoreSetting, myScore;

    File imagePath;
    ImageView shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.setting_activity);
        Log.e("lol", "lil");


        ProfilePictureView picId = (ProfilePictureView) findViewById(R.id.profile_pic);
        username = (TextView) findViewById(R.id.name);
        scoreSetting = (TextView) findViewById(R.id.score_in_setting);
        myScore = (TextView) findViewById(R.id.my_score);
        myScore.setTypeface(Typeface.createFromAsset(getAssets(),  "fonts/Hanken-Book.ttf"));
        scoreSetting.setTypeface(Typeface.createFromAsset(getAssets(),  "fonts/Hanken-Book.ttf"));

        Bundle bundle = getIntent().getExtras();
        String[] data = bundle.getStringArray("key");

        username.setText(data != null ? data[0] : null);
        picId.setPresetSize(ProfilePictureView.NORMAL);
        picId.setProfileId(data != null ? data[1] : null);

        SharedPreferences keyValues = getApplicationContext().getSharedPreferences("counter", 0);
        scoreSetting.setText("<" + keyValues.getInt("counter", 0) + "/>");

        shareButton = (ImageView) findViewById(R.id.share_btn);
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Bitmap bitmap = takeScreenshot();
                saveBitmap(bitmap);
                shareIt();
            }
        });

    }

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

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

    private void shareIt() {
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        String shareBody = "My Highest Score In ZigZagMe...Oleyy";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My ZigZagMe Score");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
