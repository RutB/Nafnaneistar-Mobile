package xyz.nafnaneistar.activities.LinkedPartnerFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import xyz.nafnaneistar.activities.NavbarFragment;
import xyz.nafnaneistar.activities.SwipeActivity;
import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.activities.items.UserItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.helpers.Prefs;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.FragmentApprovedListManagerBinding;
import xyz.nafnaneistar.model.User;

public class LinkListManagerFragment extends Fragment implements LinkRecyclerViewAdapter.OnItemListener {
    private FragmentApprovedListManagerBinding binding;
    private Prefs prefs;
    static LinkRecyclerViewAdapter adapter;
    private Long partnerId;
    private ArrayList<UserItem> userList;
    private RecyclerView recyclerView;


    public LinkListManagerFragment() {
        // Required empty public constructor
    }

    public static LinkListManagerFragment newInstance() {
        LinkListManagerFragment fragment = new LinkListManagerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Prefs(getActivity());
        userList = new ArrayList<>();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //    outState.putInt("genderSwitchState",genderSwitchState);
        //   outState.putInt("sortingSwitchState", sortingSwitchState);
        super.onSaveInstanceState(outState);
    }

    private void setAdapater() {
        adapter = new LinkRecyclerViewAdapter(userList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        binding.rvComboList.setItemAnimator(new DefaultItemAnimator());
        binding.rvComboList.setLayoutManager(layoutManager);
        binding.rvComboList.setAdapter(adapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.link_fragment_combo_list_manager, container, false);
        Fragment navbar = getParentFragmentManager().findFragmentById(R.id.navbar);

        if (navbar == null) {
            navbar = new NavbarFragment();
            getParentFragmentManager().beginTransaction()
                    .add(R.id.LinkContainer, navbar)
                    .commit();
        }
        View view = binding.getRoot();
        ApiController.getInstance().getLinkedPartners((Activity) getContext(), new VolleyCallBack<ArrayList<UserItem>>() {



            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onResponse(ArrayList<NameCardItem> list) {

            }
            @Override
            public void onUserResponse(ArrayList<UserItem> list) {
                setAdapater();
                userList.addAll(list);

            }


            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }
            @Override
            public ArrayList<UserItem> onUserSuccess() {
                return null;
            }


        });
        return view;
    }

    @Override
    public void onItemClick(int position) {
        //removeFromUserList(userList.get(position).getId(),position);
        //adapter.notifyDataSetChanged();
    }

  /*  public void removeFromUserList(int userId, int position){
        ApiController.getInstance().removeFromUserList(userId, position, (Activity) getContext(), new VolleyCallBack<JSONObject>() {
            @Override
            public ArrayList<UserItem> onUserSuccess() {
                Toast.makeText(getContext(), getResources().getString(R.string.operationSuccess) ,Toast.LENGTH_SHORT)
                        .show();
                adapter.notifyDataSetChanged();
                return null;
            }
            @Override
            public void onResponse(JSONObject response) {
                userList.remove(position);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error ,Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }*/
}

