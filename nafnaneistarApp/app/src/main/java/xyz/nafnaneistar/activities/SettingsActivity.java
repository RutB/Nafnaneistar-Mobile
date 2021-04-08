package xyz.nafnaneistar.activities;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.net.URISyntaxException;

import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivitySettingsBinding;
import xyz.nafnaneistar.model.User;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private User currentUser = new User();
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize the navbar fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.settingsContainer, navbar)
                    .commit();
        }
        // setContentView(R.layout.activity_search);
        prefs = new Prefs(SettingsActivity.this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.btnChangePw.setOnClickListener(this::PasswordSettings);
        binding.btnChangeName.setOnClickListener(this::NameSettings);
        binding.btnRestartNameList.setOnClickListener(this::RestartNameList);
        binding.switchGetNotifications.setOnClickListener(this::PushNotifications);


    }
    public void PasswordSettings (View view) {
        Intent i = new Intent(SettingsActivity.this, PasswordActivity.class);
        finish();
        startActivity(i);
    }

    public void NameSettings (View view) {
        Intent i = new Intent(SettingsActivity.this, NameChangeActivity.class);
        finish();
        startActivity(i);

    }

    public void RestartNameList (View view) {
        Intent i = new Intent(SettingsActivity.this, RestartListActivity.class);
        finish();
        startActivity(i);

    }

    public void PushNotifications (View view) {

    }

}
