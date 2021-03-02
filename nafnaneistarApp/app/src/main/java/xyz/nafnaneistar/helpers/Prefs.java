package xyz.nafnaneistar.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class Prefs {
    private SharedPreferences preferences;
    private final String userRef = "UserRef";

    public Prefs(Activity context){
        this.preferences = context.getPreferences(Context.MODE_PRIVATE);

    }

    public void saveUser(String user, String pass) {
        Set<String> userSet = new HashSet<>();
        userSet.add(user);
        userSet.add(pass);
        preferences.edit().putStringSet(userRef, userSet).apply();

    }

    public Set<String> getUser(){
        return preferences.getStringSet(userRef, new HashSet<>());
    }

    public void Logout(){
        preferences.edit().remove(userRef).apply();
    }

}
