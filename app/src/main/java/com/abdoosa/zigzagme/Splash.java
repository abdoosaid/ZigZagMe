package com.abdoosa.zigzagme;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.data;
import static java.security.AccessController.getContext;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Splash extends Activity {

    private JSONObject response, profile_pic_data, profile_pic_url;
    String[] jsonObjects = new String[3];
    String[] data = {"email", "name", "id"};
    boolean isLogged;

    private LoginButton loginButton;
    private ProfilePictureView picId;
    private TextView email, username;
    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.activity_splash);

        onViewCreated();

        callbackManager = CallbackManager.Factory.create();
        String[] permissions = {"public_profile", "user_friends", "email"};
        loginButton.setReadPermissions(permissions);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("Main", response.toString());
                                try {
                                    jsonObjects = new String[]{object.getString("email"), object.getString("name"), object.getString("id")};
                                    SharedPreferences keyValues = getApplicationContext().getSharedPreferences("jsonObjects", 0);
                                    SharedPreferences.Editor keyValuesEditor = keyValues.edit();
                                    for (int i = 0; i<data.length; i++)
                                        keyValuesEditor.putString(data[i], jsonObjects[i]);
                                    keyValuesEditor.apply();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //setProfileToView(object);
                                //setUserProfile(object.toString());
                                loginButton.setVisibility(View.GONE);
                                isLogged = true;
                                //picId.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        goToMenu(jsonObjects);
                                    }
                                }, 2000);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
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

        final ImageView imageView = (ImageView) findViewById(R.id.logo);
        final Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        if (imageView != null) {
            imageView.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (isLoggedIn()) {

                        loginButton.setVisibility(View.GONE);
                        SharedPreferences keyValues = getApplicationContext().getSharedPreferences("jsonObjects", 0);
                        for (int i = 0; i < data.length; i++)
                            jsonObjects[i] = keyValues.getString(data[i], "Data Not Found");
                        goToMenu(jsonObjects);
                    }

                    loginButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }



    }

    private void onViewCreated() {
        if (loginButton == null)
            loginButton = (LoginButton) findViewById(R.id.login_button);
        if (picId == null)
            picId = (ProfilePictureView) findViewById(R.id.picId);
        if (username == null)
            username = (TextView) findViewById(R.id.username);
        if (email == null)
            email = (TextView) findViewById(R.id.email);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    private void goToMenu(String[] data) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, GameStart.class);
        bundle.putStringArray("key", data);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setProfileToView(JSONObject jsonObject) {
        try {
            email.setText(jsonObject.getString("email"));
            username.setText(jsonObject.getString("name"));

            picId.setPresetSize(ProfilePictureView.NORMAL);
            picId.setProfileId(jsonObject.getString("id"));
            //infoLayout.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void setUserProfile(String jsondata) {
//        try {
//            response = new JSONObject(jsondata);
//            email.setText(response.get("email").toString());
//            username.setText(response.get("name").toString());
//            if (response.has("picture")) {
//                profile_pic_data = new JSONObject(response.get("picture").toString());
//                Log.e("7mar", "lol");
//                profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
//                Picasso.with(this).load(profile_pic_url.getString("url"))
//                        .into(picId);
//            }
//            else
//                Log.e("else", "lol");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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
