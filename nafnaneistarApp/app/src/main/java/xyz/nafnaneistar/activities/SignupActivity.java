package xyz.nafnaneistar.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONException;

import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.model.User;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        binding.btnLogin2.setOnClickListener(this::Login);
        binding.btnSignup2.setOnClickListener(this::SignupUser);
        prefs = new Prefs(SignupActivity.this);


    }

    public void Login(View view){
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        finish();
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        finish();
        startActivity(i);
    }

    public void SignupUser(View view){
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
        String signupUrl = String.format("%ssignup?name=%s&email=%s&password=%s", ApiController.getDomainURL(),name, email, pass);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,signupUrl,null,
                response -> {
                    Gson g = new Gson();
                    User p = g.fromJson(String.valueOf(response), User.class);
                    if(p.getName() != null){
                        Toast.makeText(SignupActivity.this, R.string.signupSuccess ,Toast.LENGTH_SHORT)
                                .show();
                        prefs.saveUser(email, pass);
                        Intent intent = getIntent();
                        intent.putExtra("close","close");
                        setResult(RESULT_OK, intent);
                        finish();
                        startActivity(new Intent( SignupActivity.this, SwipeActivity.class));
                    }
                    else {
                        String message = null;
                        try {
                            message = response.getString("message");
                            Toast.makeText(SignupActivity.this, message ,Toast.LENGTH_SHORT)
                                    .show();
                        } catch (JSONException e) {
                            Toast.makeText(SignupActivity.this, R.string.errorEmptyStrings ,Toast.LENGTH_SHORT)
                                    .show();
                            e.printStackTrace();
                        }
                    }

                },error -> {
            Toast.makeText(SignupActivity.this, R.string.errorEmptyStrings ,Toast.LENGTH_SHORT)
                    .show();
            Log.d("Test", "CheckLogin: " + error.toString());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);

    }
}