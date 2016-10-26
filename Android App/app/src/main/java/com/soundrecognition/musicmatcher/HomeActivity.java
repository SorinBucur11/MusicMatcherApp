package com.soundrecognition.musicmatcher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    SessionManager sessionUsername, sessionUserDetails, sessionSongs;
    Spinner spinner_song1, spinner_song2;
    TextView resultTV;
    String firstSongToCompare, secondSongToCompare;
    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionUsername = new SessionManager(getApplicationContext(), SessionManager.Preference.USERNAME);
        sessionUserDetails = new SessionManager(getApplicationContext(), SessionManager.Preference.USERDETAILS);
        sessionSongs = new SessionManager(getApplicationContext(), SessionManager.Preference.SONGS);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sessionSongs.getSongs());
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_song1 = (Spinner) findViewById(R.id.spinner_song1);
        spinner_song1.setAdapter(adapter1);

        spinner_song1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstSongToCompare = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sessionSongs.getSongs());
        adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_song2 = (Spinner) findViewById(R.id.spinner_song2);
        spinner_song2.setAdapter(adapter2);

        spinner_song2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondSongToCompare = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        navigationView.getMenu().getItem(0).setChecked(true);

        String username = sessionUserDetails.getUserDetails().get("username");
        String email = sessionUserDetails.getUserDetails().get("email");

        View nav_header = navigationView.getHeaderView(0);
        ((TextView) nav_header.findViewById(R.id.username)).setText(username);
        ((TextView) nav_header.findViewById(R.id.email)).setText(email);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    }

    public void onNothingSelected(AdapterView parent) {
    }

    /**
     *
     * @param view
     */
    public void compareSongs(View view) {

        RequestParams params = new RequestParams();
        params.put("firstSong", firstSongToCompare);
        params.put("secondSong", secondSongToCompare);
        invokeWebService(params);
    }

    /**
     *
     * @param params
     */
    public void invokeWebService(RequestParams params) {

        prgDialog.show();
        resultTV = (TextView) findViewById(R.id.result);

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.0.101:8080/MusicMatcherApp/compare", params , new AsyncHttpResponseHandler() {

            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                prgDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        resultTV.setText("The songs match " + jsonObject.getString("number") + "%");
                        Toast.makeText(getApplicationContext(), "Analyzing done!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        //Toast.makeText(getApplicationContext(), jsonObject.getString("error_message"), Toast.LENGTH_LONG).show();
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
            navigatetoProfileActivity();
        } else if (id == R.id.song_list) {
            navigatetoSongListActivity();
        } else if (id == R.id.compare) {

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

    public void navigatetoProfileActivity() {
        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(profileIntent);
    }

    public void navigatetoUploadaSongActivity() {
        Intent profileIntent = new Intent(getApplicationContext(), UploadSongActivity.class);
        startActivity(profileIntent);
    }

    public void navigatetoSongListActivity() {
        Intent profileIntent = new Intent(getApplicationContext(), SongListActivity.class);
        startActivity(profileIntent);
    }

    public void navigatetoAboutActivity() {
        Intent profileIntent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(profileIntent);
    }

}
