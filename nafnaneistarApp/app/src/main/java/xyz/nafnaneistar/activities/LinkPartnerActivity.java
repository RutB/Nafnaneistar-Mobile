package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;

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
        binding.btnLink.setOnClickListener(this::CheckLink);
        prefs = new Prefs(LinkPartnerActivity.this);
    }

    public void CheckLink(View view) {
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
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, linkUrl, null,
                response -> {
                    Gson g = new Gson();
                    User p = g.fromJson(String.valueOf(response), User.class);
                    Log.d("link", "linkEmail: " + response.toString());
                    if (p.getEmail() != null) {
                        Toast.makeText(LinkPartnerActivity.this, R.string.linkSuccess, Toast.LENGTH_SHORT)
                                .show();
                        //prefs.saveUser(email);
                        Intent intent = getIntent();
                        setResult(RESULT_OK, intent);
                        //finish();

                        //setja í töflu
                    } else {
                        String message = null;
                        try {
                            message = response.getString("message");
                            Toast.makeText(LinkPartnerActivity.this, message, Toast.LENGTH_SHORT)
                                    .show();
                        } catch (JSONException e) {
                            Toast.makeText(LinkPartnerActivity.this, R.string.errorEmptyStrings, Toast.LENGTH_SHORT)
                                    .show();
                            e.printStackTrace();
                        }
                    }

                }, error -> {
            Toast.makeText(LinkPartnerActivity.this, R.string.errorEmptyStrings, Toast.LENGTH_SHORT)
                    .show();
            Log.d("Test", "CheckLogin: " + error.toString());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);

    }
}
