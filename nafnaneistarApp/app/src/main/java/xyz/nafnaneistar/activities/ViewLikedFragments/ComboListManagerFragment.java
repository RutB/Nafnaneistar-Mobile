package xyz.nafnaneistar.activities.ViewLikedFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
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
import xyz.nafnaneistar.activities.items.ComboListItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.helpers.Loaders;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.FragmentComboListManagerBinding;
import xyz.nafnaneistar.model.User;

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
    private ArrayList<ComboListItem> comboList;
    private ArrayList<ComboListItem> comboListAll = new ArrayList<>();
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
            genderSwitchState = 0;
        } else {
            filterByGender(comboList,1);
            genderSwitchState = 1;
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
                    .add(R.id.clComboListManager, navbar)
                    .commit();
        }
        View view = binding.getRoot();
        String[] user = prefs.getUser();
        String user_email = user[0];
        String pass = user[1];
 

        ApiController.getInstance().getNameCardsAndRating(partnerId, user_email, pass, new VolleyCallBack<ArrayList<ComboListItem>>() {

            @Override
            public ArrayList<ComboListItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(ArrayList<ComboListItem> response) {
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
                    sortByName(comboList);
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

    public void sortByName(ArrayList<ComboListItem> comboList){
        Collections.sort(comboList, (item1, item2) -> item1.getName().compareToIgnoreCase(item2.getName()));
        adapter.notifyDataSetChanged();
    }
    public void sortByRating(ArrayList<ComboListItem> comboList){
        Comparator<ComboListItem> comboListItemComparator = Comparator.comparingInt(ComboListItem::getRating);
        Collections.sort(comboList, comboListItemComparator.reversed());
        adapter.notifyDataSetChanged();
    }

    public void filterByGender(ArrayList<ComboListItem> comboList, int gender){
        ArrayList<ComboListItem> filteredList = new ArrayList<>();
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




    public void getNameCardsAndRating(Long partnerId, final VolleyCallBack cb) {
        String[] user = prefs.getUser();
        String user_email = user[0];
        String pass = user[1];
        String listeningPath = "viewliked/combolist";
        URIBuilder b = null;
        try {
            b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        b.addParameter("email", user_email);
        b.addParameter("pass", pass);
        b.addParameter("pid", String.valueOf(partnerId));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, b.toString(), null,
                response -> {
                    JSONArray namecards = null;
                    try {
                        namecards = response.getJSONArray("namecards");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < namecards.length(); i++) {
                        try {
                            JSONObject nc = (JSONObject) namecards.get(i);
                            comboList.add(new ComboListItem(
                                    nc.getInt("id"),
                                    nc.getString("name"),
                                    nc.getInt("rating"),
                                    nc.getInt("gender")
                            ));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d("list", "getNameCardsAndRating: JOB DONE");
                    cb.onSuccess();
                }, error -> {
                Toast.makeText(getContext(), R.string.errorGettingPartners, Toast.LENGTH_SHORT)
                    .show();
            Log.d("Test", "CheckLogin: " + error.toString());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onItemClick(int position) {
       removeFromApprovedList(comboList.get(position).getId(),position);
    }

    public void removeFromApprovedList(int namecardId, int position){
        String [] user = prefs.getUser();
        String email = user[0];
        String pass = user[1];
        String listeningPath = "viewliked/remove";
        URIBuilder b = null;
        String url = "";
        try {
            b = new URIBuilder(ApiController.getDomainURL()+listeningPath);
            b.addParameter("email",email);
            b.addParameter("password",pass);
            b.addParameter("id", String.valueOf(namecardId));

            url = b.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,url,null,
                response -> {
                    Toast.makeText(getContext(), getResources().getString(R.string.operationSuccess) ,Toast.LENGTH_SHORT)
                            .show();
                    comboList.remove(position);
                    adapter.notifyDataSetChanged();

                },error -> {
                Toast.makeText(getContext(), getResources().getString(R.string.errorRetrievingData) ,Toast.LENGTH_SHORT)
                        .show();
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);

    }
}