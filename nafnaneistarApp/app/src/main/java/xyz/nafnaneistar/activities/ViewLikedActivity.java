package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
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
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;

public class ViewLikedActivity extends AppCompatActivity {
    private ActivityViewLikedBinding binding;
    private Prefs prefs;
    private User currentUser;
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
        try {
            loadCurrentUser();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        loadStatMenu();
        fixStatsTitle();
        binding.tvViewLikedMenuYfirlit.setOnClickListener(view -> loadStatMenu());
        binding.tvViewLikedMenuNameCombo.setOnClickListener(view -> loadComboNameMenu());
        binding.rbNameComboFemale.setOnClickListener(this::radioCheck);
        binding.rbNameComboMale.setOnClickListener(this::radioCheck);
        binding.btComboNameMiddle.setOnClickListener(this::generateComboName);
        binding.btComboName.setOnClickListener(this::generateComboName);

    }

    private void loadStatMenu(){
        changedSelectedMenu(binding.tvViewLikedMenuYfirlit);
        getStatData();
        hideAllMenuPages();
        binding.clNameStats.setVisibility(View.VISIBLE);
    }

    private void radioCheck(View view){
        if(view == binding.rbNameComboFemale){
            binding.rbNameComboMale.setChecked(false);
            binding.rbNameComboFemale.setChecked(true);
        }
        else {
            binding.rbNameComboMale.setChecked(true);
            binding.rbNameComboFemale.setChecked(false);
        }

    }

    private void loadComboNameMenu(){
        changedSelectedMenu(binding.tvViewLikedMenuNameCombo);
        hideAllMenuPages();
        binding.clNameCombo.setVisibility(View.VISIBLE);
        binding.tvComboNameTitle.setText(R.string.viewlikednamecombo);

    }
    private void hideAllMenuPages(){
        binding.clNameCombo.setVisibility(View.INVISIBLE);
        binding.clNameStats.setVisibility(View.INVISIBLE);
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

    private void generateComboName(View view){
        String [] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        boolean middle = view == binding.btComboNameMiddle;
        Log.d("viewliked", "generateComboName: " + String.valueOf(middle));
        int gender = (binding.rbNameComboFemale.isChecked()) ? 1 : 0;
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
                            Toast.makeText(ViewLikedActivity.this, response.getString("message") ,Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        String name = response.getString("name");
                        if(middle)
                            name = name.concat(String.format("%s", response.getString("middle")));
                        name = name.concat(" " + binding.etComboNameLastName.getText());
                        binding.tvCombonameReslt.setText(name);
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