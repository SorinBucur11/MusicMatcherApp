package com.soundrecognition.musicmatcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sorin on 6/7/2016.
 */
public class SessionManager {

    SharedPreferences preferences;
    Editor editor;
    Context context;
    public static String TAG = "tag";

    public enum Preference {
        USERNAME, USERDETAILS, SONGS
    }

    private static final String PREFERENCE_FILE_USERNAME = "Username";
    private static final String PREFERENCE_FILE_USERDETAILS = "UserDetails";
    private static final String PREFERENCE_FILE_SONGS = "Songs";

    /**
     *
     * @param context
     * @param preferenceType
     */
    public SessionManager(Context context, Preference preferenceType) {
        this.context = context;
        if (preferenceType == Preference.USERNAME) {
            preferences = context.getSharedPreferences(PREFERENCE_FILE_USERNAME, 0);
        }
        else if (preferenceType == Preference.USERDETAILS) {
            preferences = context.getSharedPreferences(PREFERENCE_FILE_USERDETAILS, 0);
            Log.d(TAG,"IN PREF USER DETAILS");
        }
        else
            preferences = context.getSharedPreferences(PREFERENCE_FILE_SONGS, 0);
        editor = preferences.edit();
    }

    /**
     *
     * @param username
     */
    public void createUserLoginSession(String username) {

        editor.putBoolean("Logged in", true);
        editor.putString("username", username);
        editor.commit();
    }

    /**
     *
     * @param username
     * @param email
     * @param dateCreated
     */
    public void createUserDetailsSession(String username, String email, String dateCreated) {

        Log.d(TAG, "in create user details " + username);
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("dateCreated", dateCreated);
        editor.commit();

    }

    /**
     *
     * @param songs
     */
    public void createSongsSession(ArrayList<String> songs) {

        editor.putInt("numberOfSongs", songs.size());
        for (int i = 0; i < songs.size(); i++) {
            editor.putString("song" + i, songs.get(i));
        }
        editor.commit();
    }


    /**
     *
     * @param songName
     */
    public void addSong(String songName) {

        int numberOfSongs = preferences.getInt("numberOfSongs", 0);
        editor.putInt("numberOfSongs", numberOfSongs + 1);
        editor.putString("song" + numberOfSongs, songName);
        editor.commit();
    }

    public boolean checkLogin() {

        if(!this.isUserLoggedIn()) {

            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            return true;
        }
        return false;
    }

    public String getUsername() {
        return preferences.getString("username", null);
    }

    public HashMap<String, String> getUserDetails() {

        HashMap<String, String> userDetails = new HashMap<String, String>();
        Log.d(TAG, "in get hash " + preferences.getString("username", null));
        userDetails.put("username", preferences.getString("username", null));
        userDetails.put("email", preferences.getString("email", null));
        userDetails.put("dateCreated", preferences.getString("dateCreated", null));

        return userDetails;
    }

    public ArrayList<String> getSongs() {

        ArrayList<String> songs = new ArrayList<String>();
        int numberOfSongs = preferences.getInt("numberOfSongs", 0);

        for (int i = 0; i < numberOfSongs; i++) {
            songs.add(preferences.getString("song" + i, null));
        }

        return songs;
    }

    public void logoutUser() {

        editor.clear();
        editor.commit();

        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public void removePreferences() {

        editor.clear();
        editor.commit();

    }


    // Check for login
    public boolean isUserLoggedIn(){
        return preferences.getBoolean("Logged in", false);
    }

}
