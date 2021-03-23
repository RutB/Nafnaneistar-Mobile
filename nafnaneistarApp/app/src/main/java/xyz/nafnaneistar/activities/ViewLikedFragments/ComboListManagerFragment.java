package xyz.nafnaneistar.activities.ViewLikedFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import xyz.nafnaneistar.activities.items.ComboListItem;
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
public class ComboListManagerFragment extends Fragment {
    private FragmentComboListManagerBinding binding;
    private Prefs prefs;
    private Long partnerId;
    private ArrayList<ComboListItem> comboList;
    private RecyclerView recyclerView;

    public ComboListManagerFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
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
        ComboListNameCardRecyclerViewAdapter adapter = new ComboListNameCardRecyclerViewAdapter(comboList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        binding.rvComboList.setItemAnimator(new DefaultItemAnimator());
        binding.rvComboList.setLayoutManager(layoutManager);
        binding.rvComboList.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_combo_list_manager, container, false);
        View view = binding.getRoot();
        getNameCardsAndRating(partnerId, (VolleyCallBack) () -> {
            setAdapater();
        });

        return view;
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
                                    nc.getString("name"),
                                    nc.getInt("rating"),
                                    "Operations",
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
}