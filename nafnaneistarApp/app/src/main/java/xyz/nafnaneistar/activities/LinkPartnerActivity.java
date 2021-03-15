package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import xyz.nafnaneistar.model.NameCard;
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
        binding.btnLink.setOnClickListener(this::CheckLink);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment(LinkPartnerActivity.this);
            fragmentManager.beginTransaction()
                    .add(R.id.LinkContainer, navbar)
                    .commit();
        }try {
            getCurrLinkedPartners();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

     /**
     * checks if the partner is already partnered
     * @param user
     */
    public void checkPartner(String[] user) throws URISyntaxException {
    }
    public void getCurrLinkedPartners() throws URISyntaxException {
        String[] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "/linkpartner/";
        URIBuilder b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        b.addParameter("email", email);
        b.addParameter("pass", pass);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, b.build().toString(), null,
                response -> {
                    Gson g = new Gson();
                    User newUser = g.fromJson(String.valueOf(response), User.class);
                    binding.tvLinkName.setText(newUser.getName());
                    binding.tvLinkEmail.setText(newUser.getEmail());

                }, error -> {
            Toast.makeText(LinkPartnerActivity.this, "Kerfisvilla", Toast.LENGTH_LONG)
                    .show();
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
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
            Toast.makeText(LinkPartnerActivity.this, R.string.errorInvalidEmail, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(LinkPartnerActivity.this, R.string.errorEmptyStrings, Toast.LENGTH_SHORT).show();
            Log.d("Test", "CheckLink: " + error.toString());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

        String inEmail = binding.etEmail2.getText().toString().trim();
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        if (email.length() == 0) {
            Toast.makeText(LinkPartnerActivity.this, R.string.errorEmptyStrings, Toast.LENGTH_SHORT).show();
            return;
        } else if (!matcher.matches()) {
            Toast.makeText(LinkPartnerActivity.this, R.string.errorInvalidEmail, Toast.LENGTH_SHORT).show();
            return;
        }
        String linkUrl = String.format("%slink?&email=%s", ApiController.getDomainURL(), email);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, linkUrl, null, response -> {
            Gson g = new Gson();
            User p = g.fromJson(String.valueOf(response), User.class);
            Log.d("link", "linkEmail: " + response.toString());
            if (p.getEmail() != null) {
                Toast.makeText(LinkPartnerActivity.this, R.string.linkSuccess, Toast.LENGTH_SHORT).show();
                // prefs.saveUser(email);

                prefs.getUser(); // ef rétt skila þessu
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                // finish();

                // setja í töflu
            } else {
                String message = null;
                try {
                    message = response.getString("message");
                    Toast.makeText(LinkPartnerActivity.this, message, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(LinkPartnerActivity.this, R.string.errorEmptyStrings, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

        }, error -> {
            Toast.makeText(LinkPartnerActivity.this, R.string.errorEmptyStrings, Toast.LENGTH_SHORT).show();
            Log.d("Test", "CheckLogin: " + error.toString());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);

    }
    private void onClick(View view) {
        try {
            CheckLink(view);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
