package xyz.nafnaneistar.activities.LinkedPartnerFragments;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.databinding.FragmentApprovedListManagerBinding;

public class linkListManagerFragment extends Fragment implements LinkRecyclerViewAdapter.OnItemListener {
    private FragmentApprovedListManagerBinding binding;
    private Prefs prefs;
    static ApprovedListCardRecyclerViewAdapter adapter;
    private ArrayList<NameCardItem> approvedList;
    private ArrayList<NameCardItem> approvedListAll = new ArrayList<>();
    private RecyclerView recyclerView;

    private int sortingSwitchState = 0;
    private int genderSwitchState = 0;


    public ApprovedNameListManagerFragment() {
        // Required empty public constructor
    }
}

