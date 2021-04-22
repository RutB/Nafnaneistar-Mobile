package xyz.nafnaneistar.activities;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.jar.Attributes;

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
    private EditText newName;
    private EditText newPassword;
    private TextView nameList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Initialize the navbar fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        newName = (EditText) findViewById(R.id.etName);
        newPassword = (EditText) findViewById(R.id.etPassword);
        nameList = (TextView) findViewById(R.id.tvViewLikedMenuYfirlit);

        if (navbar == null) {
            navbar = new NavbarFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.settingsContainer, navbar)
                    .commit();
        }

        prefs = new Prefs(SettingsActivity.this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogPassword = new AlertDialog.Builder(SettingsActivity.this);

                final View passwordLayout = getLayoutInflater().inflate(R.layout.layout_change_password, null);
                alertDialogPassword.setView(passwordLayout);
                alertDialogPassword.setTitle("Breyting á lykilorði");

                alertDialogPassword.setPositiveButton("Já", (dialog, which) -> {
                    finish();
                });

                alertDialogPassword.setNegativeButton("Hætta við",(dialog, which) -> {
                    Toast.makeText(SettingsActivity.this, "Hætt við breytingar", Toast.LENGTH_LONG)
                            .show();
                });

                AlertDialog dialog = alertDialogPassword.create();
                dialog.show();

            }
        });
        binding.btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogName = new AlertDialog.Builder(SettingsActivity.this);

                final View passwordLayout = getLayoutInflater().inflate(R.layout.layout_change_password, null);
                alertDialogName.setView(passwordLayout);
                alertDialogName.setTitle("Breyting á lykilorði");

                alertDialogName.setPositiveButton("Já", (dialog, which) -> {
                    finish();
                });

                alertDialogName.setNegativeButton("Hætta við",(dialog, which) -> {
                    Toast.makeText(SettingsActivity.this, "Hætt við breytingar", Toast.LENGTH_LONG)
                            .show();
                });

                AlertDialog dialog = alertDialogName.create();
                dialog.show();

            }
        });
        binding.btnRestartList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(binding.btnChangeName.getContext())
                        .setTitle("Endurstilla nafnalista")
                        .setMessage("Ertu viss um að þú vilt endurstilla listann?" +
                        " Öll núverandi nöfn munu hverfa")
                        .setPositiveButton("Já", null)
                        .setNegativeButton("Hætta við", null)
                        .show();

                Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentUser.getApprovedNames().isEmpty()) {
                            return;
                        }
                        if (!currentUser.getApprovedNames().isEmpty()) {
                            currentUser.setApprovedNames(null);
                        }
                       return;
                    }
                });

            }
        });
    }






































































         /**
         *
         * Button/toggle activities eingöngu
         * ekki sér síður fyrir þetta
         *
         binding.sNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        binding.btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeName();
            }
        });
        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePassword();
            }
        });
        binding.btnRestartList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestartNameList();
            }
        });
    }


     * public void ChangePassword () {
     *
     *
     *
     *
     *     }
     *
     *     public void ChangeName () {
     *         AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
     *         alertDialog.setTitle("Breyta nafni");
     *
     *         final EditText nameInput = new EditText(this);
     *         nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
     *         alertDialog.show();
     *         alertDialog.setView(nameInput);
     *
     *         alertDialog.setPositiveButton("já", new DialogInterface.OnClickListener() {
     *             @Override
     *             public void onClick(DialogInterface dialog, int which) {
     *
     *             }
     *         });
     *         alertDialog.setNegativeButton("Hætta við", new DialogInterface.OnClickListener() {
     *             @Override
     *             public void onClick(DialogInterface dialog, int which) {
     *                 dialog.cancel();
     *             }
     *         });
     *     }
     */

}
