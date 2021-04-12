package xyz.nafnaneistar.activities;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.ArrayList;

import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.model.NameCard;


public class SearchNameAdapter extends RecyclerView.Adapter<SearchNameAdapter.ViewHolder> {
    private OnItemListener onItemListener;
    private ArrayList<NameCard> nameCardList;

    public SearchNameAdapter(ArrayList<NameCard> nameCardList, SearchNameAdapter.OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
        this.nameCardList = nameCardList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvNameResult;
        OnItemListener onItemListener;

        public ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            this.onItemListener = onItemListener;

            tvNameResult = itemView.findViewById(R.id.tvNameResult);
        }

        @Override
        public void onClick(View view) {

        }
        // TODO:
        // Hnappur fyrir add to liked
        // Hnappur fyrir info um nafn

/*        public ViewHolder ViewHolder(final View view){
            super(view);
            tvNameResult = view.findViewById(R.id.tvNameResult);
        }*/
    }


    @NonNull
    @Override
    public SearchNameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_list_items, parent, false);
        return new ViewHolder(itemView, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchNameAdapter.ViewHolder holder, int position) {
        // "Here we can change the text of our textview"
        // Checka hér hvort nafn sé á liked lista logged in users og birta réttan takka?
        String name = nameCardList.get(position).getName();
        // holder.tvNameResult.setText(name);
        holder.tvNameResult.setText(getGenderSign(name, nameCardList.get(position).getGender(),holder.tvNameResult.getContext()),TextView.BufferType.SPANNABLE);
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
