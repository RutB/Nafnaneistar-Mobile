package xyz.nafnaneistar.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.model.User;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivitySignupBinding;

/**
 * The Activity that handles the signup part of the app
 */
public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        binding.btnLogin2.setOnClickListener(this::Login);
        binding.btnSignup2.setOnClickListener(view -> {
            try {
                SignupUser(view);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        prefs = new Prefs(SignupActivity.this);

    }

    /**
     * if the button near i already have account is clicked the user is taken to the
     * login activity
     * @param view
     */
    public void Login(View view){
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        finish();
        startActivity(i);
    }

    /**
     * to ensure a more normal flow the login activity is created again if the user presses back
     * on the signup activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        finish();
        startActivity(i);
    }

    /**
     * The Function that is executed when the user tries to create account
     * it makes sure that the edit text views contain text and then it creates a jsonObjectRequest
     * andit gets added to the ApiController queue
     * @param view
     */
    public void SignupUser(View view) throws URISyntaxException {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String pass = binding.etPassword.getText().toString();
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        if(name.length() == 0 || email.length() == 0 | pass.length() == 0){
            Toast.makeText(SignupActivity.this, R.string.errorEmptyStrings ,Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        else if(!matcher.matches()){
            Toast.makeText(SignupActivity.this, R.string.errorInvalidEmail ,Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        ApiController.getInstance().signup(new VolleyCallBack<JSONObject>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(JSONObject response) {
                Gson g = new Gson();
                User p = g.fromJson(String.valueOf(response), User.class);
                if(p.getName() != null){
                    Toast.makeText(SignupActivity.this, R.string.signupSuccess ,Toast.LENGTH_SHORT)
                            .show();
                    prefs.saveUser(email, pass);
                    finish();
                    startActivity(new Intent( SignupActivity.this, SwipeActivity.class));
                }
                else {
                    if(response.has("message")){
                        try {
                            Toast.makeText(SignupActivity.this, response.getString("message") ,Toast.LENGTH_SHORT)
                                    .show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SignupActivity.this, R.string.signupFailed ,Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                    else {
                        Toast.makeText(SignupActivity.this, R.string.signupFailed ,Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                try {
                    ApiController.getInstance().checkNotifications((Activity) binding.btnLogin2.getContext());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SignupActivity.this, R.string.systemError ,Toast.LENGTH_SHORT)
                        .show();

            }
        },name,email,pass);
    }
}