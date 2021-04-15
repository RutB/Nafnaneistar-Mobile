package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;


import xyz.nafnaneistar.loginactivity.databinding.ActivitySearchBinding;
import xyz.nafnaneistar.model.NameCard;

import static xyz.nafnaneistar.loginactivity.R.*;

/**
 * TODO:
 * Útlit
 */


public class SearchActivity extends AppCompatActivity implements SearchNameAdapter.OnItemListener {
    private ActivitySearchBinding binding;
    // private user currentUser = new User();
    private Prefs prefs;
    private String tvNameResult;
    private ArrayList<NameCard> nameCardList = new ArrayList<>();
    private ArrayList<NameCard> approvedList = new ArrayList<NameCard>();
    private ArrayList<NameCard> nameCardListAll = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchNameAdapter adapter;
    private CheckBox isMale;
    private CheckBox isFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_search);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment();
            fragmentManager.beginTransaction()
                    .add(id.SearchNameContainer, navbar)
                    .commit();
        }
        prefs = new Prefs(SearchActivity.this);
        binding = DataBindingUtil.setContentView(this, layout.activity_search);
        recyclerView = (RecyclerView) binding.rvSearchResults;
        EditText etNameSearch = binding.etNameSearch;
        etNameSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(etNameSearch.getContext().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        isMale = binding.cbGenderMaleSearch;
        isFemale = binding.cbGenderFemaleSearch;
        binding.btnSearchName.setOnClickListener(view -> {
            try {
                SearchName(view);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });

    }

    private void setAdapter() {
        adapter = new SearchNameAdapter(nameCardList, approvedList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        Log.d("onItemClick","Keyrir");
    }
    
    public void SearchName(View view) throws URISyntaxException {
        String nameQuery = binding.etNameSearch.getText().toString().trim();
        if (nameQuery.length() <= 1){
            Toast.makeText(getApplicationContext(), "Sláðu inn meira en einn staf" ,Toast.LENGTH_SHORT).show();
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
        getIdsOfApproved(nameQuery);
    }

    private void getIdsOfApproved(String query) {
        ApiController.getInstance().getApprovedNameCards((Activity) binding.btnSearchName.getContext(), new VolleyCallBack<ArrayList<NameCard>>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(ArrayList<NameCard> response) {
                approvedList.clear();
                nameCardList.clear();
                approvedList.addAll(response);
                getNamesByName(query);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void getNamesByName(String query) {
        ApiController.getInstance().getNameCardsByName((Activity) binding.btnSearchName.getContext(), query, new VolleyCallBack<ArrayList<NameCard>>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(ArrayList<NameCard> response) {
                setAdapter();
                nameCardListAll.clear();
                nameCardListAll.addAll(response);
                filterListByGender(response);

                if ((isMale.isChecked() && isFemale.isChecked()) || (!isMale.isChecked() && !isFemale.isChecked())) {
                    nameCardList.addAll(response);
                } else {
                    filterListByGender(response);

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) { }
        });
    }

    private void filterListByGender(ArrayList<NameCard> list) {
        Boolean male = isMale.isChecked();
        Boolean female = isFemale.isChecked();
        int gender;
        if ((male && female) || (!male && !female)) {
            nameCardList.addAll(nameCardListAll);
            return;
        } else if (!male && female) {
            gender = 1;
        } else {
            gender = 0;
        }
        list.forEach((item) -> {
            if(item.getGender() == gender) {
                nameCardList.add(item);
            }
        });


    }
}