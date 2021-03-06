package xyz.nafnaneistar.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import androidx.annotation.NonNull;


import xyz.nafnaneistar.activities.items.UserItem;
import xyz.nafnaneistar.loginactivity.R;

public class LinkRecyclerViewAdapter extends RecyclerView.Adapter<LinkRecyclerViewAdapter.ViewHolder> {
    private ArrayList<UserItem> userList;
    private OnItemListener onItemListener;

    public LinkRecyclerViewAdapter(ArrayList<UserItem> userList, LinkRecyclerViewAdapter.OnItemListener onItemListener) {
        this.userList = userList;
        this.onItemListener = onItemListener;
    }

    /**
     * Inflates the table for linked partners
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linkcombolist, parent,false);
        return new ViewHolder(itemView, onItemListener);
    }

    /**
     * Updates the contents of the view holder in correlation with position
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull LinkRecyclerViewAdapter.ViewHolder holder, int position) {
        String name = userList.get(position).getName();
        String email = userList.get(position).getEmail();
        holder.name.setText(name);
        holder.email.setText(email);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private TextView email;
        private Button delete;
        OnItemListener onItemListener;

        public ViewHolder(final View view, OnItemListener onItemListener) {
            super(view);
            name = view.findViewById(R.id.linkComboListName);
            email = view.findViewById(R.id.linkComboListEmail);
            delete = view.findViewById(R.id.linkComboListDel);
            this.onItemListener = onItemListener;
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.linkComboListDel){
                    onItemListener.onItemClick(getAdapterPosition());
            }
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }

}
