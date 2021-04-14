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
        /**
         * Button/toggle activities eingöngu
         * ekki sér síður fyrir þetta
         * **/
        binding.btnRestartNameList.setOnClickListener(this::RestartNameList);
        binding.switchGetNotifications.setOnClickListener(this::PushNotifications);


    }

    /**
     * onClick event sem leiðir á aðra síðu þar sem að passwordinu verður breytt
     * @param view
     */
    public void PasswordSettings (View view) {
        Intent i = new Intent(SettingsActivity.this, PasswordActivity.class);
        finish();
        startActivity(i);
    }

    /**
     * onClick event sem leiðir á síðu þar sem breytingar verða gerðar
     * @param view
     */
    public void NameSettings (View view) {
        Intent i = new Intent(SettingsActivity.this, NameChangeActivity.class);
        finish();
        startActivity(i);

    }

    /**
     * onClick event
     * @param view
     * staðfestingar skilaboð eiga að poppa upp
     */
    public void RestartNameList (View view) {


    }

    /**
     * onClick event
     * @param view
     * ef játað, þá koma tilkynningar, engin skilaboð
     */
    public void PushNotifications (View view) {

    }

}
