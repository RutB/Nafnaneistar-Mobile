package xyz.nafnaneistar.controller;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

import xyz.nafnaneistar.activities.LoginActivity;
import xyz.nafnaneistar.activities.SignupActivity;
import xyz.nafnaneistar.activities.SwipeActivity;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.model.User;

/**
 * Singleton Class for contacting the nafnaneistar.xyz API/Server
 * Prevents creating multiple networking objects and queues for traffic
 */
public class ApiController extends Application {
    private static ApiController instance;
    //private static String domainURL = "http://46.22.102.179:7979/";
    private static String domainURL = "http://192.168.1.207:7979/";
    private RequestQueue requestQueue;

    public static final String SHARED_PREFS = "prefsUser";
    public static final String TEXT = "text";

    /**
     * Return the current instance
     */
    public static synchronized ApiController getInstance(){
        return instance;
    }

    /**
     * Checks if we already have a requestQueue, if not we create a new one from the
     * ApplicationContext to keep us from leaking the Activity or BroadcastReceiver if someone
     * passes one in
     * @return requestQueue
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }

    public static String getDomainURL() {
        return domainURL;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
