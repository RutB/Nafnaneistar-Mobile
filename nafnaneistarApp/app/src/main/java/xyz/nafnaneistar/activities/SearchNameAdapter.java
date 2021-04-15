package xyz.nafnaneistar.activities;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.model.NameCard;
import xyz.nafnaneistar.helpers.Utils;


public class SearchNameAdapter extends RecyclerView.Adapter<SearchNameAdapter.ViewHolder> {
    private OnItemListener onItemListener;
    private ArrayList<NameCard> nameCardList;
    private ArrayList<NameCard> approvedList;

    public SearchNameAdapter(ArrayList<NameCard> nameCardList, ArrayList<NameCard> approvedList, SearchNameAdapter.OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
        this.nameCardList = nameCardList;
        this.approvedList = approvedList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvNameResult;
        private Button btnAddToLiked;
        private Button btnRemoveFromLiked;
        OnItemListener onItemListener;

        public ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            this.onItemListener = onItemListener;

            tvNameResult = itemView.findViewById(R.id.tvNameResult);
            btnAddToLiked = itemView.findViewById(R.id.btAddToLiked);
            btnRemoveFromLiked = itemView.findViewById(R.id.btRemoveFromLiked);
        }

        @Override
        public void onClick(View view) {

        }
    }


    @NonNull
    @Override
    public SearchNameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_list_items, parent, false);
        return new ViewHolder(itemView, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchNameAdapter.ViewHolder holder, int position) {
        String name = nameCardList.get(position).getName();
        int id = nameCardList.get(position).getId();
        int gender = nameCardList.get(position).getGender();
        AtomicBoolean male = new AtomicBoolean(false);
        AtomicBoolean female = new AtomicBoolean(false);

        holder.tvNameResult.setText(getGenderSign(name, nameCardList.get(position).getGender(),holder.tvNameResult.getContext()),TextView.BufferType.SPANNABLE);
        if (Utils.nameCardListContains(approvedList, nameCardList.get(position))) {
            holder.btnAddToLiked.setVisibility(View.GONE);
            holder.btnRemoveFromLiked.setVisibility(View.VISIBLE);
        } else {
            holder.btnAddToLiked.setVisibility(View.VISIBLE);
            holder.btnRemoveFromLiked.setVisibility(View.GONE);
        };
        holder.btnRemoveFromLiked.setOnClickListener( view -> {
            toggleView(view);
            toggleView(holder.btnAddToLiked);
            removeFromApprovedList(id, holder.btnRemoveFromLiked.getContext());
            approvedList.remove(nameCardList.get(position));
        });
        holder.btnAddToLiked.setOnClickListener( view -> {
            toggleView(view);
            approvedList.add(nameCardList.get(position));
            toggleView(holder.btnRemoveFromLiked);
            if (gender == 0) {
                 male.set(true);
                 female.set(false);
            } else {
                 male.set(false);
                 female.set(true);
            }
            try {
                addOrRemoveFromApprovedList("approve", id, male.get(), female.get(), (Activity) holder.btnRemoveFromLiked.getContext());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    public void toggleView(View view){
        if(view.getVisibility()==View.GONE)
            view.setVisibility(View.VISIBLE);
        else if(view.getVisibility()==View.VISIBLE)
            view.setVisibility(View.GONE);
    }

    public void removeFromApprovedList(int nameCardId, Context context) {
        ApiController.getInstance().removeFromApprovedList(nameCardId, 0, (Activity) context,  new VolleyCallBack<JSONObject>() {

            @Override
            public ArrayList<NameCardItem> onSuccess() {
                Toast.makeText(context, "Nafn fjarlægt af lista", Toast.LENGTH_SHORT).show();
                return null;
            }

            @Override
            public void onResponse(JSONObject response) {

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void addOrRemoveFromApprovedList(String action, int nameCardId, boolean male, boolean female, Activity context) throws URISyntaxException {

        ApiController.getInstance().chooseName(action, nameCardId, false, false, context, new VolleyCallBack<NameCard>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                if (action.equals("approve")) {
                    Toast.makeText(context, "Nafni bætt við á lista", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Nafn fjarlægt af lista", Toast.LENGTH_SHORT).show();
                }
                return null;
            }

            @Override
            public void onResponse(NameCard response) {

            }

            @Override
            public void onError(String error) {

            }
        });
    }


    public SpannableStringBuilder getGenderSign(String name, int gender, Context context){
        SpannableStringBuilder stringBuild = new SpannableStringBuilder(name + "  ");
        if(gender == 0){
            stringBuild.setSpan(
                    new ImageSpan(context, R.drawable.ic_gender_male, DynamicDrawableSpan.ALIGN_BASELINE), name.length() + 1, name.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else {
            stringBuild.setSpan(
                    new ImageSpan(context, R.drawable.ic_gender_female, DynamicDrawableSpan.ALIGN_BASELINE), name.length() + 1, name.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return stringBuild;
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        if (nameCardList == null) {
            return 0;
        } else
        return nameCardList.size();
    }
}
