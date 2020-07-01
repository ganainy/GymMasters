package ganainy.dev.gymmasters.ui.main.exercisesCategories;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import ganainy.dev.gymmasters.R;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.UserViewHolder> {
    private static final String TAG = "CategoriesAdapter";
    private  List<Pair<String, Drawable>> categoryList;
    private CategoryCallback categoryCallback;


    public CategoriesAdapter(CategoryCallback categoryCallback) {
        this.categoryCallback = categoryCallback;
    }

    public void setData(List<Pair<String, Drawable>> userList){
        this.categoryList =userList;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item,
                viewGroup, false);
        return new CategoriesAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        Pair<String, Drawable> currentCategory = categoryList.get(i);

        if (categoryList.get(i).first!=null) {
            userViewHolder.textViewCategoryName.setText(categoryList.get(i).first);
        }

        if (categoryList.get(i).second!=null) {
            userViewHolder.categoryImageView.setImageDrawable(categoryList.get(i).second);
        }
    }



    @Override
    public int getItemCount() {
        return categoryList ==null?0: categoryList.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.categoryImageView)
        ImageView categoryImageView;

        @BindView(R.id.textViewCategoryName)
        TextView textViewCategoryName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> {
              categoryCallback.onCategorySelected(categoryList.get(getAdapterPosition()).first);
            });

        }
    }


}
