package xyz.nafnaneistar.activities.SettingsFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import xyz.nafnaneistar.activities.SettingsActivity;
import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.FragmentChangeDataBinding;

/**
* Changes layout depending on the type it gets from the bundle, used to display Update name and Update
 * password
 */
public class ChangeDataFragment extends Fragment {
    FragmentChangeDataBinding binding;
    Activity context;
    Prefs prefs;
    int type = 0;


    public ChangeDataFragment() {
        // Required empty public constructor
    }

    public static ChangeDataFragment newInstance() {
        ChangeDataFragment fragment = new ChangeDataFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_data,container,false);
        View view = getView() != null ? getView() :
                binding.getRoot();
        Bundle bundle = this.getArguments();
        this.type = bundle.getInt("type");
        if(type == 1){
            binding.tvChangeDataTitle.setText("Breyta um Nafn");
            binding.etOldData.setVisibility(View.GONE);
            binding.etNewData.setHint("Nýtt Nafn");
        }
        if(type == 2){
            binding.tvChangeDataTitle.setText("Breyta um lykilorð");
            binding.etOldData.setVisibility(View.VISIBLE);
            binding.etOldData.setHint("Gamla Lykilorð");
            binding.etNewData.setHint("Nýja Lykilorð");
        }
        binding.btnCancel.setOnClickListener(this::closeFragment);
        binding.btnSubmitData.setOnClickListener(this::changeDataOnClick);
        return view;
    }

    private void closeFragment(View view) {
        Fragment f = getParentFragmentManager().findFragmentByTag("changedata");
        if(f != null)
            getParentFragmentManager().beginTransaction().remove(f).commit();
    }

    /**
     * Updates data depending on which type
     * @param view
     */
    public void changeDataOnClick(View view){
        if(type == 1){
            if(binding.etNewData.getText().toString().trim().length() == 0){
                Toast.makeText(getContext(), getResources().getString(R.string.errorEmptyStrings) ,Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            ApiController.getInstance().updateUsersName(view.getContext(), binding.etNewData.getText().toString(), new VolleyCallBack<JSONObject>() {
                @Override
                public ArrayList<NameCardItem> onSuccess() {
                    return null;
                }

                @Override
                public void onResponse(JSONObject response) {
                    SettingsActivity settingsActivity = (SettingsActivity) view.getContext();
                    settingsActivity.reloadStats();
                    closeFragment(view);
                    Toast.makeText(getContext(), getResources().getString(R.string.operationSuccess) ,Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error ,Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

        if(type == 2){
            if(binding.etNewData.getText().toString().trim().length() == 0 || binding.etOldData.getText().toString().trim().length() == 0){
                Toast.makeText(getContext(), getResources().getString(R.string.errorEmptyStrings) ,Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            ApiController.getInstance().updatePassword(view.getContext(), binding.etOldData.getText().toString(), binding.etNewData.getText().toString(), new VolleyCallBack<JSONObject>() {

                @Override
                public ArrayList<NameCardItem> onSuccess() {
                    return null;
                }

                @Override
                public void onResponse(JSONObject response) {
                    SettingsActivity settingsActivity = (SettingsActivity) view.getContext();
                    settingsActivity.reloadStats();
                    closeFragment(view);
                    Toast.makeText(getContext(), getResources().getString(R.string.operationSuccess) ,Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), error,Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }
}