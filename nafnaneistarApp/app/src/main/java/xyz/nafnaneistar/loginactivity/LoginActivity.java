package xyz.nafnaneistar.loginactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;

import xyz.nafnaneistar.loginactivity.controller.ApiController;
import xyz.nafnaneistar.loginactivity.data.User;
import xyz.nafnaneistar.loginactivity.databinding.ActivityLoginBinding;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    String test2Url = "http://nafnaneistar.xyz/swipe/newname";
    private Button bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        binding.btnLogin.setOnClickListener(view1 -> {

            CheckLogin(view1);
        });

    }

    public void CheckLogin(View view,String url){
        Log.d("Test", "CheckLogin: Button Clicked");
        String email = binding.etEmail.getText().toString().trim();
        String pass = binding.etPassword.getText().toString().trim();
        String loginUrl = "http://192.168.1.207:7979/login/check?email="+email+"&password="+pass;
        JsonObjectRequest jsonArrReq = new JsonObjectRequest(Request.Method.GET,url,null,
                response -> {
                    Log.d("Test", "CheckLogin: " + response.toString());
                    Gson g = new Gson();
                    User p = g.fromJson(String.valueOf(response), User.class);
                    Log.d("Test", "CheckLogin: " + p.getName());
                    if(p.getName()){
                        binding.tvError.setText("Innskráning Tókst!");
                    }
                    else {
                        binding.tvError.setText("Innskráning Mistókst!");
                    }
                },error -> {
                binding.tvError.setText("Innskráning Mistókst!");
                Log.d("Test", "CheckLogin: " + error.toString());
        });
            ApiController.getInstance().addToRequestQueue(jsonArrReq);

    }
}