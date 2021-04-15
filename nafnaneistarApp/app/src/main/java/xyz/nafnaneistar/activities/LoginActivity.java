package xyz.nafnaneistar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.net.URISyntaxException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivityLoginBinding;
import xyz.nafnaneistar.model.User;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
   // private User mCurrentUser = new User();
    private final int REQUEST_CODE = 2;
    private Prefs mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = new Prefs(LoginActivity.this);
        String[] user;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        if (mPrefs.getUser().length == 2) {
            user = mPrefs.getUser();
            binding.etEmail.setText(user[0]);
            binding.etPassword.setText(user[1]);
            try {
                CheckLogin(user);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        binding.btnLogin.setOnClickListener(view -> {
            try {
                CheckLogin(view);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        binding.btnSignup.setOnClickListener(this::Signup);

    }

    /**
     * if the user taps signup then we close the login activity and switch to signupactivity
     *
     * @param view
     */
    public void Signup(View view) {
        Intent i = new Intent(LoginActivity.this, SignupActivity.class);
        finish();
        startActivity(i);
    }

    /**
     * checks if the user is already loggedn in
     *
     * @param user
     */
    public void CheckLogin(String[] user) throws URISyntaxException {
        if (user.length != 2) {
            return;
        }
        String email = user[0];
        String pass = user[1];

        ApiController.getInstance().login(new VolleyCallBack<User>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(User p) {
                if (p.getName() != null) {
                    Toast.makeText(LoginActivity.this, R.string.loginSuccess, Toast.LENGTH_SHORT)
                            .show();
                    mPrefs.saveUser(email, pass);
                    startActivity(new Intent(LoginActivity.this, SwipeActivity.class));
                } else {
                    Snackbar.make(binding.etEmail, R.string.loginFailed, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.btnSignup, view1 -> {
                                Log.d("Snack", "showInfo: SnackBarMore");
                                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                                startActivity(i);
                            })
                            .show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        }, email, pass);
    }

    /**
     * checks if the user is submitting the correct credentials and validates the user
     *
     * @param view
     */
    public void CheckLogin(View view) throws URISyntaxException {
        String email = binding.etEmail.getText().toString().trim();
        String pass = binding.etPassword.getText().toString().trim();
        if (email.length() == 0 || pass.length() == 0) {
            Toast.makeText(LoginActivity.this, R.string.errorEmptyStrings, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        ApiController.getInstance().login(new VolleyCallBack<User>() {
            @Override
            public void onResponse(User p) {
                if (p.getName() != null) {
                    Toast.makeText(LoginActivity.this, R.string.loginSuccess, Toast.LENGTH_SHORT)
                            .show();
                    mPrefs.saveUser(email, pass);
                    startActivity(new Intent(LoginActivity.this, SwipeActivity.class));
                    finish();
                } else {
                    Snackbar.make(binding.etEmail, R.string.loginFailed, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.btnSignup, view1 -> {
                                Log.d("Snack", "showInfo: SnackBarMore");
                                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                                startActivity(i);
                            })
                            .show();
                }
            }

            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, R.string.loginFailed, Toast.LENGTH_SHORT)
                        .show();
            }
        }, email, pass);

    }

}