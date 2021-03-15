package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivityViewLikedBinding;
import xyz.nafnaneistar.model.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;

public class ViewLikedActivity extends AppCompatActivity {
    private ActivityViewLikedBinding binding;
    private Prefs prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.activity_view_liked);
        prefs = new Prefs(ViewLikedActivity.this);
        //Initialize the navbar fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.viewLikedContainer, navbar)
                    .commit();
        }
        loadStatMenu();

    }

    private void loadStatMenu(){
        changedSelectedMenu(binding.tvViewLikedMenuYfirlit);
        getStatData();
        binding.clNameStats.setVisibility(View.VISIBLE);
    }

    private void changedSelectedMenu(View view){
        int childCount = binding.llViewLikedMenu.getChildCount();
        for(int i = 0; i < childCount; i++){
            if(view ==binding.llViewLikedMenu.getChildAt(i)){
                view.setBackgroundResource(R.color.btn_color_lg);
            }
            else{
                binding.llViewLikedMenu.getChildAt(i).setBackgroundResource(R.color.ambiant);
            }
        }
    }

    private void getStatData(){
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
                    try {
                        binding.tvViewLikedUserName.setText(response.getString("name"));
                        JSONArray maleStats = response.getJSONArray("malestats");
                        String approvedMale = String.format("Samþykkt %s nöfn", maleStats.get(0).toString());
                        String disapprovedMale = String.format("Hafnað %s nöfnum", maleStats.get(1).toString());
                        String leftMale = String.format("%s nöfn ósnert", maleStats.get(2).toString());
                        binding.tvViewLikedMaleStatsApproved.setText(approvedMale);
                        binding.tvViewLikedMaleStatsDisapproved.setText(disapprovedMale);
                        binding.tvViewLikedMaleStatsLeft.setText(leftMale);
                        JSONArray femaleStats = response.getJSONArray("femalestats");
                        String approvedFemale = String.format("Samþykkt %s nöfn", femaleStats.get(0).toString());
                        String disapprovedFemale = String.format("Hafnað %s nöfnum", femaleStats.get(1).toString());
                        String leftFemale = String.format("%s nöfn ósnert", femaleStats.get(2).toString());
                        binding.tvFemaleStatApproved.setText(approvedFemale);
                        binding.tvFemaleStatRejected.setText(disapprovedFemale);
                        binding.tvFemalestatsLeft.setText(leftFemale);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },error -> {
                    Toast.makeText(ViewLikedActivity.this, getResources().getString(R.string.errorRetrievingData) ,Toast.LENGTH_SHORT)
                    .show();
                    Intent i = new Intent(ViewLikedActivity.this, LoginActivity.class);
                    finish();
                    prefs.Logout();
                    startActivity(i);
            Log.d("viewliked", "getData: " + error.getMessage());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }
}