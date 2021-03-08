package xyz.nafnaneistar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.FragmentNavbarBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NavbarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavbarFragment extends Fragment {
    FragmentNavbarBinding binding;
    Activity context;
    private Prefs prefs;

    public NavbarFragment(Activity context) {
        // Required empty public constructor
        this.context = context;
        prefs = new Prefs(context);
    }


    public static NavbarFragment newInstance(Activity context) {
        NavbarFragment fragment = new NavbarFragment(context);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    public void createIcons(){
        String searchName = getString(R.string.navbarSearchname);
        SpannableStringBuilder searchNamessb = new SpannableStringBuilder(searchName+"  ");
        searchNamessb.setSpan(new ImageSpan(getContext(), R.drawable.ic_search, DynamicDrawableSpan.ALIGN_CENTER),searchName.length()+1,searchName.length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvSearchName.setText(searchNamessb, TextView.BufferType.SPANNABLE);

        String selectNames = getString(R.string.navbarSwipe);
        SpannableStringBuilder selectNamesssb = new SpannableStringBuilder(selectNames+"  ");
        selectNamesssb.setSpan(new ImageSpan(getContext(), R.drawable.ic_swipe, DynamicDrawableSpan.ALIGN_CENTER),selectNames.length()+1,selectNames.length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvSwipe.setText(selectNamesssb, TextView.BufferType.SPANNABLE);

        String viewLiked = getString(R.string.navbarvviewliked);
        SpannableStringBuilder viewLikedssb = new SpannableStringBuilder(viewLiked+"  ");
        viewLikedssb.setSpan(new ImageSpan(getContext(), R.drawable.ic_star, DynamicDrawableSpan.ALIGN_CENTER),viewLiked.length()+1,viewLiked.length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvViewLiked.setText(viewLikedssb, TextView.BufferType.SPANNABLE);

        String linkPartner = getString(R.string.navbarLinkPartner);
        SpannableStringBuilder linkPartnerssb = new SpannableStringBuilder(linkPartner+"  ");
        linkPartnerssb.setSpan(new ImageSpan(getContext(), R.drawable.ic_partner, DynamicDrawableSpan.ALIGN_CENTER),linkPartner.length()+1,linkPartner.length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvLinkPartner.setText(linkPartnerssb, TextView.BufferType.SPANNABLE);

        String settings = getString(R.string.navbarSettings);
        SpannableStringBuilder settingsssb = new SpannableStringBuilder(settings+"  ");
        settingsssb.setSpan(new ImageSpan(getContext(), R.drawable.ic_settings, DynamicDrawableSpan.ALIGN_CENTER),settings.length()+1,settings.length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvSettings.setText(settingsssb, TextView.BufferType.SPANNABLE);

        String logout = getString(R.string.navLogout);
        SpannableStringBuilder logoutssb = new SpannableStringBuilder(logout+"  ");
        logoutssb.setSpan(new ImageSpan(getContext(), R.drawable.ic_logout, DynamicDrawableSpan.ALIGN_CENTER),logout.length()+1,logout.length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvLogOut.setText(logoutssb, TextView.BufferType.SPANNABLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_navbar,container,false);
        binding.navViewDrawer.setVisibility(View.INVISIBLE);
        binding.navViewDrawer.bringToFront();
        binding.btnNavToggle.bringToFront();
        createIcons();
        binding.btnNavToggle.setOnClickListener(this::ToggleNavDrawer);
        binding.tvLogOut.setOnClickListener(this::logout);
        View view = binding.getRoot();
        return view;
    }

    public void logout(View view){
        prefs.Logout();
        context.finish();
        Intent i = new Intent(context,LoginActivity.class);
        startActivity(i);
    }


    /**
     * When the menu button is pressed then we toggle the navmenu to the left
     * @param view
     */
    public void ToggleNavDrawer(View view) {

        if (binding.navViewDrawer.getVisibility() == View.VISIBLE){
            Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out);
            binding.navViewDrawer.startAnimation(slideOut);
            binding.navViewDrawer.setVisibility(View.INVISIBLE);
        }
        else {
            Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
            binding.navViewDrawer.startAnimation(slideIn);
            binding.navViewDrawer.setVisibility(View.VISIBLE);
        }
    }

}