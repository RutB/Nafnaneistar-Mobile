package xyz.nafnaneistar.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.nafnaneistar.activities.LoginActivity;
import xyz.nafnaneistar.activities.SwipeActivity;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.model.User;

public class Prefs {
    private SharedPreferences preferences;
    private final String userRef = "UserRef";
    private final String prefContext = "userPref";
    private final String notifyRef = "NotifyRef";
    private Activity context;

    /**
     * We can create a new Prefs object with reference to our activity and it just should work
     * in all activities
     * @param context
     */
    public Prefs(Activity context){
        this.preferences = context.getSharedPreferences(prefContext, Context.MODE_PRIVATE);
        this.context = context;

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
    public void setEnableNotifications(boolean enable) {
        preferences.edit().putBoolean(notifyRef,enable).apply();
    }
    public boolean getEnableNotifications(){
        boolean enabled =  preferences.getBoolean(notifyRef,true);
        return enabled;

    }
    public void CheckLogin(String[] user) {
        if (user.length != 2) {
            Intent i = new Intent(context, LoginActivity.class);
            context.finish();
            context.startActivity(i);
            return;
        }
        else {
            String email = user[0];
            String pass = user[1];
            String loginUrl = ApiController.getDomainURL() + "login/check?email=" + email + "&password=" + pass;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, loginUrl, null,
                    response -> {
                        Gson g = new Gson();
                        User p = g.fromJson(String.valueOf(response), User.class);
                        if (p.getName() != null) {
                            Intent i = new Intent(context, SwipeActivity.class);
                            context.finish();
                            context.startActivity(i);
                        }
                        else {
                            Intent i = new Intent(context, LoginActivity.class);
                            context.finish();
                            context.startActivity(i);
                        }
                    }, error -> {
                Intent i = new Intent(context, LoginActivity.class);
                context.finish();
                context.startActivity(i);
            });
            ApiController.getInstance().addToRequestQueue(jsonObjReq);
        }

    }

    /**
     * A function to get the current loggedin user
     * @return Set<String> containng the users credentials
     */
    public String[] getUser(){
        Set<String> userSet = preferences.getStringSet(userRef, new HashSet<>());
        if(userSet.size() == 0) return new String[0];
        String user  = "";
        String pass = "";
        String test = userSet.toArray()[0].toString();
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(test);
        if(matcher.matches()) {
            user = test;
            pass = userSet.toArray()[1].toString();
        }else {
            pass = test;
            user = userSet.toArray()[1].toString();
        }
        return new String[] {user, pass};
    }

    /**
     * deletes the userRef variable from sharedPrefs and thus the users info
     */
    public void Logout(){
        preferences.edit().remove(userRef).apply();
    }

}
