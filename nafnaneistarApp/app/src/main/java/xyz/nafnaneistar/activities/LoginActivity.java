package xyz.nafnaneistar.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.model.User;
import xyz.nafnaneistar.loginactivity.databinding.ActivityLoginBinding;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private  User currentUser = new User();
    private final int REQUEST_CODE = 2;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Prefs(LoginActivity.this);
        String[] user;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        if(prefs.getUser().length == 2){
            user = prefs.getUser();
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

        //prefs.Logout();


    }

    /**
     * if the user taps signup then we close the login activity and switch to signupactivity
     * @param view
     */
    public void Signup(View view){
        Intent i = new Intent(LoginActivity.this, SignupActivity.class);
        finish();
        startActivity(i);
    }

    /**
     * checks if the user is already loggedn in
     * @param user
     */
    public void CheckLogin(String[] user) throws URISyntaxException {
        Log.d("user", "CheckLogin: " + user);
        if (user.length != 2){
            return;
        }
        String email = user[0];
        String pass = user[1];
        String listeningPath = "login/check";
        URIBuilder b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
        b.addParameter("email",email);
        b.addParameter("pass",pass);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,b.build().toString(),null,
                response -> {
                    Gson g = new Gson();
                    User p = g.fromJson(String.valueOf(response), User.class);
                    if(p.getName() != null){
                        Intent i = new Intent( LoginActivity.this, SwipeActivity.class);
                        finish();
                        startActivity(i);
                    }
                },error -> {
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * checks if the user is submitting the correct credentials and validates the user
     * @param view
     */
    public void CheckLogin(View view) throws URISyntaxException {
        String email = binding.etEmail.getText().toString().trim();
        String pass = binding.etPassword.getText().toString().trim();
        if(email.length() == 0 || pass.length()==0){
            Toast.makeText(LoginActivity.this, R.string.errorEmptyStrings ,Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        String listeningPath = "login/check";
        URIBuilder b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
        b.addParameter("email",email);
        b.addParameter("pass",pass);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,b.build().toString(),null,
                response -> {
                    Gson g = new Gson();
                    User p = g.fromJson(String.valueOf(response), User.class);
                    if(p.getName() != null){
                        Toast.makeText(LoginActivity.this, R.string.loginSuccess ,Toast.LENGTH_SHORT)
                                .show();
                        prefs.saveUser(email,pass);
                        startActivity(new Intent( LoginActivity.this, SwipeActivity.class));
                    }
                    else {
                        Snackbar.make(binding.etEmail,R.string.loginFailed, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.btnSignup, view1 -> {
                                    Log.d("Snack", "showInfo: SnackBarMore");
                                    Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                                    startActivity(i);

                                })
                                .show();
                    }

                },error -> {
                Toast.makeText(LoginActivity.this, R.string.loginFailed ,Toast.LENGTH_SHORT)
                    .show();
        });
            ApiController.getInstance().addToRequestQueue(jsonObjReq);

    }

}