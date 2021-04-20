package xyz.nafnaneistar.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

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
        private Button btnDescription;
        OnItemListener onItemListener;

        public ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            this.onItemListener = onItemListener;

            tvNameResult = itemView.findViewById(R.id.tvNameResult);
            btnAddToLiked = itemView.findViewById(R.id.btAddToLiked);
            btnRemoveFromLiked = itemView.findViewById(R.id.btRemoveFromLiked);
            btnDescription = itemView.findViewById(R.id.btDescription);
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
        String desc = nameCardList.get(position).getDescription();

        int id = nameCardList.get(position).getId();
        int gender = nameCardList.get(position).getGender();

        AtomicBoolean male = new AtomicBoolean(false);
        AtomicBoolean female = new AtomicBoolean(false);

        holder.tvNameResult.setText(
                getGenderSign(name,
                        nameCardList.get(position).getGender(),
                        holder.tvNameResult.getContext()
                ),
                TextView.BufferType.SPANNABLE
        );

        if (Utils.nameCardListContains(approvedList, nameCardList.get(position))) {
            holder.btnAddToLiked.setVisibility(View.GONE);
            holder.btnRemoveFromLiked.setVisibility(View.VISIBLE);
        } else {
            holder.btnAddToLiked.setVisibility(View.VISIBLE);
            holder.btnRemoveFromLiked.setVisibility(View.GONE);
        };

        holder.btnDescription.setOnClickListener( view -> {
            Balloon balloon = createBalloon(holder.btnDescription.getContext(), desc);
            balloon.showAlignBottom(holder.btnDescription);
        });

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
                approveName(id,
                        male.get(),
                        female.get(),
                        (Activity) holder.btnRemoveFromLiked.getContext()
                );
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

    public void approveName(int nameCardId, boolean male, boolean female, Activity context) throws URISyntaxException {
        Log.d("SearcNameAdapter.approveName",  "nameCardId: " + nameCardId + " male: " + male + " female: " + female + " context: " + context.toString());
        ApiController.getInstance().addToLiked(nameCardId, male, female, context, new VolleyCallBack<NameCard>() {
            @Override
            public ArrayList<NameCardItem> onSuccess() {
                return null;
            }

            @Override
            public void onResponse(NameCard response) {
                Log.d("SearcNameAdapter.approveName", "Response: " + response.getName());
                Toast.makeText(context, "Nafni bætt við á lista", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Log.d("SearcNameAdapter.approveName", "Error: " + error);
            }
        });
    }

    public Balloon createBalloon(Context context, String text) {
        Balloon balloon = new Balloon.Builder(context)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setIsVisibleArrow(true)
                .setArrowPosition(0.5f)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setHeight(BalloonSizeSpec.WRAP)
                .setWidth(BalloonSizeSpec.WRAP)
                .setTextSize(15f)
                .setTextTypeface(Typeface.BOLD_ITALIC)
                .setText(text)
                .setTextColor(ContextCompat.getColor(context, R.color.black))
                .setBackgroundColor(ContextCompat.getColor(context, R.color.ambiant))
                .setCornerRadius(4f)
                .setBalloonAnimation(BalloonAnimation.FADE)
                .setDismissWhenClicked(true)
                .setDismissWhenTouchOutside(true)
                .setPadding(8)
                .setMarginRight(14)
                .setMarginLeft(14)
                .setAlpha(0.85f)
                .build();
        return balloon;
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
