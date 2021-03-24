package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.TextViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
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
import org.json.JSONObject;

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
                putCheckLink(view);
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
            getCheckLink();
        }catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void fillTable(JSONArray resp) throws JSONException {
        for (int i = resp.length()-1; i >= 0 ; i--) {
            Log.d("fill", "fill" + i+ "    " + binding.llsvPartner.getChildAt(i));
            binding.llsvPartner.removeView(binding.llsvPartner.getChildAt(i));
        }

        for(int i = 0; i < resp.length(); i++){
            Log.d("for", "for" + i);
            JSONObject bla = (JSONObject) resp.get(i);
            String partner = bla.getString("name");
            String partnerEmail = bla.getString("email");
            TextView column1 = new TextView(this,  null, 0, R.style.linkpartnerItem);
            TextView column2 = new TextView(this, null, 0, R.style.linkpartnerItem);
            LinearLayout row = new LinearLayout(this, null, 0, R.style.linkpartnerrow);
            row.setOrientation(LinearLayout.HORIZONTAL);
            final float scale = row.getResources().getDisplayMetrics().density;
            //column1.setWidth(380);
            column1.setWidth((int) (95 * scale + 0.5f));
            column1.setHeight((int) (26 * scale + 0.5f));
            column2.setWidth((int) (135 * scale + 0.5f));
            Log.d("nafn", "nafn" + partner);
            column1.setText(partner);
            column2.setText(partnerEmail);
            row.addView(column1);
            row.addView(column2);
            binding.llsvPartner.addView(row);
            //String sName = "tvLinkName" + String.valueOf(i);

            //Log.d("partners", "CheckLink: ");
        }

    }

    public void putCheckLink(View view) throws URISyntaxException {
        String[] user = prefs.getUser();
        String user_email = user[0];
        String pass = user[1];
        String email = binding.etEmail2.getText().toString().trim();
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        if (email.length() == 0) {
            Toast.makeText(LinkPartnerActivity.this, R.string.errorEmptyStrings, Toast.LENGTH_SHORT)
                    .show();
            return;
        } else if (!matcher.matches()) {
            Toast.makeText(LinkPartnerActivity.this, R.string.errorInvalidEmail, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        String linkUrl = String.format("%slink?&email=%s", ApiController.getDomainURL(), email);
        String listeningPath = "linkpartner";

        URIBuilder b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        b.addParameter("email", user_email);
        b.addParameter("pass", pass);
        b.addParameter("partner", email);
        Log.d("Test", "CheckLink: "+ b.toString());
        JsonObjectRequest jsonObjReqBla = new JsonObjectRequest(Request.Method.POST, b.toString(), null,
                response -> {
                    JSONArray resp = new JSONArray();
                    try {
                        resp = response.getJSONArray("partners");
                        Log.d("partners", "pruuuuufa"+  resp);
                        fillTable(resp);
                        Log.d("partners", "buin með fill");
                        binding.etEmail2.setText("");
                        binding.etEmail2.clearFocus();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("partners", "CheckLink: " +resp );


                }, error -> {
            Toast.makeText(LinkPartnerActivity.this, R.string.errorInvalidEmail, Toast.LENGTH_SHORT)
                    .show();
            Log.d("Test", "CheckLogin: " + error.toString());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReqBla);
    }
    public void checkPartners() throws URISyntaxException {

    }

    public void getCheckLink() throws URISyntaxException {
        String[] user = prefs.getUser();
        String user_email = user[0];
        String pass = user[1];
        String email = binding.etEmail2.getText().toString().trim();
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
                        fillTable(resp);

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