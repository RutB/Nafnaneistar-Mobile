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

import androidx.annotation.NonNull;

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
    private Prefs mPrefs;
    static ApprovedListCardRecyclerViewAdapter adapter;
    private ArrayList<NameCardItem> mApprovedList;
    private ArrayList<NameCardItem> mApprovedListAll = new ArrayList<>();
    private RecyclerView recyclerView;
    
    private int mSortingSwitchState = 0;
    private int mGenderSwitchState = 0;
    

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
        mPrefs = new Prefs(getActivity());
        mApprovedList = new ArrayList<>();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("genderSwitchState", mGenderSwitchState);
        outState.putInt("sortingSwitchState", mSortingSwitchState);
        super.onSaveInstanceState(outState);
    }



    private void setAdapater() {
        adapter = new ApprovedListCardRecyclerViewAdapter(mApprovedList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        binding.rvComboList.setItemAnimator(new DefaultItemAnimator());
        binding.rvComboList.setLayoutManager(layoutManager);
        binding.rvComboList.setAdapter(adapter);
        binding.swOrderBy.setOnCheckedChangeListener(this::onCheckedChanged);
        binding.swToggleGender.setOnCheckedChangeListener(this::onGenderCheckedChange);
    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            sortByRating(mApprovedList);
            mSortingSwitchState = 1;
        } else {
            sortByName(mApprovedList);
            mSortingSwitchState = 0;
        }
    }

    public void onGenderCheckedChange(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            filterByGender(mApprovedList,0);
            mGenderSwitchState = 1;
        } else {
            filterByGender(mApprovedList,1);
            mGenderSwitchState = 0;
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
        if(savedInstanceState != null){
            mGenderSwitchState = savedInstanceState.getInt("genderSwitchState");
            mSortingSwitchState = savedInstanceState.getInt("sortingSwitchState");
        }
        ApiController.getInstance().getApprovedNames((Activity) getContext(), new VolleyCallBack<ArrayList<NameCardItem>>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(ArrayList<NameCardItem> list) {
                setAdapater();
                mApprovedList.addAll(list);
                mApprovedListAll.addAll(list);
                if(mApprovedList.size()==0){
                    Snackbar.make(binding.rvComboList, R.string.NoNameYet, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.SwipeMoreNames, view1 -> {
                                Intent i = new Intent(getActivity(), SwipeActivity.class);
                                getActivity().finish();
                                startActivity(i);
                            }).show();
                }
                else {
                    if(mGenderSwitchState == 0) filterByGender(mApprovedList,1);
                    else filterByGender(mApprovedList,0);
                    if(mSortingSwitchState == 0)sortByName(mApprovedList);
                    else sortByRating(mApprovedList);


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
            comboList.addAll(mApprovedListAll);
        }

        comboList.forEach((key)-> {
           if(key.getGender() == gender){
               filteredList.add(key);
           }
        });

        comboList.clear();
        comboList.addAll(filteredList);
        if(mSortingSwitchState == 0) sortByName(comboList);
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
        removeFromApprovedList(mApprovedList.get(position).getId(),position);
        adapter.notifyDataSetChanged();

        if(mSortingSwitchState == 0) sortByName(mApprovedList);
        else sortByRating(mApprovedList);

    }



    public void removeFromApprovedList(int namecardId, int position){
        ApiController.getInstance().removeFromApprovedList(namecardId,position, (Activity) getContext(), new VolleyCallBack<JSONObject>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                Toast.makeText(getContext(), getResources().getString(R.string.operationSuccess) ,Toast.LENGTH_SHORT)
                        .show();
                adapter.notifyDataSetChanged();
                return null;
            }
            @Override
            public void onResponse(JSONObject response) {
                NameCardItem nc = mApprovedList.get(position);
                mApprovedListAll.remove(nc);
                mApprovedList.remove(position);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error ,Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }
}