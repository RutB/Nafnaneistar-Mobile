package xyz.nafnaneistar.activities.LinkedPartnerFragments;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import xyz.nafnaneistar.activities.ViewLikedFragments.ComboListFragment;
import xyz.nafnaneistar.activities.ViewLikedFragments.ComboListManagerFragment;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.FragmentComboListBinding;

public class LinkComboListFragment extends Fragment implements  AdapterView.OnItemSelectedListener {
    FragmentComboListBinding binding;
    FragmentManager fragmentManager;
    Activity context;
    Prefs prefs;
    ArrayList<Long> partnerIds = new ArrayList<>();
    Long currentPartnerId = Long.valueOf(-1);

    public LinkComboListFragment() {
        // Required empty public constructor
    }

    public static LinkComboListFragment newInstance() {
        LinkComboListFragment fragment = new LinkComboListFragment();
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (partnerIds.size() > 0) {
            try {
                Log.d("spinner", "onItemSelected: " + adapterView.getItemAtPosition(i));
                Log.d("spinner", "onItemSelected: " + partnerIds.get(i - 1));
                currentPartnerId = partnerIds.get(i - 1);

            } catch (Exception e) {
                Log.d("spinner", "onItemSelected: " + adapterView.getItemAtPosition(i));
                Log.d("spinner", "onItemSelected: " + e.getMessage());
            }


        }


    }
    public void openListView(View view) {
        Fragment f = fragmentManager.findFragmentById(R.layout.link_fragment_combo_list_manager);
        if (f == null) {
            f = new ComboListManagerFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("partnerId",currentPartnerId);
            f.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(R.id.viewLikedContainer, f, "listViewCombo")
                    .commit();
        }
    }
}