package xyz.nafnaneistar.activities.ViewLikedFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
import xyz.nafnaneistar.loginactivity.databinding.FragmentComboListManagerBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComboListManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComboListManagerFragment extends Fragment implements  ComboListNameCardRecyclerViewAdapter.OnItemListener {
    private FragmentComboListManagerBinding binding;
    private Prefs prefs;
    ComboListNameCardRecyclerViewAdapter adapter;
    private Long partnerId;
    private ArrayList<NameCardItem> comboList;
    private ArrayList<NameCardItem> comboListAll = new ArrayList<>();
    private RecyclerView recyclerView;

    private int sortingSwitchState = 0;
    private int genderSwitchState = 0;
    

    public ComboListManagerFragment() {
        // Required empty public constructor
    }

    public static ComboListManagerFragment newInstance() {
        ComboListManagerFragment fragment = new ComboListManagerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Prefs(getActivity());
        partnerId = getArguments().getLong("partnerId");
        comboList = new ArrayList<>();
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("genderSwitchState",genderSwitchState);
        outState.putInt("sortingSwitchState", sortingSwitchState);
        super.onSaveInstanceState(outState);
    }


    private void setAdapater() {
        adapter = new ComboListNameCardRecyclerViewAdapter(comboList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        binding.rvComboList.setItemAnimator(new DefaultItemAnimator());
        binding.rvComboList.setLayoutManager(layoutManager);
        binding.rvComboList.setAdapter(adapter);
        binding.swOrderBy.setOnCheckedChangeListener(this::onCheckedChanged);
        binding.swToggleGender.setOnCheckedChangeListener(this::onGenderCheckedChange);
    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            sortByRating(comboList);
            sortingSwitchState = 1;
        } else {
            sortByName(comboList);
            sortingSwitchState = 0;
        }
    }

    public void onGenderCheckedChange(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            filterByGender(comboList,0);
            genderSwitchState = 1;
        } else {
            filterByGender(comboList,1);
            genderSwitchState = 0;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_combo_list_manager, container, false);
        Fragment navbar = getParentFragmentManager().findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment();
            getParentFragmentManager().beginTransaction()
                    .add(R.id.clApprovedListManager, navbar)
                    .commit();
        }
        View view = binding.getRoot();
        String[] user = prefs.getUser();
        String user_email = user[0];
        String pass = user[1];
        if(savedInstanceState != null){
            genderSwitchState = savedInstanceState.getInt("genderSwitchState");
            sortingSwitchState = savedInstanceState.getInt("sortingSwitchState");
        }
        ApiController.getInstance().getNameCardsAndRating(partnerId, user_email, pass, new VolleyCallBack<ArrayList<NameCardItem>>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(ArrayList<NameCardItem> response) {
                setAdapater();
                comboList.addAll(response);
                comboListAll.addAll(comboList);
                if(comboList.size()==0){
                    Snackbar.make(binding.rvComboList, R.string.NoComboNames, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.SwipeMoreNames, view1 -> {
                                Intent i = new Intent(getActivity(), SwipeActivity.class);
                                getActivity().finish();
                                startActivity(i);
                            }).show();
                }
                else {
                    if(genderSwitchState == 0) filterByGender(comboList,1);
                    else filterByGender(comboList,0);
                    if(sortingSwitchState == 0)sortByName(comboList);
                    else sortByRating(comboList);

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
            comboList.addAll(comboListAll);
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
        Fragment f = getParentFragmentManager().findFragmentByTag("listViewCombo");
        if (f != null) {
            getParentFragmentManager().beginTransaction()
                    .remove(f)
                    .commit();
        }
    }
    @Override
    public void onItemClick(int position) {
       removeFromApprovedList(comboList.get(position).getId(),position);
        adapter.notifyDataSetChanged();
    }

    public void removeFromApprovedList(int namecardId, int position){
        ApiController.getInstance().removeFromApprovedList(namecardId,position, (Activity) getContext(), new VolleyCallBack<JSONObject>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                Toast.makeText(getContext(), getResources().getString(R.string.operationSuccess) ,Toast.LENGTH_SHORT)
                        .show();
                NameCardItem nc = comboList.get(position);
                comboListAll.remove(nc);
                comboList.remove(position);
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