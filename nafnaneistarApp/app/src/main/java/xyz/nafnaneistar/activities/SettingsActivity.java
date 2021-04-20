package xyz.nafnaneistar.activities;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.w3c.dom.NameList;

import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivitySettingsBinding;
import xyz.nafnaneistar.model.NameCard;
import xyz.nafnaneistar.model.User;

public class SettingsActivity extends ViewLikedActivity {
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
        prefs = new Prefs(SettingsActivity.this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        /**
         * Button/toggle activities eingöngu
         * ekki sér síður fyrir þetta
         * **/
        binding.sNotifications.setOnClickListener(this::PushNotifications);
        binding.tilChangeName.setOnClickListener(this::NameSettings);
        binding.ettPassword.setOnClickListener(this::PasswordSettings);
        binding.btnRestartList.setOnClickListener(this::RestartNameList);
    }

    /**
     * onClick event sem leiðir á aðra síðu þar sem að passwordinu verður breytt
     * @param view
     */
    public void PasswordSettings (View view) {


    }

    /**
     * onClick event sem leiðir á síðu þar sem breytingar verða gerðar
     * @param view
     */
    public void NameSettings (View view) {
        Intent i = new Intent(SettingsActivity.this, ChangeNameActivity.class);
        finish();
        startActivity(i);

    }

    /**
     * onClick event
     * @param view
     * staðfestingar skilaboð eiga að poppa upp
     */
    public void RestartNameList (View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Endurstilla nafnalista")
                .setMessage("Ertu viss um að þú vilt endurstilla listann?" +
                        " Öll núverandi nöfn munu hverfa")
                .setPositiveButton("Já", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentUser.getAvailableNames(currentUser, )
                    }
                })
                .setNegativeButton("Hætta við", null)
                .show();

        Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    /**
     * onClick event
     * @param view
     * ef játað, þá koma tilkynningar, engin skilaboð
     */
    public void PushNotifications (View view) {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_change_name, container,false);

    }

}
