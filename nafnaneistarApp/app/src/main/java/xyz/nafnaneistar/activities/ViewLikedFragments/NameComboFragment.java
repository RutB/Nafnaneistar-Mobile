package xyz.nafnaneistar.activities.ViewLikedFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;

import java.net.URISyntaxException;

import xyz.nafnaneistar.activities.LoginActivity;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.FragmentNamecomboBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NameComboFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NameComboFragment extends Fragment {
    FragmentNamecomboBinding binding;
    Activity context;
    Prefs prefs;

    public NameComboFragment() {
        // Required empty public constructor
    }

    public static NameComboFragment newInstance() {
        NameComboFragment fragment = new NameComboFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        prefs = new Prefs(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_namecombo, container, false);
        binding.tvComboNameTitle.setText(R.string.viewlikednamecombo);
        binding.rbNameComboFemale.setOnClickListener(this::radioCheck);
        binding.rbNameComboMale.setOnClickListener(this::radioCheck);
        binding.btComboName.setOnClickListener(this::generateComboName);
        binding.btComboNameMiddle.setOnClickListener(this::generateComboName);
        View view = binding.getRoot();
        return view;
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
                            Toast.makeText(getContext(), response.getString("message") ,Toast.LENGTH_SHORT)
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
            Toast.makeText(getContext(), getResources().getString(R.string.errorRetrievingData) ,Toast.LENGTH_SHORT)
                    .show();
            Intent i = new Intent(getContext(), LoginActivity.class);
            context.finish();
            prefs.Logout();
            startActivity(i);
            Log.d("viewliked", "getData: " + error.getMessage());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);

    }
}