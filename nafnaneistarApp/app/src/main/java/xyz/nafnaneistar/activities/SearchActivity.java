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
import android.widget.CompoundButton;
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

public class SearchActivity extends AppCompatActivity implements SearchNameAdapter.OnItemListener {
    private ActivitySearchBinding binding;
    private Prefs prefs;
    private String tvNameResult;
    private ArrayList<NameCard> nameCardList = new ArrayList<>();
    private ArrayList<NameCard> approvedList = new ArrayList<NameCard>();
    private ArrayList<NameCard> nameCardListAll = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchNameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        binding.rvSearchResults.setVisibility(View.INVISIBLE);
        EditText etNameSearch = binding.etNameSearch;
        etNameSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(etNameSearch.getContext().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        binding.cbGenderMaleSearch.setOnCheckedChangeListener(this::OnCheckedChangeListener);
        binding.cbGenderFemaleSearch.setOnCheckedChangeListener(this::OnCheckedChangeListener);
        binding.btnSearchName.setOnClickListener(view -> {
            try {
                SearchName(view);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Listens to changes of the checkboxes. Makes sure that search results only include the gender
     * indicated by the ticked checkboxes.
     *
     * @param compoundButton Button
     * @param b Boolean
     */
    private void OnCheckedChangeListener(CompoundButton compoundButton, boolean b) {
        if (nameCardListAll != null) {
            filterListByGender(nameCardListAll);
        }
    }

    /**
     * Set the adapter for the RecyclerView
     */
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

    /**
     * Handles click event of the search button.
     *
     * @param view
     * @throws URISyntaxException
     */
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

    /**
     * Updates the list of names the current user has liked and will also search for names that are
     * similar to the user-provided string.
     *
     * @param query String to search by.
     */
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

    /**
     * Updates the search results by the string provided by the user.
     * @param query String used for the search of a name.
     */
    public void getNamesByName(String query) {
        ApiController.getInstance().getNameCardsByName((Activity) binding.btnSearchName.getContext(), query, new VolleyCallBack<ArrayList<NameCard>>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(ArrayList<NameCard> response) {
                setAdapter();
                binding.rvSearchResults.setVisibility(View.VISIBLE);
                nameCardListAll.clear();
                if (response.size() <= 0) {
                    Toast.makeText(binding.btnSearchName.getContext(), "Leit skilaði engum niðurstöðum", Toast.LENGTH_LONG).show();
                }
                nameCardListAll.addAll(response);
                filterListByGender(response);
                adapter.notifyDataSetChanged();
                try {
                    ApiController.getInstance().checkNotifications((Activity) binding.btnSearchName.getContext());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) { }
        });
    }

    /**
     * Updates the search result by removing names according to the gender indicated by the checkboxes.
     * @param list ArrayList of NameCards to be filtered.
     */
    private void filterListByGender(ArrayList<NameCard> list) {
        nameCardList.clear();
        Boolean male = binding.cbGenderMaleSearch.isChecked();
        Boolean female = binding.cbGenderFemaleSearch.isChecked();
        int gender;
        if ((male && female) || (!male && !female)) {
            nameCardList.clear();
            nameCardList.addAll(nameCardListAll);
            adapter.notifyDataSetChanged();
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
        adapter.notifyDataSetChanged();
    }
}