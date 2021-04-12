package xyz.nafnaneistar.activities;


import androidx.appcompat.app.AppCompatActivity;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import xyz.nafnaneistar.activities.LinkedPartnerFragments.LinkRecyclerViewAdapter;
import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.activities.items.UserItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivityLinkPartnerBinding;
import xyz.nafnaneistar.model.User;

public class LinkPartnerActivity extends AppCompatActivity implements LinkRecyclerViewAdapter.OnItemListener {
    private ActivityLinkPartnerBinding binding;
    private User currentUser = new User();
    private Prefs prefs;
    static LinkRecyclerViewAdapter adapter;
    private ArrayList<UserItem> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_link_partner);
        binding.btnLink.setOnClickListener(view -> {
                    populateNewTable(view);
        });
        prefs = new Prefs(LinkPartnerActivity.this);

        //Initialize the navbar fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment navbar = fragmentManager.findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.LinkContainer, navbar)
                    .commit();
        }
        populateTable();
    }

    private void setAdapater() {
        adapter = new LinkRecyclerViewAdapter(userList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        binding.rvComboList.setItemAnimator(new DefaultItemAnimator());
        binding.rvComboList.setLayoutManager(layoutManager);
        binding.rvComboList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        userList.get(position);
        removeFromUserList(userList.get(position).getEmail(), position);
        adapter.notifyDataSetChanged();
    }

    public void populateTable() {
        ApiController.getInstance().getLinkedPartners((Activity) binding.btnLink.getContext(), new VolleyCallBack<ArrayList<UserItem>>() {

            @Override
            public void onError(String error) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onResponse(ArrayList<UserItem> list) {
                setAdapater();
                if (!list.isEmpty())
                    userList.addAll(list);
            }

            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

        });
    }

    public void removeFromUserList(String email, int position) {
        ApiController.getInstance().removeFromLinkPartners(email, (Activity) binding.btnLink.getContext(), new VolleyCallBack<JSONObject>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(JSONObject response) {
                UserItem user = userList.get(position);
                userList.remove(user);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText((Activity) binding.btnLink.getContext(), error, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void populateNewTable(View view) {
        String email = binding.etEmail2.getText().toString().trim();
        Pattern pattern = Pattern.compile("^.+@.+\\..+$");
        Matcher matcher = pattern.matcher(email);
        if (email.length() == 0) {
            Toast.makeText(LinkPartnerActivity.this, R.string.errorEmptyStrings, Toast.LENGTH_SHORT)
                    .show();
            return;
        } else if (!matcher.matches()) {
            Toast.makeText(LinkPartnerActivity.this, R.string.errorInvalidEmail, Toast.LENGTH_SHORT)
                    .show();
            return;
        }


        ApiController.getInstance().putLinkedPartners(email, (Activity) binding.btnLink.getContext(), new VolleyCallBack<ArrayList<UserItem>>() {

            @Override
            public void onError(String error) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onResponse(ArrayList<UserItem> list) {
                setAdapater();
                // Log.d("test", "onResponse: "+ list.get(0));
                userList.clear();
                userList.addAll(list);
                // binding.etEmail2.setText(" ");
                //binding.etEmail2.clearFocus();
                // binding.etEmail2.setHint(R.string.hint_email);
            }

            @Override
            public ArrayList<NameCardItem> onSuccess() {
                binding.etEmail2.setText(" ");
                binding.etEmail2.clearFocus();
                binding.etEmail2.setHint(R.string.hint_email);
                return null;
            }

        });
    }



}