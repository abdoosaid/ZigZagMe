package com.abdoosa.zigzagme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class Splash extends Activity {

    String[] jsonObjects = new String[3];
    String[] data = {"name", "id"};
    TextView textView;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.activity_splash);

        onViewCreated();

        callbackManager = CallbackManager.Factory.create();
        String[] permissions = {"public_profile", "user_friends"};
        loginButton.setReadPermissions(permissions);

        if (isLoggedIn())
            loginButton.setVisibility(View.GONE);

        final ImageView logo = (ImageView) findViewById(R.id.logo);
        final Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.scale);


        if (logo != null) {
            logo.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (isLoggedIn()) {
                        loginButton.setVisibility(View.GONE);

                        // getting the stored data
                        SharedPreferences keyValues = getApplicationContext().getSharedPreferences("jsonObjects", 0);
                        for (int i = 0; i < data.length; i++)
                            jsonObjects[i] = keyValues.getString(data[i], "Data Not Found");

                        // moving to GameStart class
                        goToGameStart(jsonObjects);

                    } else
                        loginButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // storing data
                                try {
                                    jsonObjects = new String[]{object.getString("name"), object.getString("id")};

                                    SharedPreferences keyValues = getApplicationContext().getSharedPreferences("jsonObjects", 0);
                                    SharedPreferences.Editor keyValuesEditor = keyValues.edit();
                                    for (int i = 0; i < data.length; i++)
                                        keyValuesEditor.putString(data[i], jsonObjects[i]);
                                    keyValuesEditor.apply();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                loginButton.setVisibility(View.GONE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        goToGameStart(jsonObjects);
                                    }
                                }, 2000);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("cancel", "lol");
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(Splash.this, "error to Login Facebook", Toast.LENGTH_SHORT).show();
            }
        });

        textView = (TextView) findViewById(R.id.terms);
        textView.setTextColor(Color.BLACK);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void displayTerms(View view) {
        SpannableString ss = new SpannableString("By signing with Facebook, you agree on Terms of Use");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                //startActivity(new Intent(MyActivity.this, NextActivity.class));
                Log.e("displayTerms", "lol");
                Toast.makeText(getApplicationContext(), "looool", Toast.LENGTH_LONG).show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 39, 51, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.RED);
    }

    private void onViewCreated() {
        if (loginButton == null)
            loginButton = (LoginButton) findViewById(R.id.login_button);

    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void goToGameStart(String[] data) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, GameStart.class);
        bundle.putStringArray("key", data);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getApplication());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppEventsLogger.activateApp(getApplication());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
