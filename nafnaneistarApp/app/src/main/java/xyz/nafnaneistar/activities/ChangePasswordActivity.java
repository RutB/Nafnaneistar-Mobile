package xyz.nafnaneistar.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivityChangePasswordBinding;
import xyz.nafnaneistar.model.User;

public class ChangePasswordActivity extends SettingsActivity {
    private ActivityChangePasswordBinding binding;
    private User currentUser = new User();
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.passwordContainer, navbar)
                    .commit();
        }

        prefs = new Prefs(ChangePasswordActivity.this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        binding.tilOldPassword.setOnClickListener(this::oldPassword);
        binding.tilNewPassword.setOnClickListener(this::newPassword);
        binding.btnConfirm1.setOnClickListener(this::confirmButton);
        binding.btnGoBack.setOnClickListener(this::goBackButton);
    }

    public void oldPassword(View view) {

    }
    public void newPassword(View view) {

    }
    public void confirmButton(View view) {

    }
    public void goBackButton(View view) {

    }
}
