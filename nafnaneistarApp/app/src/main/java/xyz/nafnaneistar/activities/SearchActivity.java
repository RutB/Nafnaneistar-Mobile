package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.jar.Attributes;

import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;


import xyz.nafnaneistar.loginactivity.databinding.ActivitySearchBinding;
import xyz.nafnaneistar.model.NameCard;
import xyz.nafnaneistar.model.User;

import static xyz.nafnaneistar.loginactivity.R.*;


public class SearchActivity extends AppCompatActivity {
    private ActivitySearchBinding binding;
    // private user currentUser = new User();
    private Prefs prefs;
    private String tvNameResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_search);
        prefs = new Prefs(SearchActivity.this);

        binding = DataBindingUtil.setContentView(this, layout.activity_search);
        binding.btnSearchName.setOnClickListener(view -> {
            try {
                SearchName(view);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });


        // binding.textView13.setText("LOL");
        binding.textView13.getText();

    }

    /**
     * TODO:
     *  Taka inn leitarstreng
     *  Nota leitarstreng til að fá results
     *  Mata results inn í töflu
     * @param view
     * @return
     */
    public NameCard[] SearchName(View view) throws URISyntaxException {
        String nameQuery = binding.etNameSearch.getText().toString().trim();
        String listeningPath = "searchname";

        /**
         * TODO:
         * Senda username og pass með query fyrir authentication.
         */

        URIBuilder b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
        b.addParameter("query",nameQuery);
        String requestURL = b.build().toString();

        // binding.textView13.setText("LOL");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,requestURL,null,
                response -> {
                    Gson g = new Gson();
                    NameCard queryResults = g.fromJson(String.valueOf(response), NameCard.class);
                    Log.d("nameSearch", "Searchname: "+response.toString());

                },error -> {


            /*
            Toast.makeText(SignupActivity.this, R.string.errorEmptyStrings ,Toast.LENGTH_SHORT)
                    .show();
             */
            Log.d("Test", "CheckLogin: " + error.toString());
        });

        return null;
    }


}