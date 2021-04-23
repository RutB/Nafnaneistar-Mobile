package xyz.nafnaneistar.activities;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.jar.Attributes;

import xyz.nafnaneistar.activities.SettingsFragment.ChangeDataFragment;
import xyz.nafnaneistar.activities.ViewLikedFragments.ViewNameStatsFragment;
import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivitySettingsBinding;
import xyz.nafnaneistar.model.NameCard;
import xyz.nafnaneistar.model.User;

import static android.app.PendingIntent.getActivity;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private User currentUser = new User();
    private Prefs prefs;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = new Prefs(this);

        fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);
        Fragment nameStats = fragmentManager.findFragmentById(R.id.clNameStats);

        if (navbar == null) {
            navbar = new NavbarFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.settingsContainer, navbar)
                    .commit();
        }

        if (nameStats == null) {
            nameStats = new ViewNameStatsFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.clStatContainer, nameStats,"nameStats")
                    .commit();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        binding.btnChangeName.setOnClickListener(this::changeDataPopup);
        binding.btnChangePassword.setOnClickListener(this::changeDataPopup);
        initNotificationToggle();
        binding.swNotifications.setOnCheckedChangeListener(this::toggleNotifications);
        binding.btnRestartList.setOnClickListener(this::createResetListAlert);

    }
    private  void closeFragment() {
        Fragment f = fragmentManager.findFragmentByTag("nameStats");
        if(f != null)
            fragmentManager.beginTransaction().remove(f).commit();
    }

    public void reloadStats() {
        closeFragment();
        Fragment nameStats;
        nameStats = new ViewNameStatsFragment();
        fragmentManager.beginTransaction()
                .add(R.id.clStatContainer, nameStats,"nameStats")
                .commit();
    }

    public void changeDataPopup(View view) {
        Bundle bundle = new Bundle();

        Fragment changedata = fragmentManager.findFragmentByTag("changedata");
        switch (view.getId()){
            case R.id.btnChangeName:
                bundle.putInt("type", 1);
                break;
            case R.id.btnChangePassword:
                bundle.putInt("type", 2);
                break;
        }
        if(changedata != null){
            fragmentManager.beginTransaction().remove(changedata).commit();
            changedata = fragmentManager.findFragmentByTag("changedata");
            changedata = new ChangeDataFragment();
            changedata.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(R.id.clStatContainer, changedata,"changedata")
                    .commit();

        }
        if (changedata == null) {
            changedata = new ChangeDataFragment();
            changedata.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(R.id.clStatContainer, changedata,"changedata")
                    .commit();
        }
    }

    public void initNotificationToggle(){
        Log.d("notify", "initNotificationToggle: " + prefs.getEnableNotifications());
        binding.swNotifications.setChecked(prefs.getEnableNotifications());
    }

    public void toggleNotifications(CompoundButton buttonView, boolean isChecked){
        prefs.setEnableNotifications(isChecked);
    }
    public void createResetListAlert(View view) {
        android.app.AlertDialog alert =  new AlertDialog.Builder(this)
                .setTitle("Núllstilla listana")
                .setMessage("Ertu Alveg viss um að þú viljir núllstilla nöfnin?")

                .setPositiveButton("Já", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ApiController.getInstance().resetUserLists(view.getContext(), new VolleyCallBack<JSONObject>() {
                            @Override
                            public ArrayList<NameCardItem> onSuccess() {
                                return null;
                            }

                            @Override
                            public void onResponse(JSONObject response) {
                                reloadStats();
                                Toast.makeText(view.getContext(), getResources().getString(R.string.operationSuccess) ,Toast.LENGTH_SHORT)
                                        .show();
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("Nei 😯", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}




































































