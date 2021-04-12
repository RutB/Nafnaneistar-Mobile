package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.jar.Attributes;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;


import xyz.nafnaneistar.loginactivity.databinding.ActivitySearchBinding;
import xyz.nafnaneistar.model.NameCard;
import xyz.nafnaneistar.model.User;

import static xyz.nafnaneistar.loginactivity.R.*;


public class SearchActivity extends AppCompatActivity implements SearchNameAdapter.OnItemListener {
    private ActivitySearchBinding binding;
    // private user currentUser = new User();
    private Prefs prefs;
    private String tvNameResult;
    // private ArrayList<NameCard> nameCardList;
    private ArrayList<NameCard> nameCardList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchNameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_search);
        prefs = new Prefs(SearchActivity.this);
        binding = DataBindingUtil.setContentView(this, layout.activity_search);
        recyclerView = (RecyclerView) binding.rvSearchResults;
        binding.btnSearchName.setOnClickListener(view -> {
            try {
                SearchName(view);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });

    }

    private void setAdapter() {
        // SearchNameAdapter adapter = new SearchNameAdapter(nameCardList);
        adapter = new SearchNameAdapter(nameCardList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        // TODO: Custom animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //Initialize the navbar fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment();
            fragmentManager.beginTransaction()
                    .add(id.SearchNameContainer, navbar)
                    .commit();
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.d("onItemClick","Keyrir");
    }

    /**
     * TODO:
     *  User authentication.
     *  Mata gögn inn viðmótið í lista.
     *  Add og Remove hnappar á lista við hvert nafn.
     *  Deala við þegar leitarreitur er tómur og ýtt er á "leita"
     *
     * WIP: Eins og er þá skilar þetta fall leitarniðustöðu í Logcat.
     * @param view
     * @return null
     */
    public void SearchName(View view) throws URISyntaxException {
        String nameQuery = binding.etNameSearch.getText().toString().trim();
        if (nameQuery.length() <= 1){
            Toast.makeText(getApplicationContext(), "Sláðu inn meira en einn staf" ,Toast.LENGTH_SHORT).show();
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);

        ApiController.getInstance().getNamesByName((Activity) binding.btnSearchName.getContext(), nameQuery, new VolleyCallBack<ArrayList<NameCard>>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(ArrayList<NameCard> response) {
                nameCardList.clear();
                setAdapter();
                nameCardList.addAll(response);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onError(String error) {

            }
        });




    }


}