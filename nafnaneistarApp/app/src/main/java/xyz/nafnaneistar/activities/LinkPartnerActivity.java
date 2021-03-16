package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivityLinkPartnerBinding;
import xyz.nafnaneistar.loginactivity.databinding.ActivityLoginBinding;
import xyz.nafnaneistar.model.User;

public class LinkPartnerActivity extends AppCompatActivity {
    private ActivityLinkPartnerBinding binding;
    private User currentUser = new User();
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_link_partner);
        binding.btnLink.setOnClickListener(view -> {
            try {
                CheckLink(view);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        prefs = new Prefs(LinkPartnerActivity.this);

        //Initialize the navbar fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.LinkContainer, navbar)
                    .commit();
        }
        try {
            CheckLink();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void CheckLink(View view) throws URISyntaxException {
        String[] user = prefs.getUser();
        String user_email = user[0];
        String pass = user[1];
        String email = binding.etEmail2.getText().toString().trim();
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        /*if (email.length() == 0) {
            Toast.makeText(LinkPartnerActivity.this, R.string.errorEmptyStrings, Toast.LENGTH_SHORT)
                    .show();
            return;
        } else if (!matcher.matches()) {
            Toast.makeText(LinkPartnerActivity.this, R.string.errorInvalidEmail, Toast.LENGTH_SHORT)
                    .show();
            return;
        }*/
        String linkUrl = String.format("%slink?&email=%s", ApiController.getDomainURL(), email);
        String listeningPath = "linkpartner";

        URIBuilder b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        b.addParameter("email", user_email);
        b.addParameter("pass", pass);
        Log.d("Test", "CheckLink: "+ b.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, b.toString(), null,
                response -> {
                    JSONArray resp = new JSONArray();
                    try {
                        resp = response.getJSONArray("partners");
                        for(int i = 0; i < resp.length(); i++){
                            JsonObject bla = (JsonObject) resp.get(i);
                            Log.d("partners", "CheckLink: ");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("partners", "CheckLink: " +resp );

                }, error -> {
            Toast.makeText(LinkPartnerActivity.this, R.string.errorEmptyStrings, Toast.LENGTH_SHORT)
                    .show();
            Log.d("Test", "CheckLogin: " + error.toString());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);

    }
}