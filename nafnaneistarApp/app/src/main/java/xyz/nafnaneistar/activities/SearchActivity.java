package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import org.apache.http.client.utils.URIBuilder;

import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;

// import xyz.nafnaneistar.loginactivity.databinding.ActivitySignupBinding;
import xyz.nafnaneistar.loginactivity.databinding.ActivitySearchBinding;
import xyz.nafnaneistar.model.NameCard;

import static xyz.nafnaneistar.loginactivity.R.*;


public class SearchActivity extends AppCompatActivity {
    private ActivitySearchBinding binding;
    // private user currentUser = new User();
    private Prefs prefs;
    private String tvNameResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_search);
        prefs = new Prefs(SearchActivity.this);

        binding = DataBindingUtil.setContentView(this, layout.activity_search);
        binding.btnSearchName.setOnClickListener(this::SearchName);

        // binding.textView13.setText("LOL");
        binding.textView13.getText();

    }

    /**
     * TODO:
     *  Taka inn leitarstreng
     *  Nota leitarstreng til að fá results
     *  Mata results inn í töflu
     * @param view
     * @return
     */
    public NameCard[] SearchName(View view){
        String nameQuery = binding.etNameSearch.getText().toString().trim();

        // URIBuilder b = new URIBuilder(ApiController.getDomainURL()+listeningPath)

        // binding.textView13.setText("LOL");

        return null;
    }


}