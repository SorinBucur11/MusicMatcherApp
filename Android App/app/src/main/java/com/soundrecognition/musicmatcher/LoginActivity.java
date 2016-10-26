package com.soundrecognition.musicmatcher;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.*;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

public class LoginActivity extends Activity {

    ProgressDialog prgDialog;
    TextView errorMessage;
    EditText usernameET;
    EditText passwordET;
    SessionManager sessionUsername, sessionUserDetails, sessionSongs;
    public static String TAG = "tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        Log.d("INTRAT", "in login activity");

        sessionUsername = new SessionManager(getApplicationContext(), SessionManager.Preference.USERNAME);
        sessionUserDetails = new SessionManager(getApplicationContext(), SessionManager.Preference.USERDETAILS);
        sessionSongs = new SessionManager(getApplicationContext(), SessionManager.Preference.SONGS);
        errorMessage = (TextView)findViewById(R.id.login_error);
        usernameET = (EditText)findViewById(R.id.loginUser);
        passwordET = (EditText)findViewById(R.id.loginPassword);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        setupUI(findViewById(R.id.loginactivity));
    }


    /**
     *
     * @param view
     */
    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    Utils.hideSoftKeyboard(LoginActivity.this);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    /**
     *
     * @param view
     */
    public void loginUser(View view) {

        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        RequestParams params = new RequestParams();
        password = MD5Encryption.cryptWithMD5(password);

        if (Utils.isNotNull(username) && Utils.isNotNull(password)) {
            params.put("username", username);
            params.put("password", password);
            invokeWS(params, username);
        } else {
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }
    }

    /**
     *
     * @param params
     */
    public void invokeWS(RequestParams params, final String username) {

        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.0.101:8080/MusicMatcherApp/login/loginuser", params , new AsyncHttpResponseHandler() {

            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                prgDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                        sessionUsername.createUserLoginSession(username);
                        invokeHomeWS(username);
                        navigatetoHomeActivity();
                        clearValues();
                    }
                    else {
                        clearValues();
                        //errorMessage.setText(obj.getString("error_message"));
                        Toast.makeText(getApplicationContext(), jsonObject.getString("error_message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }
            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     *
     */
    public void clearValues(){
        passwordET.getText().clear();
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
     *
     * @param view
     */
    public void navigatetoRegisterActivity(View view) {
        Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerIntent);
    }

    /**
     *
     * @param view
     */
    public void navigatetoRecoverPasswordActivity(View view) {
        Intent recoverIntent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
        recoverIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(recoverIntent);
    }


    /**
     *
     * @param username
     */
    public void invokeHomeWS(String username) {

        RequestParams params = new RequestParams();
        params.put("username", username);
        prgDialog.show();

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.0.101:8080/MusicMatcherApp/home", params , new AsyncHttpResponseHandler() {

            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                prgDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        Log.d(TAG, "inaine de deconstruct");
                        boolean operationJSON = deconstructJSON(jsonObject);
                    }
                    else {
                        clearValues();
                        Toast.makeText(getApplicationContext(), jsonObject.getString("error_message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }
            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public boolean deconstructJSON(JSONObject jsonObject) {

        boolean resultJSON = false;
        ArrayList<String> songs = new ArrayList<String>();
        JSONObject jsonObjectUserDetails = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            jsonObjectUserDetails = jsonObject.getJSONObject("user_details");
            String username = jsonObjectUserDetails.getString("username");
            String email = jsonObjectUserDetails.getString("email");
            String dateCreated = jsonObjectUserDetails.getString("dateCreated");
            sessionUserDetails.createUserDetailsSession(username, email, dateCreated);
            Log.d(TAG, "user details called from deconstruct" + username);

            jsonArray = jsonObject.getJSONArray("songs");
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObjectSongs = jsonArray.getJSONObject(i);
                songs.add(jsonObjectSongs.getString("name"));
            }
            sessionSongs.createSongsSession(songs);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultJSON;
    }

}

