package xyz.nafnaneistar.activities.ViewLikedFragments;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import org.w3c.dom.Text;

import xyz.nafnaneistar.activities.items.ComboListItem;
import xyz.nafnaneistar.loginactivity.R;

import java.util.ArrayList;

public class ComboListNameCardRecyclerViewAdapter extends RecyclerView.Adapter<ComboListNameCardRecyclerViewAdapter.ViewHolder> {
    private ArrayList<ComboListItem> comboList;
    private OnItemListener onItemListener;

    public ComboListNameCardRecyclerViewAdapter(ArrayList<ComboListItem> comboList, OnItemListener onItemListener){
        this.comboList = comboList;
        this.onItemListener = onItemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private  TextView rating;
        private Button delete;
        OnItemListener onItemListener;

        public ViewHolder(final View view, OnItemListener onItemListener){
            super(view);
            name = view.findViewById(R.id.comboListName);
            rating = view.findViewById(R.id.comboListRating);
            delete = view.findViewById(R.id.comboListOperations);
            this.onItemListener = onItemListener;
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ComboListNameCardRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.combolist, parent,false);
        return new ViewHolder(itemView, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboListNameCardRecyclerViewAdapter.ViewHolder holder, int position) {
        String name = comboList.get(position).getName();
        int rating = comboList.get(position).getRating();
        holder.name.setText(getGenderSign(name,comboList.get(position).getGender(),holder.rating.getContext()),TextView.BufferType.SPANNABLE);
        String ratingString = "";
        for(int i = 0; i < 5; i++){
            if(i < rating) ratingString += "❤";
            else ratingString +="🤍";
        }
        holder.rating.setText(ratingString);



    }
    public SpannableStringBuilder getGenderSign(String name, int gender, Context context){
        SpannableStringBuilder stringbuild = new SpannableStringBuilder(name + "  ");
        if(gender == 0){
            stringbuild.setSpan(
                    new ImageSpan(context, R.drawable.ic_gender_male, DynamicDrawableSpan.ALIGN_BASELINE), name.length() + 1, name.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else {
            stringbuild.setSpan(
                    new ImageSpan(context, R.drawable.ic_gender_female, DynamicDrawableSpan.ALIGN_BASELINE), name.length() + 1, name.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


       return stringbuild;
    }

    public interface OnItemListener{
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return comboList.size();
    }
}