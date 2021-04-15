package xyz.nafnaneistar.activities.ViewLikedFragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.adapters.AdapterViewBindingAdapter;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;

import androidx.fragment.app.FragmentManager;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.FragmentComboListBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComboListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComboListFragment extends Fragment implements  AdapterView.OnItemSelectedListener {
    FragmentComboListBinding binding;
    FragmentManager fragmentManager;
    Activity context;
    Prefs mPrefs;
    ArrayList<Long> mPartnerIds = new ArrayList<>();
    Long mCurrentSelectedPartnerId = Long.valueOf(-1);
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
        mPrefs = new Prefs(context);
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



    public void getPartnerList(ArrayList<String> list){
        list.clear();
        list.add("Velja...");
        String[] user = mPrefs.getUser();
        String user_email = user[0];
        String pass = user[1];
        String listeningPath = "linkpartner";
        URIBuilder b = null;
        try {
            b = new URIBuilder(ApiController.getDomainURL() + listeningPath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        b.addParameter("email", user_email);
        b.addParameter("pass", pass);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, b.toString(), null,
        response -> {
            JSONArray resp = new JSONArray();
            try {
                resp = response.getJSONArray("partners");
                for(int i = 0; i < resp.length(); i++) {
                    JSONObject partner = (JSONObject) resp.get(i);
                    String name = partner.getString("name");
                    Long id = partner.getLong("id");
                    list.add(name);
                    mPartnerIds.add(id);
                }
                populateSpinner(list);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("partners", "CheckLink: " +resp );

        }, error -> {
            Toast.makeText(getContext(), R.string.errorGettingPartners, Toast.LENGTH_SHORT)
                    .show();
            Log.d("Test", "CheckLogin: " + error.toString());
        });
        ApiController.getInstance().addToRequestQueue(jsonObjReq);
    }

    //afhverju er fragment að kvarta?
    public void openListView(View view) {
        Fragment f = fragmentManager.findFragmentById(R.layout.fragment_combo_list_manager);
        if(f != null) return;;
        if(mCurrentSelectedPartnerId < 0){
            Toast.makeText(getContext(), R.string.please_choose_list, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (f == null) {
            f = new ComboListManagerFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("partnerId",mCurrentSelectedPartnerId);
            f.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(R.id.viewLikedContainer, f, "listViewCombo")
                    .commit();
        }
    }


    public void populateSpinner(ArrayList<String> list){
        ArrayAdapter adapter= new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, list){
            @Override
            public boolean isEnabled(int position){
                if (position == 0){
                    return false;
                }
                return true;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spPartners.setAdapter(adapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(mPartnerIds.size() > 0){
            try {
                mCurrentSelectedPartnerId = mPartnerIds.get(i-1);
            }
            catch (Exception e){
            }
        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}