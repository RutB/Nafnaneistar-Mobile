package xyz.nafnaneistar.controller;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton Class for contacting the nafnaneistar.xyz API/Server
 * Prevents creating multiple networking objects and queues for traffic
 */
public class ApiController extends Application {
    private static ApiController instance;
    //private static String domainURL = "http://46.22.102.179:7979/";
    private static String domainURL = "http://192.168.1.207:7979/";
    private RequestQueue requestQueue;

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
