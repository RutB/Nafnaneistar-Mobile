package xyz.nafnaneistar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_navbar,container,false);
        binding.navViewDrawer.setVisibility(View.INVISIBLE);
        binding.navViewDrawer.bringToFront();
        binding.btnNavToggle.bringToFront();
        binding.btnNavToggle.setOnClickListener(this::ToggleNavDrawer);
        binding.tvSettings.setOnClickListener(this::logout);
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
        if(binding.navViewDrawer.getVisibility() == View.VISIBLE)
            binding.navViewDrawer.setVisibility(View.INVISIBLE);
        else
            binding.navViewDrawer.setVisibility(View.VISIBLE);
    }
}