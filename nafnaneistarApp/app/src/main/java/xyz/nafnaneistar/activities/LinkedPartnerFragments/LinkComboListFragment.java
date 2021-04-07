package xyz.nafnaneistar.activities.LinkedPartnerFragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import xyz.nafnaneistar.activities.ViewLikedFragments.ComboListFragment;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.FragmentComboListBinding;

public class LinkComboListFragment extends Fragment implements  AdapterView.OnItemSelectedListener {
    FragmentComboListBinding binding;
    FragmentManager fragmentManager;
    Activity context;
    Prefs prefs;
    ArrayList<Long> partnerIds = new ArrayList<>();
    Long currentSelectedPartnerId = Long.valueOf(-1);
    public ComboListFragment() {
        // Required empty public constructor
    }
    public static ComboListFragment newInstance() {
        ComboListFragment fragment = new ComboListFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        prefs = new Prefs(context);
        fragmentManager = getParentFragmentManager();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_combo_list, container, false);

        binding.tvComboNameTitle.setText(R.string.viewlikedComboList);
        View view = binding.getRoot();
        ArrayList<String> partners = new ArrayList<>();
        partners.add("Sæki lista...");
        populateSpinner(partners);
        getPartnerList(partners);
        binding.spPartners.setOnItemSelectedListener(this);
        binding.btnConfirmPartner.setOnClickListener(this::openListView);
        return view;
    }

    public void fillTable()

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
