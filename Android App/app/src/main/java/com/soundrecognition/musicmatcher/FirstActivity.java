package com.soundrecognition.musicmatcher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class FirstActivity extends Activity {

    SessionManager sessionUsername, sessionUserDetails, sessionSongs;
    TextView uname;
    public static String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_first);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start first activity");
        sessionUsername = new SessionManager(getApplicationContext(), SessionManager.Preference.USERNAME);
        sessionUserDetails = new SessionManager(getApplicationContext(), SessionManager.Preference.USERDETAILS);
        sessionSongs = new SessionManager(getApplicationContext(), SessionManager.Preference.SONGS);

        uname = (TextView) findViewById(R.id.uname);

        if(sessionUsername.checkLogin()) {
            navigatetoLoginActivity();
            return;
        }
        else {
            navigatetoHomeActivity();
            return;
        }
    }

    /**
     * Method which navigates from Login Activity to Home Activity
     */
    public void navigatetoHomeActivity() {
        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

    /**
     * Method which navigates from Login Activity to Home Activity
     */
    public void navigatetoLoginActivity() {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }

    public void logout(View view) {

        sessionUserDetails.removePreferences();
        sessionSongs.removePreferences();
        sessionUsername.logoutUser();
    }
}
