package xyz.nafnaneistar.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
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
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import xyz.nafnaneistar.activities.items.ComboListItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
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
        binding.btnApprove.setOnClickListener(this::onClick);
        binding.btnDislike.setOnClickListener(this::onClick);
        binding.cbGenderMale.setOnClickListener(view -> {
            try {
                getNewName();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        binding.cbGenderFemale.setOnClickListener(view -> {
            try {
                getNewName();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
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
            navbar = new NavbarFragment();
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
        if (currentCard == null) return;
        int currentID = currentCard.getId();
        initLoading();
        String action = "disapprove";
        if (view.getId() == R.id.btnApprove) action = "approve";
        currentCard = null; //Núllað út þannig að það sé ekki hægt að spamma bara hægri og búa til requests
        ApiController.getInstance().chooseName(action, currentID, binding.cbGenderMale.isChecked(),
                binding.cbGenderFemale.isChecked(), this, new VolleyCallBack<NameCard>() {
                    @Override
                    public ArrayList<ComboListItem> onSuccess() {
                        return null;
                    }

                    @Override
                    public void onResponse(NameCard nc) {
                        binding.llLoadingContainer.setVisibility(View.INVISIBLE);
                        binding.tvTexti.setText(nc.getDescription());
                        SpannableStringBuilder ssb = new SpannableStringBuilder(nc.getName() + "  ");
                        if (nc.getGender() == 0)
                            ssb.setSpan(new ImageSpan(getApplicationContext(), R.drawable.ic_gender_male, DynamicDrawableSpan.ALIGN_CENTER), nc.getName().length() + 1, nc.getName().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (nc.getGender() == 1)
                            ssb.setSpan(new ImageSpan(getApplicationContext(), R.drawable.ic_gender_female, DynamicDrawableSpan.ALIGN_CENTER), nc.getName().length() + 1, nc.getName().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        binding.tvName.setText(ssb, TextView.BufferType.SPANNABLE);
                        currentCard = nc;

                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(SwipeActivity.this, error, Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    public void getNewName() throws URISyntaxException {
        initLoading();
        ApiController.getInstance().getNewName(binding.cbGenderMale.isChecked(), binding.cbGenderFemale.isChecked(),
                this, new VolleyCallBack<NameCard>() {
                    @Override
                    public ArrayList<ComboListItem> onSuccess() {
                        return null;
                    }
                    @Override
                    public void onResponse(NameCard nc) {
                        binding.llLoadingContainer.setVisibility(View.INVISIBLE);
                        binding.tvTexti.setText(nc.getDescription());
                        currentCard = nc;
                        SpannableStringBuilder ssb = new SpannableStringBuilder(nc.getName() + "  ");
                        int icon = (nc.getGender() == 1 ) ? (R.drawable.ic_gender_female) :  (R.drawable.ic_gender_male);
                        ssb.setSpan(new ImageSpan(getApplicationContext(),icon, DynamicDrawableSpan.ALIGN_CENTER), nc.getName().length() + 1, nc.getName().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        binding.tvName.setText(ssb, TextView.BufferType.SPANNABLE);
                    }
                    @Override
                    public void onError(String error) {
                        Toast.makeText(SwipeActivity.this, error, Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }

    private void initLoading() {
        binding.llLoadingContainer.setVisibility(View.VISIBLE);
        binding.tvName.setText("");
        binding.tvTexti.setText("");
    }

    private void onClick(View view) {
        try {
            chooseName(view);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
