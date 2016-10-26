package com.soundrecognition.musicmatcher;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class UploadSongActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    SessionManager sessionUsername, sessionUserDetails, sessionSongs;
    Spinner spinner;
    String songToUpload;
    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_song);

        sessionUsername = new SessionManager(getApplicationContext(), SessionManager.Preference.USERNAME);
        sessionUserDetails = new SessionManager(getApplicationContext(), SessionManager.Preference.USERDETAILS);
        sessionSongs = new SessionManager(getApplicationContext(), SessionManager.Preference.SONGS);


        ArrayList<String> files = listOfFiles();
        if (files.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, files);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spinner = (Spinner) findViewById(R.id.spinner);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    songToUpload = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

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
        navigationView.getMenu().getItem(3).setChecked(true);

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
    public void uploadSong(View view) {

        File file = new File("storage/emulated/0/Download", songToUpload);
        RequestParams params = new RequestParams();
        byte[] song = new byte[(int) file.length()];

        try {
            InputStream is = new FileInputStream(file);
            is.read(song);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream toSend = new ByteArrayInputStream(song);
        params.put("file", toSend, songToUpload, "multipart/form-data");

        invokeWebService(params);
    }


    /**
     *
     * @param params
     */
    public void invokeWebService(RequestParams params) {

        prgDialog.show();

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://192.168.0.101:8080/MusicMatcherApp/upload", params , new AsyncHttpResponseHandler() {

            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                prgDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    sessionSongs.addSong(songToUpload.substring(0, songToUpload.length() - 4));
                    if (jsonObject.getBoolean("status")) {
                        Toast.makeText(getApplicationContext(), "Upload done!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Something went wrong at the server end", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     *
     * @return
     */
    public ArrayList<String> listOfFiles() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        ArrayList<String> files = new ArrayList<String>();

        File file = new File("storage/emulated/0/Download");
        if(file.listFiles() != null) {
            for (File f : file.listFiles()) {
                if(f.isFile() && f.getName().contains(".wav")) {
                    files.add(f.getName());
                }
            }
        }
        else {
        }

        return files;
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
            navigatetoHomeActivity();
        } else if (id == R.id.upload) {

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
