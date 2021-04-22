package xyz.nafnaneistar.activities.SettingsFragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.FragmentChangeDataBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangeDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeDataFragment extends Fragment {
    FragmentChangeDataBinding binding;
    Activity context;
    Prefs prefs;
    int type = 0;


    public ChangeDataFragment() {
        // Required empty public constructor
    }

    public static ChangeDataFragment newInstance() {
        ChangeDataFragment fragment = new ChangeDataFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        prefs = new Prefs(context);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_data,container,false);
        View view = getView() != null ? getView() :
                binding.getRoot();
        Bundle bundle = this.getArguments();
        this.type = bundle.getInt("type");
        if(type == 1){
            binding.tvChangeDataTitle.setText("Breyta um Nafn");
            binding.etOldData.setVisibility(View.GONE);
            binding.etNewData.setHint("Nýtt Nafn");
        }
        if(type == 2){
            binding.tvChangeDataTitle.setText("Breyta um lykilorð");
            binding.etOldData.setVisibility(View.VISIBLE);
            binding.etOldData.setHint("Gamla Lykilorð");
            binding.etNewData.setHint("Nýja Lykilorð");
        }
        binding.btnCancel.setOnClickListener(this::closeFragment);
        return view;
    }
    private  void closeFragment(View view) {
        Fragment f = getParentFragmentManager().findFragmentByTag("changedata");
        if(f != null)
            getParentFragmentManager().beginTransaction().remove(f).commit();
    }
}