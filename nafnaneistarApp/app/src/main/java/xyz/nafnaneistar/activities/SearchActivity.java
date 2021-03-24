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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

        //Initialize the navbar fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment();
            fragmentManager.beginTransaction()
                    .add(id.SearchNameContainer, navbar)
                    .commit();
        }
    }

    /**
     * TODO:
     *  User authentication.
     *  Mata gögn inn viðmótið í lista.
     *  Add og Remove hnappar á lista við hvert nafn.
     *
     * WIP: Eins og er þá skilar þetta fall leitarniðustöðu í Logcat.
     * @param view
     * @return null
     */
    public void SearchName(View view) throws URISyntaxException {
        String nameQuery = binding.etNameSearch.getText().toString().trim();
        String listeningPath = "searchname";
        URIBuilder b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
        b.addParameter("query",nameQuery);
        String requestURL = b.build().toString();
        Log.d("TEST",requestURL);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,requestURL,null,
                response -> {
                    Gson g = new Gson();
                    NameCard queryResults = g.fromJson(String.valueOf(response), NameCard.class);
                    try {
                        Log.d("nameSearch", "Searchname: "+response.getJSONArray("results").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },error -> {
                    Log.d("searchActivity", "searchActivity " + error.toString());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }


}