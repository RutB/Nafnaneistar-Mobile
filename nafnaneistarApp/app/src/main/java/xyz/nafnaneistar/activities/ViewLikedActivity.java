package xyz.nafnaneistar.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import xyz.nafnaneistar.activities.ViewLikedFragments.ApprovedNameListManagerFragment;
import xyz.nafnaneistar.activities.ViewLikedFragments.ComboListFragment;
import xyz.nafnaneistar.activities.ViewLikedFragments.ComboListManagerFragment;
import xyz.nafnaneistar.activities.ViewLikedFragments.NameComboFragment;
import xyz.nafnaneistar.activities.ViewLikedFragments.ViewNameStatsFragment;
import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivityViewLikedBinding;
import xyz.nafnaneistar.model.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class ViewLikedActivity extends AppCompatActivity {
    private ActivityViewLikedBinding binding;
    private Prefs prefs;
    private User currentUser;
    FragmentManager fragmentManager;
    private int selectedMenuIndex = -1;
    private String[] FragmentTags = new String[]{"comboList","nameCombo"};
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
        binding.tvViewLikedMenuCombinedList.setOnClickListener(view -> loadComboListMenu());
        binding.tvViewLikedMenuRateName.setOnClickListener(view -> loadApprovedNames());
    }


    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("restore", "onRestoreInstanceState: " + savedInstanceState.getInt("selectedMenu"));
        loadCurrentMenu(savedInstanceState.getInt("selectedMenu"));
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("selectedMenu",selectedMenuIndex);
        super.onSaveInstanceState(outState);

    }

    /**
     * toggles different menus depending on selected index
     * @param index
     */
    public void loadCurrentMenu(int index){
        switch (index){
            case 1:
                selectedMenuIndex = 1;
                loadComboListMenu();
                break;
            case 2:
                selectedMenuIndex = 2;
                loadApprovedNames();
                ;break;
            case 3:
                selectedMenuIndex = 3;
                loadComboNameMenu();
                break;
            default:
                selectedMenuIndex = 0;
                loadStatMenu();
                fixStatsTitle();
            break;

        }
    }

    /**
    loads the statsFragment
     **/
    private void loadStatMenu(){
        if(selectedMenuIndex == 0) return;
        selectedMenuIndex = 0;
        changedSelectedMenu(binding.tvViewLikedMenuYfirlit);
        getStatData();
        hideAllMenuPages();
        Fragment f = fragmentManager.findFragmentById(R.layout.fragment_view_name_stats);
        if (f == null) {
            f = new ViewNameStatsFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.clFragmentContainer, f, "ViewLikedStats")
                    .commit();
        }
    }

    /**
     * Loads the combolist fragment
     */
    private void loadComboListMenu(){
        if(selectedMenuIndex == 1) return;
        selectedMenuIndex = 1;
        changedSelectedMenu(binding.tvViewLikedMenuCombinedList);
        hideAllMenuPages();
        Fragment f = fragmentManager.findFragmentById(R.layout.fragment_combo_list);
        if (f == null) {
            f = new ComboListFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.clFragmentContainer, f, "comboList")
                    .commit();
        }
    }
    private void loadApprovedNames(){
        Fragment f = fragmentManager.findFragmentById(R.layout.fragment_approved_list_manager);
        if(f != null) return;
        if(selectedMenuIndex == 2) return;
        selectedMenuIndex = 2;
        changedSelectedMenu(binding.tvViewLikedMenuRateName);
        hideAllMenuPages();
        binding.clNameStats.setVisibility(View.VISIBLE);

        if (f == null ) {
            f = new ApprovedNameListManagerFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.viewLikedContainer, f, "ApprovedList")
                    .commit();
        }
    }

    private void loadComboNameMenu(){
        if(selectedMenuIndex == 3) return;
        selectedMenuIndex = 3;
        changedSelectedMenu(binding.tvViewLikedMenuNameCombo);
        hideAllMenuPages();
        Fragment f = fragmentManager.findFragmentByTag("nameCombo");
        if (f == null) {
            f = new NameComboFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.clFragmentContainer, f, "nameCombo")
                    .commit();
        }
    }

    /**
     * overrides onBackPressed to close the fullscreen fragments oonly and not leave viewliked Activity
     */
    @Override
    public void onBackPressed() {
        Fragment f = fragmentManager.findFragmentByTag("listViewCombo");
        Fragment f2 = fragmentManager.findFragmentByTag("ApprovedList");
        if (f != null) {
            removeFragment("listViewCombo");
        } else if (f2 != null) {
            removeFragment("ApprovedList");
        }else {
            super.onBackPressed();
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
        for (String tag: FragmentTags) {
            if(tag == "nameCombo" && selectedMenuIndex == 3)
                continue;

            removeFragment(tag);
        }
    }

    /**
     * Fixes the title for statistics on current user
     */
    private void fixStatsTitle(){
        String female = getString(R.string.tvViewLikeStatNameFemaleTitle);
        SpannableStringBuilder femaleString = new SpannableStringBuilder(female + "  ");
        femaleString.setSpan(new ImageSpan(this, R.drawable.ic_gender_female, DynamicDrawableSpan.ALIGN_CENTER), female.length() + 1, female.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvViewLikedFemaleTitleStats.setText(femaleString, TextView.BufferType.SPANNABLE);

        String male = getString(R.string.vtViewLikedNameStatsTitleMale);
        SpannableStringBuilder maleString = new SpannableStringBuilder(male + "  ");
        maleString.setSpan(new ImageSpan(this, R.drawable.ic_gender_male, DynamicDrawableSpan.ALIGN_CENTER), male.length() + 1, male.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvViewLikedMaleTitleStats.setText(maleString, TextView.BufferType.SPANNABLE);
    }

    /**
     * Changes color to indicate what submeu is selected
     * @param view
     */
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

    /**
     * Fills in statistics on current user
     */
    private void getStatData(){
        ApiController.getInstance().getStatData(this, new VolleyCallBack<JSONObject>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    binding.tvViewLikedUserName.setText(response.getString("name"));
                    JSONArray maleStats = response.getJSONArray("malestats");
                    String approvedMale = String.format("Samþykkt %s nöfn", maleStats.get(0).toString());
                    String disapprovedMale = String.format("Hafnað %s nöfnum", maleStats.get(1).toString());
                    String leftMale = String.format("%s nöfn ósnert", maleStats.get(2).toString());
                    binding.tvViewLikedStatsMeaning.setText(response.getString("meaning"));
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
                try {
                    ApiController.getInstance().checkNotifications((Activity) binding.tvViewLikedMenuCombinedList.getContext());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ViewLikedActivity.this, error ,Toast.LENGTH_SHORT)
                        .show();
                Intent i = new Intent(ViewLikedActivity.this, LoginActivity.class);
                finish();
                prefs.Logout();
                startActivity(i);
            }
        });
    }

    /**
     * loads the current user to have that accessible
     * @throws URISyntaxException
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