package com.abdoosa.zigzagme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class Splash extends Activity {

    private LoginButton loginButton;
    private ProfilePictureView picId;
    private TextView email, username;
    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                //myName.setText(String.format("Your name: %s \nand your email: %s", profile.getName(), profile.getLinkUri()));
            }
            //goToMainActivity();
        }

        @Override
        public void onCancel() {
            //myName.setText("your name");
        }

        @Override
        public void onError(FacebookException error) {

        }
    };

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

        final ImageView imageView = (ImageView) findViewById(R.id.logo);
        final Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        if (imageView != null) {
            Log.e("Fernou9", "Here I am!!");
            imageView.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
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
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("Main", response.toString());
                                setProfileToView(object);
                                picId.setVisibility(View.VISIBLE);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        goToMenu();
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

            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(Splash.this, "error to Login Facebook", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void onViewCreated(){
        loginButton = (LoginButton) findViewById(R.id.login_button);
        picId = (ProfilePictureView) findViewById(R.id.picId);
        username = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
    }

    private void goToMenu(){
        startActivity(new Intent(this, Menu.class));
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
