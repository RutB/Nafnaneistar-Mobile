package xyz.nafnaneistar.activities;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.FragmentNavbarBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NavbarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavbarFragment extends Fragment {
    FragmentNavbarBinding binding;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_navbar,container,false);
        binding.navViewDrawer.setVisibility(View.INVISIBLE);
        binding.btnNavToggle.setOnClickListener(this::ToggleNavDrawer);
        View view = binding.getRoot();
        return view;
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