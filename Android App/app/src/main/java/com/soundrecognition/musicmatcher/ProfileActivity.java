package com.soundrecognition.musicmatcher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager sessionUsername, sessionUserDetails, sessionSongs;
    ProgressDialog prgDialog;
    TextView usernameTV, emailTV, dateCreatedTV;
    EditText passwordET, newPasswordET, confirmNewPasswordET;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionUsername = new SessionManager(getApplicationContext(), SessionManager.Preference.USERNAME);
        sessionUserDetails = new SessionManager(getApplicationContext(), SessionManager.Preference.USERDETAILS);
        sessionSongs = new SessionManager(getApplicationContext(), SessionManager.Preference.SONGS);
        usernameTV = (TextView) findViewById(R.id.uname);
        emailTV = (TextView) findViewById(R.id.email);
        dateCreatedTV = (TextView) findViewById(R.id.date_created);
        usernameTV.setText(sessionUserDetails.getUserDetails().get("username"));
        emailTV.setText(sessionUserDetails.getUserDetails().get("email"));
        dateCreatedTV.setText(sessionUserDetails.getUserDetails().get("dateCreated"));

        passwordET = (EditText) findViewById(R.id.password);
        newPasswordET = (EditText) findViewById(R.id.password_new);
        confirmNewPasswordET = (EditText) findViewById(R.id.password_confirm);

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        username = sessionUserDetails.getUserDetails().get("username");
        String email = sessionUserDetails.getUserDetails().get("email");

        View nav_header = navigationView.getHeaderView(0);
        ((TextView) nav_header.findViewById(R.id.username)).setText(username);
        ((TextView) nav_header.findViewById(R.id.email)).setText(email);

        setupUI(findViewById(R.id.drawer_layout));
    }

    /**
     *
     * @param view
     */
    public void changePassword(View view) {

        RequestParams params = new RequestParams();
        String password = passwordET.getText().toString();
        String password_new = newPasswordET.getText().toString();
        String password_confirm = confirmNewPasswordET.getText().toString();

        if (password_new.equals(password_confirm)) {
            password = MD5Encryption.cryptWithMD5(password);
            password_new = MD5Encryption.cryptWithMD5(password_new);
            params.put("username", username);
            params.put("password", password);
            params.put("newpassword", password_new);
            invokeWebService(params);
        }
        else {
            Toast.makeText(getApplicationContext(), "Passwords don't match!", Toast.LENGTH_LONG).show();
        }

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
                    Utils.hideSoftKeyboard(ProfileActivity.this);
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
     * @param params
     */
    public void invokeWebService(RequestParams params) {

        prgDialog.show();

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.0.101:8080/MusicMatcherApp/changepassword", params , new AsyncHttpResponseHandler() {

            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                prgDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        Toast.makeText(getApplicationContext(), "Password changed!", Toast.LENGTH_LONG).show();
                    }
                    else {
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            navigatetoAboutActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {

        } else if (id == R.id.song_list) {
            navigatetoSongListActivity();
        } else if (id == R.id.compare) {
            navigatetoHomeActivity();
        } else if (id == R.id.upload) {
            navigatetoUploadaSongActivity();
        }  else if (id == R.id.logout) {
            sessionUserDetails.removePreferences();
            sessionSongs.removePreferences();
            sessionUsername.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void navigatetoUploadaSongActivity() {
        Intent profileIntent = new Intent(getApplicationContext(), UploadSongActivity.class);
        startActivity(profileIntent);
        finish();
    }

    public void navigatetoHomeActivity() {
        Intent profileIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(profileIntent);
        finish();
    }

    public void navigatetoSongListActivity() {
        Intent profileIntent = new Intent(getApplicationContext(), SongListActivity.class);
        startActivity(profileIntent);
        finish();
    }

    public void navigatetoAboutActivity() {
        Intent profileIntent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(profileIntent);
    }
}
