package xyz.nafnaneistar.activities.ViewLikedFragments;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import xyz.nafnaneistar.activities.items.ComboListItem;
import xyz.nafnaneistar.loginactivity.R;

import java.util.ArrayList;

public class ComboListNameCardRecyclerViewAdapter extends RecyclerView.Adapter<ComboListNameCardRecyclerViewAdapter.ViewHolder> {
    private ArrayList<ComboListItem> comboList;

    public ComboListNameCardRecyclerViewAdapter(ArrayList<ComboListItem> comboList){
        this.comboList = comboList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private  TextView rating;
        private Button delete;


        public ViewHolder(final View view){
            super(view);
            name = view.findViewById(R.id.linkComboListName);
            rating = view.findViewById(R.id.LinkComboListEmail);
            delete = view.findViewById(R.id.LinkComboListDel);
        }
    }
    @NonNull
    @Override
    public ComboListNameCardRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.combolist, parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ComboListNameCardRecyclerViewAdapter.ViewHolder holder, int position) {
        String name = comboList.get(position).getName();
        int rating = comboList.get(position).getRating();
        holder.name.setText(name);
        String ratingString = "";
        for(int i = 0; i < 5; i++){
            if(i < rating) ratingString += "❤";
            else ratingString +="🤍";
        }
        holder.rating.setText(ratingString);



    }

    @Override
    public int getItemCount() {
        return comboList.size();
    }
}