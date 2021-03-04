package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivitySwipeBinding;
import xyz.nafnaneistar.model.NameCard;
import xyz.nafnaneistar.model.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Set;

public class SwipeActivity extends AppCompatActivity {
    private ActivitySwipeBinding binding;
    private Prefs prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_swipe);
        prefs = new Prefs(SwipeActivity.this);
        //Initialize the navbar fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null){
            navbar = new NavbarFragment(SwipeActivity.this);
            fragmentManager.beginTransaction()
                    .add(R.id.SwipeContainer, navbar)
                    .commit();
        }

        getNewName();


    }

    public void loader(){

    }

    public void getNewName(){
        String[] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String loginUrl = ApiController.getDomainURL()+"swipe/newname?email=" +email+"&password="+pass;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,loginUrl,null,
                response -> {
                    Log.d("respnse", "getNewName: "+response.toString());
                    Gson g = new Gson();
                    NameCard nc = g.fromJson(String.valueOf(response), NameCard.class);

                },error -> {
                    Toast.makeText(SwipeActivity.this, "Kerfisvilla" ,Toast.LENGTH_LONG)
                    .show();
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }
}