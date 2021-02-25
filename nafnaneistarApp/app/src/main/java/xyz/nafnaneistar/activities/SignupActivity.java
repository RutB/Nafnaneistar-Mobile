package xyz.nafnaneistar.activities;

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
import xyz.nafnaneistar.model.User;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        binding.btnLogin2.setOnClickListener(this::Login);
        binding.btnSignup2.setOnClickListener(this::SignupUser);
    }

    public void Login(View view){
        onBackPressed();
    }

    public void SignupUser(View view){
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String pass = binding.etPassword.getText().toString();
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        if(name.length() == 0 || email.length() == 0 | pass.length() == 0){
            binding.tvError.setText(R.string.errorEmptyStrings);
            binding.tvError.setVisibility(View.VISIBLE);
            return;
        }
        else if(!matcher.matches()){
            binding.tvError.setText(R.string.errorInvalidEmail);
            binding.tvError.setVisibility(View.VISIBLE);
            return;
        }
        else {
            binding.tvError.setVisibility(View.INVISIBLE);
        }
        String signupUrl = String.format("%ssignup?name=%s&email=%s&password=%s", ApiController.getDomainURL(),name, email, pass);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,signupUrl,null,
                response -> {
                    Gson g = new Gson();
                    User p = g.fromJson(String.valueOf(response), User.class);
                    System.out.println(response.toString());
                    if(p.getName() != null){
                        Toast.makeText(SignupActivity.this, R.string.signupSuccess ,Toast.LENGTH_SHORT)
                                .show();
                    }
                    else {
                        String message = null;
                        try {
                            message = response.getString("message");
                            binding.tvError.setText(message);
                        } catch (JSONException e) {
                            binding.tvError.setText("Nýskráning Mistókt");
                            e.printStackTrace();
                        }
                        binding.tvError.setVisibility(View.VISIBLE);
                        Log.d("fetch", "SignupUser: "+ response.toString());
                    }

                },error -> {
            binding.tvError.setText("Nýskráning Mistókst!");
            binding.tvError.setVisibility(View.VISIBLE);
            Log.d("Test", "CheckLogin: " + error.toString());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);

    }
}