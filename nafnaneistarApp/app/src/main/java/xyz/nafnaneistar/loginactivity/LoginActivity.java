package xyz.nafnaneistar.loginactivity;

import androidx.appcompat.app.AppCompatActivity;
import xyz.nafnaneistar.loginactivity.controller.ApiController;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

public class LoginActivity extends AppCompatActivity {
    String testUrl = "http://192.168.1.207:7979/login/check?email=test@test.is&password=tt";
    String test2Url = "http://nafnaneistar.xyz/swipe/newname";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        JsonObjectRequest jsonArrReq = new JsonObjectRequest(Request.Method.GET,testUrl,null,
                response -> {
                    Log.d("test",   "onCreate: " + response.toString());
                },error -> {
                    Log.d("test",   "onCreate: " + error.toString());
        });

        ApiController.getInstance().addToRequestQueue(jsonArrReq);
    }
}