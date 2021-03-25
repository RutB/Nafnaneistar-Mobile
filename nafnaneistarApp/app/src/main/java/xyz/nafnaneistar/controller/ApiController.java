package xyz.nafnaneistar.controller;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import xyz.nafnaneistar.activities.items.ComboListItem;
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
    //private static String domainURL = "localhost:7979/";
    // private static String domainURL = "http://127.0.0.1:7979/";
    //private static String domainURL = "http://192.168.0.164:7979/";


    private RequestQueue requestQueue;


    /**
     * Return the current instance to prevent multiple instances
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

    /**
     * makes sure to queue up the requests to maintain a stable connection
     * @param req the jsonobject or array reqyest
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }



    /**
     * make the domainUrl a variable for better convience
     * @return
     */
    public static String getDomainURL() {
        return domainURL;
    }

    /**
     * makes sure that the instance can only be this one
     */
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public void login(VolleyCallBack<User> volleyCallBack, String email, String pass) throws URISyntaxException {
        String listeningPath = "login/check";
        URIBuilder b = null;
        try {
            b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        b.addParameter("email",email);
        b.addParameter("password",pass);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,b.build().toString(),
                null, response -> {
            Gson g = new Gson();
            User p = g.fromJson(String.valueOf(response), User.class);
            volleyCallBack.onResponse(p);
        }, error -> volleyCallBack.onError(getString(R.string.loginError)));
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }
    public void signup(VolleyCallBack<JSONObject> volleyCallBack, String name, String email, String pass) throws URISyntaxException {
        String listeningPath = "signup";
        URIBuilder b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
        b.addParameter("name",name);
        b.addParameter("email",email);
        b.addParameter("password",pass);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,b.build().toString(),
                null, volleyCallBack::onResponse, error -> volleyCallBack.onError(getString(R.string.signupFailed)));
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void getNameCardsAndRating(Long partnerId,String user_email,String pass, VolleyCallBack<ArrayList<ComboListItem>> volleyCallBack) {
        ArrayList<ComboListItem> comboList = new ArrayList<>();
        String listeningPath = "viewliked/combolist";
        URIBuilder b = null;
        try {
            b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        b.addParameter("email", user_email);
        b.addParameter("pass", pass);
        b.addParameter("pid", String.valueOf(partnerId));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, b.toString(), null,
                response -> {
                    JSONArray namecards = null;
                    try {
                        namecards = response.getJSONArray("namecards");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < namecards.length(); i++) {
                        try {
                            JSONObject nc = (JSONObject) namecards.get(i);
                            comboList.add(new ComboListItem(
                                    nc.getInt("id"),
                                    nc.getString("name"),
                                    nc.getInt("rating"),
                                    nc.getInt("gender")
                            ));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    volleyCallBack.onResponse((ArrayList<ComboListItem>) comboList);
                }, error -> {
                volleyCallBack.onError( getString(R.string.errorGettingPartners));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }


}
