package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.model.User;
import xyz.nafnaneistar.loginactivity.databinding.ActivityLoginBinding;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private  User currentUser = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.btnLogin.setOnClickListener(this::CheckLogin);
        binding.btnSignup.setOnClickListener(this::Signup);


    }

    public void Signup(View view){
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
    }

    public void CheckLogin(View view){
        String email = binding.etEmail.getText().toString().trim();
        String pass = binding.etPassword.getText().toString().trim();
        if(email.length() == 0 || pass.length()==0){
            binding.tvError.setText("Textareitur má ekki vera tómur");
            binding.tvError.setVisibility(View.VISIBLE);
            return;
        }
        String loginUrl = ApiController.getDomainURL()+"login/check?email=" +email+"&password="+pass;
        JsonObjectRequest jsonArrReq = new JsonObjectRequest(Request.Method.GET,loginUrl,null,
                response -> {
                    Gson g = new Gson();
                    User p = g.fromJson(String.valueOf(response), User.class);
                    if(p.getName() != null){

                        Toast.makeText(LoginActivity.this, R.string.loginSuccess ,Toast.LENGTH_SHORT)
                                .show();
                    }
                    else {
                        binding.tvError.setText(R.string.loginFailed);
                        binding.tvError.setVisibility(View.VISIBLE);
                        Snackbar.make(binding.etEmail,R.string.loginFailed, Snackbar.LENGTH_LONG)
                                .setAction(R.string.btnSignup, view1 -> {
                                    Log.d("Snack", "showInfo: SnackBarMore");
                                })
                                .show();
                    }

                },error -> {
                binding.tvError.setText("Innskráning Mistókst!");
                Log.d("Test", "CheckLogin: " + error.toString());
        });
            ApiController.getInstance().addToRequestQueue(jsonArrReq);

    }
}