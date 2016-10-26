package com.soundrecognition.musicmatcher;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SongListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionManager sessionUsername, sessionUserDetails, sessionSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        Log.d("TAGH2","");

        sessionUsername = new SessionManager(getApplicationContext(), SessionManager.Preference.USERNAME);
        sessionUserDetails = new SessionManager(getApplicationContext(), SessionManager.Preference.USERDETAILS);
        sessionSongs = new SessionManager(getApplicationContext(), SessionManager.Preference.SONGS);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);

        String username = sessionUserDetails.getUserDetails().get("username");
        String email = sessionUserDetails.getUserDetails().get("email");

        View nav_header = navigationView.getHeaderView(0);
        ((TextView) nav_header.findViewById(R.id.username)).setText(username);
        ((TextView) nav_header.findViewById(R.id.email)).setText(email);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sessionSongs.getSongs());
        ListView list = (ListView) findViewById(R.id.list_songs);
        list.setAdapter(adapter);
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

    public void navigatetoUploadaSongActivity() {
        Intent profileIntent = new Intent(getApplicationContext(), UploadSongActivity.class);
        startActivity(profileIntent);
        finish();
    }

    public void navigatetoAboutActivity() {
        Intent profileIntent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(profileIntent);
    }
}
