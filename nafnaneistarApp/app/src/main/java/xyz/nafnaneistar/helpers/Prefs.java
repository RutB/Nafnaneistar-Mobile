package xyz.nafnaneistar.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class Prefs {
    private SharedPreferences preferences;
    private final String userRef = "UserRef";

    /**
     * We can create a new Prefs object with reference to our activity and it just should work
     * in all activities
     * @param context
     */
    public Prefs(Activity context){
        this.preferences = context.getPreferences(Context.MODE_PRIVATE);

    }

    /***
     * Saves the users credentials as a String set in the userRef variable at SharedPreferences
     * @param user netfang
     * @param pass lykilorð
     */
    public void saveUser(String user, String pass) {
        Set<String> userSet = new HashSet<>();
        userSet.add(user);
        userSet.add(pass);
        preferences.edit().putStringSet(userRef, userSet).apply();

    }

    /**
     * A function to get the current loggedin user
     * @return Set<String> containng the users credentials
     */
    public Set<String> getUser(){
        return preferences.getStringSet(userRef, new HashSet<>());
    }

    /**
     * deletes the userRef variable from sharedPrefs and thus the users info
     */
    public void Logout(){
        preferences.edit().remove(userRef).apply();
    }

}
