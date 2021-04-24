package xyz.nafnaneistar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
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

    public NavbarFragment() {
        // Required empty public constructor

    }

    public static NavbarFragment newInstance() {
        NavbarFragment fragment = new NavbarFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        prefs = new Prefs(context);
    }

    /**
     *
     * @param view
     * @return true if the activity
     */
    public Boolean onNavigationItemSelected(MenuItem view){
        Log.d("partners", "SwitchIntent: " + "click?");
        switch (view.getItemId()){
            case R.id.navSwipe:
                if(!getActivity().getLocalClassName().toString().contains("SwipeActivity")){
                    getActivity().finish();
                    startActivity(new Intent(getContext(),SwipeActivity.class));
                    return true;
                }
            break;
            case R.id.navViewLiked:
                if(getActivity().getLocalClassName().contains("ViewLikedActivity")){
                    removeFragment("listViewCombo");
                }
                if(!getActivity().getLocalClassName().contains("ViewLikedActivity")){
                    getActivity().finish();
                    startActivity(new Intent(getContext(),ViewLikedActivity.class));
                    return true;
                }
            break;
            case R.id.navLinkPartner:
                if(!getActivity().getLocalClassName().contains("LinkPartnerActivity")){
                    getActivity().finish();
                    startActivity(new Intent(getContext(),LinkPartnerActivity.class));
                    return true;
                }
                break;
            case R.id.navMenu:
                ToggleNavDrawer();
                return true;
            case R.id.navSearch:
                if (!getActivity().getLocalClassName().contains("SearchActivity")){
                    getActivity().finish();
                    startActivity(new Intent(getContext(),SearchActivity.class));
                    return true;
                }
            break;
            case R.id.navSettings:
                if (!getActivity().getLocalClassName().contains("SettingsActivity")){
                    getActivity().finish();
                    startActivity(new Intent(getContext(),SettingsActivity.class));
                    return true;
                }
                break;
            case R.id.navLogout:
                logout();
                return true;
        }
        return false;
    }

    /**
     * Removes fragment in relation to tag
     * @param tag
     */
    private void removeFragment(String tag) {
        Fragment f = getParentFragmentManager().findFragmentByTag(tag);
        if (f != null) {
            getParentFragmentManager().beginTransaction()
                    .remove(f)
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_navbar, container, false);
        binding.navViewDrawer.setVisibility(View.INVISIBLE);
        binding.navViewDrawer.bringToFront();
        binding.navViewDrawer.setZ(90);
        binding.navViewTop.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        binding.navViewDrawer.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        View view = binding.getRoot();
        return view;
    }

    /**
     * Logs out current user and initializes new intent
     */
    public void logout() {
        prefs.Logout();
        context.finish();
        Intent i = new Intent(context, LoginActivity.class);
        startActivity(i);
    }

    /**
     * When the menu button is pressed then we toggle the navmenu to the left
     */
    public void ToggleNavDrawer() {
        if (binding.navViewDrawer.getVisibility() == View.VISIBLE) {
            Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out);
            binding.navViewDrawer.startAnimation(slideOut);
            binding.navViewDrawer.setVisibility(View.INVISIBLE);
        } else {
            Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
            binding.navViewDrawer.startAnimation(slideIn);
            binding.navViewDrawer.setVisibility(View.VISIBLE);
        }
    }

}