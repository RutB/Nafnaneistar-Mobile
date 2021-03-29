package xyz.nafnaneistar.activities.ViewLikedFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import xyz.nafnaneistar.activities.NavbarFragment;
import xyz.nafnaneistar.activities.SwipeActivity;
import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.FragmentApprovedListManagerBinding;
import xyz.nafnaneistar.loginactivity.databinding.FragmentComboListManagerBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ApprovedNameListManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApprovedNameListManagerFragment extends Fragment implements  ApprovedListCardRecyclerViewAdapter.OnItemListener {
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

    public static ApprovedNameListManagerFragment newInstance() {
        ApprovedNameListManagerFragment fragment = new ApprovedNameListManagerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Prefs(getActivity());
        approvedList = new ArrayList<>();
    }




    private void setAdapater() {
        adapter = new ApprovedListCardRecyclerViewAdapter(approvedList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        binding.rvComboList.setItemAnimator(new DefaultItemAnimator());
        binding.rvComboList.setLayoutManager(layoutManager);
        binding.rvComboList.setAdapter(adapter);
        binding.swOrderBy.setOnCheckedChangeListener(this::onCheckedChanged);
        binding.swToggleGender.setOnCheckedChangeListener(this::onGenderCheckedChange);
    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            sortByRating(approvedList);
            sortingSwitchState = 1;
        } else {
            sortByName(approvedList);
            sortingSwitchState = 0;
        }
    }

    public void onGenderCheckedChange(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            filterByGender(approvedList,0);
            genderSwitchState = 0;
        } else {
            filterByGender(approvedList,1);
            genderSwitchState = 1;
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_approved_list_manager, container, false);
        Fragment navbar = getParentFragmentManager().findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment();
            getParentFragmentManager().beginTransaction()
                    .add(R.id.clApprovedListManager, navbar)
                    .commit();
        }
        View view = binding.getRoot();
        ApiController.getInstance().getApprovedNames((Activity) getContext(), new VolleyCallBack<ArrayList<NameCardItem>>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(ArrayList<NameCardItem> list) {
                setAdapater();
                approvedList.addAll(list);
                approvedListAll.addAll(list);
                if(approvedList.size()==0){
                    Snackbar.make(binding.rvComboList, R.string.NoNameYet, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.SwipeMoreNames, view1 -> {
                                Intent i = new Intent(getActivity(), SwipeActivity.class);
                                getActivity().finish();
                                startActivity(i);
                            }).show();
                }
                else {
                    if(genderSwitchState == 0) filterByGender(approvedList,1);
                    else filterByGender(approvedList,0);
                    sortByName(approvedList);
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT)
                        .show();
            }
        });
        binding.btnViewLikedGoBack.setOnClickListener(this::removeListView);
        return view;
    }


    public void sortByName(ArrayList<NameCardItem> comboList){
        Collections.sort(comboList, (item1, item2) -> item1.getName().compareToIgnoreCase(item2.getName()));
        adapter.notifyDataSetChanged();
    }
    public void sortByRating(ArrayList<NameCardItem> comboList){
        Comparator<NameCardItem> comboListItemComparator = Comparator.comparingInt(NameCardItem::getRating);
        Collections.sort(comboList, comboListItemComparator.reversed());
        adapter.notifyDataSetChanged();
    }

    public void filterByGender(ArrayList<NameCardItem> comboList, int gender){
        ArrayList<NameCardItem> filteredList = new ArrayList<>();
        if(gender != 0 || gender != 1){
            comboList.clear();
            comboList.addAll(approvedListAll);
        }

        comboList.forEach((key)-> {
           if(key.getGender() == gender){
               filteredList.add(key);
           }
        });

        comboList.clear();
        comboList.addAll(filteredList);
        if(sortingSwitchState == 0) sortByName(comboList);
        else sortByRating(comboList);
        adapter.notifyDataSetChanged();

    }


    public void removeListView(View view){
        Fragment f = getParentFragmentManager().findFragmentByTag("ApprovedList");
        if (f != null) {
            getParentFragmentManager().beginTransaction()
                    .remove(f)
                    .commit();
        }
    }


    @Override
    public void onItemClick(int position) {
        if(sortingSwitchState == 0) sortByName(approvedList);
        else sortByRating(approvedList);
        removeFromApprovedList(approvedList.get(position).getId(),position);
        adapter.notifyDataSetChanged();
    }



    public void removeFromApprovedList(int namecardId, int position){
        ApiController.getInstance().removeFromApprovedList(namecardId,position, (Activity) getContext(), new VolleyCallBack<JSONObject>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                Toast.makeText(getContext(), getResources().getString(R.string.operationSuccess) ,Toast.LENGTH_SHORT)
                        .show();
                approvedList.remove(position);
                adapter.notifyDataSetChanged();
                return null;
            }
            @Override
            public void onResponse(JSONObject response) {
            }
            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error ,Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }
}