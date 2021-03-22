package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import xyz.nafnaneistar.activities.ViewLikedFragments.NameComboFragment;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivityViewLikedBinding;
import xyz.nafnaneistar.model.User;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
    private User currentUser;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.activity_view_liked);
        prefs = new Prefs(ViewLikedActivity.this);
        fragmentManager = getSupportFragmentManager();
        //Initialize the navbar fragment
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.viewLikedContainer, navbar)
                    .commit();
        }
        try {
            loadCurrentUser();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        loadStatMenu();
        fixStatsTitle();
        binding.tvViewLikedMenuYfirlit.setOnClickListener(view -> loadStatMenu());
        binding.tvViewLikedMenuNameCombo.setOnClickListener(view -> loadComboNameMenu());



    }

    private void loadStatMenu(){
        changedSelectedMenu(binding.tvViewLikedMenuYfirlit);
        getStatData();
        hideAllMenuPages();
        binding.clNameStats.setVisibility(View.VISIBLE);
    }



    private void loadComboNameMenu(){
        changedSelectedMenu(binding.tvViewLikedMenuNameCombo);
        hideAllMenuPages();

        addFragment(R.layout.fragment_namecombo,"nameCombo");

    }

    private void addFragment(int id,String tag){
        Fragment f = fragmentManager.findFragmentById(id);

        if (f == null) {
            f = new NameComboFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.clFragmentContainer, f, tag)
                    .commit();
        }
    }

    private void removeFragment(String tag) {

        Fragment f = fragmentManager.findFragmentByTag(tag);
        if (f != null) {
            fragmentManager.beginTransaction()
                    .remove(f)
                    .commit();
        }
    }
    private void hideAllMenuPages(){

        binding.clNameStats.setVisibility(View.INVISIBLE);
        removeFragment("nameCombo");

    }

    private void fixStatsTitle(){
        String female = getString(R.string.tvViewLikeStatNameFemaleTitle);
        SpannableStringBuilder femaleString = new SpannableStringBuilder(female + "  ");
        femaleString.setSpan(new ImageSpan(this, R.drawable.ic_gender_female, DynamicDrawableSpan.ALIGN_CENTER), female.length() + 1, female.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvViewLikedFemaleTitleStats.setText(femaleString, TextView.BufferType.SPANNABLE);

        String male = getString(R.string.vtViewLikedNameStatsTitleMale);
        SpannableStringBuilder maleString = new SpannableStringBuilder(female + "  ");
        maleString.setSpan(new ImageSpan(this, R.drawable.ic_gender_male, DynamicDrawableSpan.ALIGN_CENTER), male.length() + 1, male.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvViewLikedMaleTitleStats.setText(maleString, TextView.BufferType.SPANNABLE);
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



    /**
     * checks if the user is already loggedn in
     * @param user
     */
    public void loadCurrentUser() throws URISyntaxException {
        String[] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "login/check";
        URIBuilder b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
        b.addParameter("email",email);
        b.addParameter("password",pass);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,b.build().toString(),null,
                response -> {
                    Gson g = new Gson();
                    User p = g.fromJson(String.valueOf(response), User.class);
                    currentUser = p;
                },error -> {
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }
}