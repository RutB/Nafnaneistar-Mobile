package xyz.nafnaneistar.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.model.NameCard;

public class SearchNameAdapter extends RecyclerView.Adapter<SearchNameAdapter.ViewHolder> {

    private ArrayList<NameCard> nameCardList;

    public SearchNameAdapter(ArrayList<NameCard> nameCardList) {
        this.nameCardList = nameCardList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNameResult;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameResult = itemView.findViewById(R.id.tvNameResult);
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
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // "Here we can change the text of our textview"
        // Checka hér hvort nafn sé á liked lista logged in users og birta réttan takka?
        String name = nameCardList.get(position).getName();
        holder.tvNameResult.setText(name);
    }

    @Override
    public int getItemCount() {
        if (nameCardList == null) {
            return 0;
        } else
        return nameCardList.size();
    }
}
