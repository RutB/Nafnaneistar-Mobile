package xyz.nafnaneistar.controller;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import androidx.core.app.NotificationManagerCompat;
import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.activities.items.UserItem;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.model.NameCard;
import xyz.nafnaneistar.model.User;

/**
 * Singleton Class for contacting the nafnaneistar.xyz API/Server
 * Prevents creating multiple networking objects and queues for traffic
 */
public class ApiController extends NotificationController {
    private static ApiController instance;
    private NotificationManagerCompat notificationManager;
    static String domainURL = "http://46.22.102.179:7979/";
    //private static String domainURL = "http://192.168.1.207:7979/";
    // private static String domainURL = "localhost:7979/";
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

        notificationManager = NotificationManagerCompat.from(this);

    }

    /**
     * Checks if user has some notifications and notifies if there are any
    **/
    public void checkNotifications(Activity context) throws URISyntaxException {
        Prefs prefs = new Prefs(context);
        String [] user = prefs.getUser();
        if(!prefs.getEnableNotifications())
            return;
        if(user.length ==0){
            return;
        }
        String email = user[0];
        String pass = user[1];
        String listeningPath = "notify";
        URIBuilder b = null;
        try {
            b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        b.addParameter("email",email);
        b.addParameter("pass",pass);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,b.build().toString(),
                null, response -> {
            if(response.has(NotificationController.Name_Notification_Channel)){
                try {
                    Notification notification = NotificationController.createApprovedNameNotification(context,response.getString(NotificationController.Name_Notification_Channel));
                    notificationManager.notify(1,notification);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(response.has(NotificationController.Partner_Notification_Channel)){
                try {
                    Notification notification = NotificationController.createPartnerNotification(context,response.getString(NotificationController.Partner_Notification_Channel));
                    notificationManager.notify(2,notification);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error ->{
            Log.d("notify", "checkNotifications: "+ error.getMessage());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Tries to log in with the given emal and password
     * @param volleyCallBack what to do on error or response
     * @param email email of the user trying to log in
     * @param pass password of the user trying to log in
     * @throws URISyntaxException
     */
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


    /**
     * Sends a POST request to the backend to signup a new user
     * @param volleyCallBack implements callback for onresponse and onError
     * @param name name of the new user
     * @param email email of the new user
     * @param pass password of the new user
     * @throws URISyntaxException
     */
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

    /**
     * Get's the namecards of the user and their rating
     * @param partnerId id of the requested partner
     * @param user_email email of the logged in user
     * @param pass password of the logged in user
     * @param volleyCallBack callback for onrespnse and onError
     */
    public void getNameCardsAndRating(Long partnerId,String user_email,String pass, VolleyCallBack<ArrayList<NameCardItem>> volleyCallBack) {
        ArrayList<NameCardItem> comboList = new ArrayList<>();
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
                            comboList.add(new NameCardItem(
                                    nc.getInt("id"),
                                    nc.getString("name"),
                                    nc.getInt("rating"),
                                    nc.getInt("gender")
                            ));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    volleyCallBack.onResponse((ArrayList<NameCardItem>) comboList);
                }, error -> {
            volleyCallBack.onError( getString(R.string.errorGettingPartners));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Returns a list of the current users approved NameCardItems
     * @param context
     * @param volleyCallBack
     */
    public void getApprovedNames(Activity context, VolleyCallBack<ArrayList<NameCardItem>> volleyCallBack) {
        Prefs prefs = new Prefs(context);
        String [] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        ArrayList<NameCardItem> approvedList = new ArrayList<>();
        String listeningPath = "viewliked/approvedlist";
        URIBuilder b = null;
        try {
            b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        b.addParameter("email", email);
        b.addParameter("pass", pass);
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
                            approvedList.add(new NameCardItem(
                                    nc.getInt("id"),
                                    nc.getString("name"),
                                    nc.getInt("rating"),
                                    nc.getInt("gender")
                            ));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    volleyCallBack.onResponse((ArrayList<NameCardItem>) approvedList);
                }, error -> {
            volleyCallBack.onError( getString(R.string.errorGettingPartners));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Returns a list of the current users approved NameCards.
     * @param context
     * @param volleyCallBack
     */
    public void getApprovedNameCards(Activity context, VolleyCallBack<ArrayList<NameCard>> volleyCallBack) {
        Prefs prefs = new Prefs(context);
        String [] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        ArrayList<NameCard> approvedList = new ArrayList<>();
        String listeningPath = "viewliked/approvedlist";
        URIBuilder b = null;
        try {
            b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        b.addParameter("email", email);
        b.addParameter("pass", pass);
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
                            approvedList.add(new NameCard(
                                    nc.getInt("id"),
                                    nc.getString("name"),
                                    nc.getString("rating"),
                                    nc.getInt("gender")
                            ));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    volleyCallBack.onResponse((ArrayList<NameCard>) approvedList);
                }, error -> {
            volleyCallBack.onError( getString(R.string.errorGettingPartners));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Sends a GET method to the server (maybe it should had been delete..) that removes a NameCardID
     * From the loggedin user
     * @param namecardId id of the NameCard to be removed
     * @param context
     * @param volleyCallBack
     */
    public void removeFromApprovedList(int namecardId, Activity context, VolleyCallBack<JSONObject> volleyCallBack){
        Prefs prefs = new Prefs(context);
        String [] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "viewliked/remove";
        URIBuilder b = null;
        String url = "";
        try {
            b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
            b.addParameter("email",email);
            b.addParameter("password",pass);
            b.addParameter("id", String.valueOf(namecardId));

            url = b.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url,null,
                response -> {
                    volleyCallBack.onSuccess();
                    volleyCallBack.onResponse(response);

                },error -> {
            volleyCallBack.onError(getString(R.string.errorRetrievingData));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     *  Updated the NameCard with the given rating for the logged in user
     * @param namecardId
     * @param rating
     * @param context
     * @param volleyCallBack
     */
    public void updateRating(int namecardId, int rating, Activity context, VolleyCallBack<JSONObject> volleyCallBack){
        Prefs prefs = new Prefs(context);
        String [] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "viewliked/updaterating";
        URIBuilder b = null;
        String url = "";
        try {
            b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
            b.addParameter("email",email);
            b.addParameter("password",pass);
            b.addParameter("id", String.valueOf(namecardId));
            b.addParameter("rating", String.valueOf(rating));
            url = b.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url,null,
                response -> {
                    volleyCallBack.onSuccess();
                    volleyCallBack.onResponse(response);

                },error -> {
            volleyCallBack.onError(getString(R.string.errorRetrievingData));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);

    }

    /**
     * Gets a new name by the gender that is specified from the list of available names from the
     * logged in user
     * @param male
     * @param female
     * @param context
     * @param volleyCallBack
     * @throws URISyntaxException
     */
    public void getNewName(boolean male, boolean female, Activity context, VolleyCallBack<NameCard> volleyCallBack) throws URISyntaxException {
        Prefs prefs = new Prefs(context);
        String[] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "swipe/newname";
        URIBuilder b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        b.addParameter("email", email);
        b.addParameter("pass", pass);
        if (female)
            b.addParameter("female", "true");
        if (male)
            b.addParameter("male", "true");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, b.build().toString(), null,
                response -> {

                    Gson g = new Gson();
                    NameCard nc = g.fromJson(String.valueOf(response), NameCard.class);
                    volleyCallBack.onResponse(nc);

                }, error -> {
            volleyCallBack.onError(getString(R.string.systemError));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * a method used to either approve name or disapprove name and get a new NameCard from the given
     * parameters
     * @param action - either approve or disapprove
     * @param currentCardId - id of the current card on screen
     * @param male - used to specify the next card gender
     * @param female - used to specify the next card
     * @param context
     * @param volleyCallBack
     * @throws URISyntaxException
     */
    public void chooseName(String action, int currentCardId,boolean male, boolean female, Activity context, VolleyCallBack<NameCard> volleyCallBack) throws URISyntaxException {
        Log.d("ApiController.chooseName", "Action: " + action + " nameCardId: " + currentCardId + " male: " + male + " female: " + female + " Context: " + context );
        Prefs prefs = new Prefs(context);
        String[] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "swipe/" + action;
        URIBuilder b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        b.addParameter("id", String.valueOf(currentCardId));
        b.addParameter("email", email);
        b.addParameter("pass", pass);
        if (female)
            b.addParameter("female", "true");
        if (male)
            b.addParameter("male", "true");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, b.build().toString(), null,
                response -> {
                    Gson g = new Gson();
                    NameCard nc = g.fromJson(String.valueOf(response), NameCard.class);
                    volleyCallBack.onResponse(nc);
                }, error -> {
            volleyCallBack.onError(getString(R.string.systemError));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Adds current card to liked names list for current user
     * @param currentCardId
     * @param male
     * @param female
     * @param context
     * @param volleyCallBack
     * @throws URISyntaxException
     */
    public void addToLiked(int currentCardId,boolean male, boolean female, Activity context, VolleyCallBack<NameCard> volleyCallBack) throws URISyntaxException {
        Log.d("ApiController.chooseName",  "nameCardId: " + currentCardId + " male: " + male + " female: " + female + " Context: " + context );
        Prefs prefs = new Prefs(context);
        String[] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "searchname/addtoliked";
        URIBuilder b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        b.addParameter("id", String.valueOf(currentCardId));
        b.addParameter("email", email);
        b.addParameter("pass", pass);
        if (female)
            b.addParameter("female", "true");
        if (male)
            b.addParameter("male", "true");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, b.build().toString(), null,
                response -> {
                    Gson g = new Gson();
                    NameCard nc = g.fromJson(String.valueOf(response), NameCard.class);
                    volleyCallBack.onResponse(nc);
                }, error -> {
            volleyCallBack.onError(getString(R.string.systemError));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Returns statistics for current user
     * @param context
     * @param volleyCallBack
     */
    public void getStatData(Activity context,VolleyCallBack<JSONObject> volleyCallBack){
        Prefs prefs = new Prefs(context);
        String [] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "viewliked";
        URIBuilder b = null;
        String url = "";
        try {
            b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
            b.addParameter("email",email);
            b.addParameter("pass",pass);
            url = b.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url,null,
                response -> {
                    volleyCallBack.onResponse(response);
                },error -> {
            volleyCallBack.onError(getString(R.string.errorRetrievingData));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Returns ArrayList of UserItems that are the linked partners to the current user
     * @param context
     * @param volleyCallBack
     */
    public void getLinkedPartners(Activity context, VolleyCallBack<ArrayList<UserItem>> volleyCallBack) {
        Prefs prefs = new Prefs(context);
        String [] user = prefs.getUser();
        String user_email = user[0];
        String pass = user[1];
        ArrayList<UserItem> partnersList = new ArrayList<>();
        String listeningPath = "linkpartner";
        URIBuilder b = null;
        try {
            b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        b.addParameter("email", user_email);
        b.addParameter("pass", pass);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, b.toString(), null,
                response -> {
                    JSONArray resp = new JSONArray();
                    try {
                        resp = response.getJSONArray("partners");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        for(int i = 0; i < resp.length(); i++) {
                            JSONObject us = (JSONObject) resp.get(i);
                            String partner = us.getString("name");
                            String partnerEmail = us.getString("email");

                            partnersList.add(new UserItem(
                                    partner, partnerEmail
                            ));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    volleyCallBack.onResponse((ArrayList<UserItem>) partnersList);
                }, error -> {
            volleyCallBack.onError( getString(R.string.errorGettingPartners));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Puts new partner on linked partner list for current user based on pEmail
     * @param pEmail
     * @param context
     * @param volleyCallBack
     */
    public void putLinkedPartners(String pEmail, Activity context, VolleyCallBack<ArrayList<UserItem>> volleyCallBack) {
        Prefs prefs = new Prefs(context);
        String [] user = prefs.getUser();
        String user_email = user[0];
        String pass = user[1];
        String email = pEmail;
        ArrayList<UserItem> partnersList = new ArrayList<>();
        String listeningPath = "linkpartner";
        URIBuilder b = null;
        try {
            b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        b.addParameter("email", user_email);
        b.addParameter("pass", pass);
        b.addParameter("partner", email);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, b.toString(), null,
                response -> {
                    JSONArray resp = new JSONArray();
                    try {
                        if(response.has("message")){
                            Toast.makeText(context, response.getString("message") ,Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        resp = response.getJSONArray("partners");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                         for(int i = 0; i < resp.length(); i++) {
                            JSONObject us = (JSONObject) resp.get(i);
                            String partner = us.getString("name");
                            String partnerEmail = us.getString("email");

                            partnersList.add(new UserItem(
                                    partner, partnerEmail
                            ));
                          }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    volleyCallBack.onResponse((ArrayList<UserItem>) partnersList);
                    volleyCallBack.onSuccess();
                }, error -> {
            volleyCallBack.onError( getString(R.string.errorGettingPartners));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * removes the given partnerID from the partner list of the logged in user
     * @param partnerEmail
     * @param context
     * @param volleyCallBack
     */
    public void removeFromLinkPartners(String partnerEmail, Activity context, VolleyCallBack<JSONObject> volleyCallBack) {
        Prefs prefs = new Prefs(context);
        String [] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "linkpartner/remove";
        URIBuilder b = null;
        String url = "";
        try {
            b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
            b.addParameter("email",email);
            b.addParameter("password",pass);
            b.addParameter("partnerEmail", partnerEmail);

            url = b.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url,null,
                response -> {
                    volleyCallBack.onSuccess();
                    volleyCallBack.onResponse(response);

                },error -> {
            volleyCallBack.onError(getString(R.string.errorRetrievingData));
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Returns generated full name based on inputs
     * @param context
     * @param middle
     * @param gender
     * @param lastName
     * @param volleyCallBack
     */
    public void generateComboName(Activity context, boolean middle, int gender, String lastName, VolleyCallBack<String> volleyCallBack){
        Prefs prefs = new Prefs(context);
        String [] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "viewliked/namemaker";
        URIBuilder b = null;
        String url = "";
        try {
            b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
            b.addParameter("email",email);
            b.addParameter("pass",pass);
            b.addParameter("middle", String.valueOf(middle));
            b.addParameter("gender", String.valueOf(gender));
            url = b.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url,null,
                response -> {
                    try {
                        if(response.has("message")){
                            Toast.makeText(context, response.getString("message") ,Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        String name = response.getString("name");
                        if(middle)
                            name = name.concat(String.format("%s", response.getString("middle")));
                        name = name.concat(" " + lastName );
                        volleyCallBack.onResponse(name);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },error -> {
                    volleyCallBack.onError(getResources().getString(R.string.errorRetrievingData));

        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);

    }

    /**
     * Returns an ArrayList of NameCards by name from the total stored in the database.
     * @param context application context
     * @param nameQuery String
     * @param volleyCallBack
     */
    public void getNameCardsByName(Activity context, String nameQuery, VolleyCallBack<ArrayList<NameCard>> volleyCallBack) {
        Prefs prefs = new Prefs(context);
        String [] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "searchname";
        URIBuilder b = null;
        String url = "";

        try {
            b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
            b.addParameter("email",email);
            b.addParameter("pass",pass);
            b.addParameter("query", nameQuery);
            url = b.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {
                    Gson g = new Gson();
                    ArrayList<NameCard> nameCards = new ArrayList<>();
                    try {
                        Log.d("nameSearch", "Searchname: "+response.getJSONArray("results").toString());
                        JSONArray result = response.getJSONArray("results");
                        for (int i = 0; i < result.length(); i++ ) {
                            JSONObject nc = (JSONObject) result.get(i);
                            nameCards.add( new NameCard(
                                    nc.getInt("id"),
                                    nc.getString("name"),
                                    nc.getString("description"),
                                    nc.getInt("gender")
                            ));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    volleyCallBack.onResponse((ArrayList<NameCard>) nameCards);
                },error -> {
                    volleyCallBack.onError("Leit tókst ekki");
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);

    }

    /**
     * updates the password of the logged in user from the given password
     * @param context
     * @param pass
     * @param newpass
     * @param volleyCallBack
     */
    public  void updatePassword(Context context,String pass, String newpass, VolleyCallBack<JSONObject> volleyCallBack){
        Prefs prefs = new Prefs((Activity) context);
        String [] user = prefs.getUser();
        String email = user[0];
        String listeningPath = "api/settings/changepassword";
        URIBuilder b = null;
        String url = "";

        try {
            b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
            b.addParameter("email",email);
            b.addParameter("pass",pass);
            b.addParameter("newpass",newpass);

            url = b.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {
                    volleyCallBack.onSuccess();
                    volleyCallBack.onResponse(response);
                },error -> {
            volleyCallBack.onError("Kerfisvilla");
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }


    /**
     * updates the users name
     * @param context
     * @param newname
     * @param volleyCallBack
     */
    public void updateUsersName(Context context,String newname, VolleyCallBack<JSONObject> volleyCallBack){
        Prefs prefs = new Prefs((Activity) context);
        String [] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "api/settings/updatename";
        URIBuilder b = null;
        String url = "";

        try {
            b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
            b.addParameter("email",email);
            b.addParameter("pass",pass);
            b.addParameter("newname",newname);

            url = b.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {
                    volleyCallBack.onSuccess();
                    volleyCallBack.onResponse(response);
                },error -> {
            volleyCallBack.onError("Kerfisvilla");
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Resets the approveed named list and available names for th elogged in user
     * @param context
     * @param volleyCallBack
     */
    public void resetUserLists(Context context, VolleyCallBack<JSONObject> volleyCallBack){
        Prefs prefs = new Prefs((Activity) context);
        String [] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "api/settings/resetlists";
        URIBuilder b = null;
        String url = "";

        try {
            b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
            b.addParameter("email",email);
            b.addParameter("pass",pass);
            url = b.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {
                    volleyCallBack.onSuccess();
                    volleyCallBack.onResponse(response);
                },error -> {
            volleyCallBack.onError("Kerfisvilla");
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
