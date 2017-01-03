package com.abdoosa.zigzagme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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

    private String[] jsonObjects = new String[3], data = {"name", "id"};
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initializing Facebook SDK
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.activity_splash);

        // initiating the views
        initiateViews();

        // working on Facebook login
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("public_profile", "user_friends");

        // we don't show the loginButton if the user is already logged in
        if (isLoggedIn())
            loginButton.setVisibility(View.GONE);

        // working on the animation of the logo
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

        // getting the info from Facebook login
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

    }


    // A method to initiate the views
    private void initiateViews() {
        // we don't want to initiate login_button if the user is already logged in
        if (loginButton == null)
            loginButton = (LoginButton) findViewById(R.id.login_button);

    }

    // A method to check whether the user is logged in or not
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    // A method to move to GameStart Activity
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
