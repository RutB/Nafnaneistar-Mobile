package xyz.nafnaneistar.activities.ViewLikedFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import xyz.nafnaneistar.activities.LoginActivity;
import xyz.nafnaneistar.activities.ViewLikedActivity;
import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.FragmentViewNameStatsBinding;
import xyz.nafnaneistar.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewNameStatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewNameStatsFragment extends Fragment {
    FragmentViewNameStatsBinding binding;
    Activity context;
    Prefs prefs;
    private User currentUser;

    public ViewNameStatsFragment() {
        // Required empty public constructor
    }

    public static ViewNameStatsFragment newInstance() {
        ViewNameStatsFragment fragment = new ViewNameStatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        prefs =new Prefs(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_name_stats,container,false);
        try {
            loadCurrentUser();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        getStatData();
        fixStatsTitle();
        View view = getView() != null ? getView() :
                binding.getRoot();
        return view;
    }

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
    private void getStatData(){
        ApiController.getInstance().getStatData(context, new VolleyCallBack<JSONObject>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    binding.tvUserName.setText(response.getString("name"));
                    JSONArray maleStats = response.getJSONArray("malestats");
                    String approvedMale = String.format("Samþykkt %s nöfn", maleStats.get(0).toString());
                    String disapprovedMale = String.format("Hafnað %s nöfnum", maleStats.get(1).toString());
                    String leftMale = String.format("%s nöfn ósnert", maleStats.get(2).toString());
                    binding.tvDescription.setText(response.getString("meaning"));
                    binding.tvMaleApprovedNames.setText(approvedMale);
                    binding.tvMaleRejected.setText(disapprovedMale);
                    binding.tvMaleNamesLeft.setText(leftMale);
                    JSONArray femaleStats = response.getJSONArray("femalestats");
                    String approvedFemale = String.format("Samþykkt %s nöfn", femaleStats.get(0).toString());
                    String disapprovedFemale = String.format("Hafnað %s nöfnum", femaleStats.get(1).toString());
                    String leftFemale = String.format("%s nöfn ósnert", femaleStats.get(2).toString());
                    binding.tvFemaleApprovedNames.setText(approvedFemale);
                    binding.tvFemaleRejected.setText(disapprovedFemale);
                    binding.tvFemaleNamesLeft.setText(leftFemale);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    ApiController.getInstance().checkNotifications((Activity) binding.tvMaleApprovedNames.getContext());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context, error ,Toast.LENGTH_SHORT)
                        .show();
                Intent i = new Intent(context, LoginActivity.class);
                getActivity().finish();
                prefs.Logout();
                startActivity(i);
            }
        });

    }
    private void fixStatsTitle() {
        String female = getString(R.string.tvViewLikeStatNameFemaleTitle);
        SpannableStringBuilder femaleString = new SpannableStringBuilder(female + "  ");
        femaleString.setSpan(new ImageSpan(context, R.drawable.ic_gender_female, DynamicDrawableSpan.ALIGN_CENTER), female.length() + 1, female.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvTitleFemale.setText(femaleString, TextView.BufferType.SPANNABLE);

        String male = getString(R.string.vtViewLikedNameStatsTitleMale);
        SpannableStringBuilder maleString = new SpannableStringBuilder(male + "  ");
        maleString.setSpan(new ImageSpan(context, R.drawable.ic_gender_male, DynamicDrawableSpan.ALIGN_CENTER), male.length() + 1, male.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvTitleMale.setText(maleString, TextView.BufferType.SPANNABLE);
    }


}