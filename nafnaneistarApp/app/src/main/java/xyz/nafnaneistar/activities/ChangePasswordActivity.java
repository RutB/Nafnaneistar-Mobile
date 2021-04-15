package xyz.nafnaneistar.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivityChangePasswordBindingImpl;
import xyz.nafnaneistar.model.User;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBindingImpl binding;
    private User currentUser = new User();
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = new Prefs(ChangePasswordActivity.this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
    }
}
