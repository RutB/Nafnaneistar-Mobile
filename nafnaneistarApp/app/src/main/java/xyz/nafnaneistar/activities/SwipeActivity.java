package xyz.nafnaneistar.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.helpers.OnSwipeTouchListener;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivitySwipeBinding;
import xyz.nafnaneistar.model.NameCard;

public class SwipeActivity extends AppCompatActivity {
    private ActivitySwipeBinding binding;
    private Prefs prefs;
    private NameCard currentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_swipe);
        binding.btnDislike.setForeground(getDrawable(R.drawable.ic_arrow_dislike));
        prefs = new Prefs(SwipeActivity.this);
        binding.btnApprove.setOnClickListener(this::onClick2);
        binding.btnDislike.setOnClickListener(this::onClick);
        binding.SwipeContainer.setOnTouchListener(new OnSwipeTouchListener(SwipeActivity.this) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                binding.btnApprove.callOnClick();
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                binding.btnDislike.callOnClick();
            }
        });

        binding.scrollView2.setOnTouchListener(new OnSwipeTouchListener(SwipeActivity.this) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                binding.btnApprove.callOnClick();
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                binding.btnDislike.callOnClick();
            }
        });
        //Initialize the navbar fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null) {
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

    public void chooseName(View view) throws URISyntaxException {
        String[] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String action = "disapprove";
        if (view.getId() == R.id.btnApprove) action = "approve";
        String listeningPath = "swipe/" + action;
        URIBuilder b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        b.addParameter("id", String.valueOf(currentCard.getId()));
        b.addParameter("email", email);
        b.addParameter("pass", pass);
        if (binding.cbGenderFemale.isChecked())
            b.addParameter("female", "true");
        if (binding.cbGenderMale.isChecked())
            b.addParameter("male", "true");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, b.build().toString(), null,
                response -> {
                    Gson g = new Gson();
                    NameCard nc = g.fromJson(String.valueOf(response), NameCard.class);
                    ///kanna hvort er fyrir nafn
                    binding.tvTexti.setText(nc.getDescription());
                    SpannableStringBuilder ssb = new SpannableStringBuilder(nc.getName() + "  ");

                    if (nc.getGender() == 0)
                        ssb.setSpan(new ImageSpan(getApplicationContext(), R.drawable.ic_gender_male, DynamicDrawableSpan.ALIGN_CENTER), nc.getName().length() + 1, nc.getName().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (nc.getGender() == 1)
                        ssb.setSpan(new ImageSpan(getApplicationContext(), R.drawable.ic_gender_female, DynamicDrawableSpan.ALIGN_CENTER), nc.getName().length() + 1, nc.getName().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.tvName.setText(ssb, TextView.BufferType.SPANNABLE);
                    currentCard = nc;

                }, error -> {
            Toast.makeText(SwipeActivity.this, "Kerfisvilla", Toast.LENGTH_LONG)
                    .show();
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void getNewName() throws URISyntaxException {
        String[] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "swipe/newname";
        URIBuilder b = new URIBuilder(ApiController.getDomainURL() + listeningPath);

        b.addParameter("email", email);
        b.addParameter("pass", pass);
        if (binding.cbGenderFemale.isChecked())
            b.addParameter("female", "true");
        if (binding.cbGenderMale.isChecked())
            b.addParameter("male", "true");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, b.build().toString(), null,
                response -> {
                    Gson g = new Gson();
                    NameCard nc = g.fromJson(String.valueOf(response), NameCard.class);
                    binding.tvTexti.setText(nc.getDescription());
                    currentCard = nc;
                    SpannableStringBuilder ssb = new SpannableStringBuilder(nc.getName() + "  ");
                    if (nc.getGender() == 0)
                        ssb.setSpan(new ImageSpan(getApplicationContext(), R.drawable.ic_gender_male, DynamicDrawableSpan.ALIGN_CENTER), nc.getName().length() + 1, nc.getName().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (nc.getGender() == 1)
                        ssb.setSpan(new ImageSpan(getApplicationContext(), R.drawable.ic_gender_female, DynamicDrawableSpan.ALIGN_CENTER), nc.getName().length() + 1, nc.getName().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.tvName.setText(ssb, TextView.BufferType.SPANNABLE);
                }, error -> {
            Toast.makeText(SwipeActivity.this, "Kerfisvilla", Toast.LENGTH_LONG)
                    .show();
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void onClick(View view) {
        try {
            chooseName(view);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void onClick2(View view) {
        try {
            chooseName(view);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}