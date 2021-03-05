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
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.util.Set;

public class SwipeActivity extends AppCompatActivity {
    private ActivitySwipeBinding binding;
    private Prefs prefs;
    private NameCard currentCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_swipe);
        prefs = new Prefs(SwipeActivity.this);
        binding.btnApprove.setOnClickListener(view -> {
            try {
                chooseName(view);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        binding.btnDislike.setOnClickListener(view -> {
            try {
                chooseName(view);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        //Initialize the navbar fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null){
            navbar = new NavbarFragment(SwipeActivity.this);
            fragmentManager.beginTransaction()
                    .add(R.id.SwipeContainer, navbar)
                    .commit();
        }
        try {
            getNewName();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    public void loader(){

    }
    public void chooseName(View view) throws URISyntaxException {
        String[] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "swipe/newname";
        URIBuilder b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
        b.addParameter("email",email);
        b.addParameter("pass",pass);
        if(binding.cbGenderFemale.isChecked())
            b.addParameter("female","true");
        if(binding.cbGenderMale.isChecked())
            b.addParameter("male","true");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,b.build().toString(),null,
                response -> {
                    Gson g = new Gson();
                    NameCard nc = g.fromJson(String.valueOf(response), NameCard.class);
                    binding.tvTexti.setText(nc.getDescription());
                    SpannableStringBuilder ssb = new SpannableStringBuilder(nc.getName()+"  ");

                    if(nc.getGender() == 0)
                        ssb.setSpan(new ImageSpan(getApplicationContext(), R.drawable.ic_gender_male, DynamicDrawableSpan.ALIGN_CENTER),nc.getName().length()+1,nc.getName().length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if(nc.getGender() == 1)
                        ssb.setSpan(new ImageSpan(getApplicationContext(), R.drawable.ic_gender_female, DynamicDrawableSpan.ALIGN_CENTER),nc.getName().length()+1,nc.getName().length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.tvName.setText(ssb, TextView.BufferType.SPANNABLE);
                    currentCard = nc;

                },error -> {
            Toast.makeText(SwipeActivity.this, "Kerfisvilla" ,Toast.LENGTH_LONG)
                    .show();
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void getNewName() throws URISyntaxException {
        String[] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        URIBuilder b = new URIBuilder(ApiController.getDomainURL()+"swipe/newname");
        b.addParameter("email",email);
        b.addParameter("pass",pass);
        if(binding.cbGenderFemale.isChecked())
            b.addParameter("female","true");
        if(binding.cbGenderMale.isChecked())
            b.addParameter("male","true");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,b.build().toString(),null,
                response -> {
                    Gson g = new Gson();
                    NameCard nc = g.fromJson(String.valueOf(response), NameCard.class);
                    binding.tvTexti.setText(nc.getDescription());
                    currentCard = nc;
                    SpannableStringBuilder ssb = new SpannableStringBuilder(nc.getName()+"  ");

                    if(nc.getGender() == 0)
                        ssb.setSpan(new ImageSpan(getApplicationContext(), R.drawable.ic_gender_male, DynamicDrawableSpan.ALIGN_CENTER),nc.getName().length()+1,nc.getName().length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if(nc.getGender() == 1)
                        ssb.setSpan(new ImageSpan(getApplicationContext(), R.drawable.ic_gender_female, DynamicDrawableSpan.ALIGN_CENTER),nc.getName().length()+1,nc.getName().length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.tvName.setText(ssb, TextView.BufferType.SPANNABLE);

                },error -> {
                    Toast.makeText(SwipeActivity.this, "Kerfisvilla" ,Toast.LENGTH_LONG)
                    .show();
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }
}