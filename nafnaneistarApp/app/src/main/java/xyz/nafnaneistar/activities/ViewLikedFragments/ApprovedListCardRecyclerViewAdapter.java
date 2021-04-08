package xyz.nafnaneistar.activities.ViewLikedFragments;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import xyz.nafnaneistar.activities.items.NameCardItem;
import xyz.nafnaneistar.controller.ApiController;
import xyz.nafnaneistar.controller.VolleyCallBack;
import xyz.nafnaneistar.loginactivity.R;

public class ApprovedListCardRecyclerViewAdapter extends RecyclerView.Adapter<ApprovedListCardRecyclerViewAdapter.ViewHolder> {
    private ArrayList<NameCardItem> approvedList;
    private OnItemListener onItemListener;

    public ApprovedListCardRecyclerViewAdapter(ArrayList<NameCardItem> approvedList, OnItemListener onItemListener){
        this.approvedList = approvedList;
        this.onItemListener = onItemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private  LinearLayout rating;
        private Button delete;
        OnItemListener onItemListener;

        public ViewHolder(final View view, OnItemListener onItemListener){
            super(view);
            name = view.findViewById(R.id.comboListName);
            rating = view.findViewById(R.id.llRatingContainer);
            delete = view.findViewById(R.id.comboListOperations);
            this.onItemListener = onItemListener;
            delete.setOnClickListener(this);
            for( int i = 0; i < 5   ; i++){
                View v = rating.getChildAt(i);
                v.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View view) {

             switch (view.getId()){
                case R.id.comboListOperations:
                    int pos = this.getAdapterPosition();
                    onItemListener.onItemClick(getAdapterPosition());
                break;
                 default:
                     ratingClick(view,this.getAdapterPosition());
                 break;
            }
        }

    }
    public void ratingClick(View view, int position){
        LinearLayout ratingContainer = (LinearLayout) view.getParent();
        TextView tv = (TextView) ratingContainer.getChildAt(5);
        int nameCardId = Integer.parseInt(String.valueOf(tv.getText()));
        int nameCardRating = 0;
        switch (view.getId()){
            case R.id.r1:
                nameCardRating = 1;
                break;
        case R.id.r2:
            nameCardRating = 2;
                break;
        case R.id.r3:
            nameCardRating = 3;
                break;
        case R.id.r4:
            nameCardRating = 4;
            break;
        case R.id.r5:
            nameCardRating = 5;
            break;
        default:
            break;
        }
        approvedList.get(position).setRating(nameCardRating);
        updateNameCardRating(nameCardId,nameCardRating, (Activity) view.getContext(),ratingContainer);
    }
    @NonNull
    @Override
    public ApprovedListCardRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.combolist, parent,false);
        return new ViewHolder(itemView, onItemListener);
    }
    @Override
    public void onBindViewHolder(@NonNull ApprovedListCardRecyclerViewAdapter.ViewHolder holder, int position) {
        String name = approvedList.get(position).getName();
        int rating = approvedList.get(position).getRating();
        holder.name.setText(getGenderSign(name, approvedList.get(position).getGender(),holder.rating.getContext()),TextView.BufferType.SPANNABLE);
        for(int i = 0; i < 5; i++){
            String ratingString = "";
            if(i < rating) ratingString += "❤";
            else ratingString +="🤍";
            TextView tv = (TextView) holder.rating.getChildAt(i);
            tv.setText(ratingString);
        }
        TextView tv = (TextView) holder.rating.getChildAt(5);
        tv.setText(String.valueOf(approvedList.get(position).getId()));
    }
    public void updateNameCardRating(int id, int rating, Activity context, LinearLayout ratingContainer){

        ApiController.getInstance().updateRating(id, rating, context, new VolleyCallBack<JSONObject>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() { return null;
            }
            @Override
            public void onResponse(JSONObject response) {
                TextView tv1;
                for (int i = 0; i < 5; i++){
                    tv1 = (TextView) ratingContainer.getChildAt(i);
                    tv1.setText("🤍");
                }
                for (int i = 0; i < rating; i++){
                    tv1 = (TextView) ratingContainer.getChildAt(i);
                    tv1.setText("❤");
                }
                Toast.makeText(context, context.getResources().getString(R.string.operationSuccess) ,Toast.LENGTH_SHORT)
                        .show();
            }
            @Override
            public void onError(String error) {
                Toast.makeText(context, error ,Toast.LENGTH_SHORT)
                        .show();
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

    public interface OnItemListener{
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return approvedList.size();
    }
}