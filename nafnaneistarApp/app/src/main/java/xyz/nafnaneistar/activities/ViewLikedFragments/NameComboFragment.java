package xyz.nafnaneistar.activities.ViewLikedFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.ArrayList;

import xyz.nafnaneistar.activities.LoginActivity;
import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
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
    int selectedGender = 1;
    String lastName = "";
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            selectedGender = savedInstanceState.getInt("selectedGender");
            lastName = savedInstanceState.getString("lastName");
            restoreView(selectedGender,lastName);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("selectedGender",selectedGender);
        outState.putString("lastName", String.valueOf(binding.etComboNameLastName.getText()));
        super.onSaveInstanceState(outState);
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
        View view = getView() != null ? getView() :
                binding.getRoot();
        return view;
    }

    private void restoreView(int gender, String lastName){
            if(gender == 1) {
                binding.rbNameComboMale.setChecked(false);
                binding.rbNameComboFemale.setChecked(true);
                selectedGender = 1;
            }
            else {
                binding.rbNameComboMale.setChecked(true);
                binding.rbNameComboFemale.setChecked(false);
                selectedGender = 0;

            }
            binding.etComboNameLastName.setText(lastName);
    }

    private void radioCheck(View view){
        if(view == binding.rbNameComboFemale){
            binding.rbNameComboMale.setChecked(false);
            binding.rbNameComboFemale.setChecked(true);
            selectedGender = 1;
        }
        else {
            binding.rbNameComboMale.setChecked(true);
            binding.rbNameComboFemale.setChecked(false);
            selectedGender = 0;
        }

    }

    private void generateComboName(View view){
        boolean middle = view == binding.btComboNameMiddle;
        int gender = (binding.rbNameComboFemale.isChecked()) ? 1 : 0;
        ApiController.getInstance().generateComboName((Activity) view.getContext(), middle, gender,
                String.valueOf(binding.etComboNameLastName.getText()), new VolleyCallBack<String>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(String name) {
                binding.tvCombonameReslt.setText(name);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context,error ,Toast.LENGTH_SHORT)
                        .show();
                Intent i = new Intent(getContext(), LoginActivity.class);
                context.finish();
                prefs.Logout();
                startActivity(i);
            }
        });
    }
}