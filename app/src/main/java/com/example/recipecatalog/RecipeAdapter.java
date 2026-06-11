package com.example.recipecatalog; // PENTING: Sesuaikan dengan nama package-mu!

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<Recipe> recipeList;
    private DatabaseHelper dbHelper;
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
        this.dbHelper = new DatabaseHelper(context);
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.tvRecipeTitle.setText(recipe.getTitle());

        Glide.with(context)
                .load(recipe.getImage())
                .centerCrop()
                .into(holder.ivRecipeImage);

        executorService.execute(() -> {
            boolean isFavorite = dbHelper.isFavorite(recipe.getId());
            mainThreadHandler.post(() -> {
                if (isFavorite) {
                    holder.ivFavoriteIcon.setImageResource(android.R.drawable.star_on);
                    holder.ivFavoriteIcon.setColorFilter(Color.parseColor("#E51D2A"));
                } else {
                    holder.ivFavoriteIcon.setImageResource(android.R.drawable.star_off);
                    holder.ivFavoriteIcon.setColorFilter(Color.parseColor("#808080"));
                }
            });
        });

        holder.btnFavoriteCard.setOnClickListener(v -> {
            executorService.execute(() -> {
                boolean isFav = dbHelper.isFavorite(recipe.getId());
                if (isFav) {
                    dbHelper.deleteFavorite(recipe.getId());
                    mainThreadHandler.post(() -> {
                        holder.ivFavoriteIcon.setImageResource(android.R.drawable.star_off);
                        holder.ivFavoriteIcon.setColorFilter(Color.parseColor("#808080"));
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    dbHelper.insertFavorite(recipe);
                    mainThreadHandler.post(() -> {
                        holder.ivFavoriteIcon.setImageResource(android.R.drawable.star_on);
                        holder.ivFavoriteIcon.setColorFilter(Color.parseColor("#E51D2A"));
                        Toast.makeText(context, "Saved to favorites", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_ID", String.valueOf(recipe.getId()));
            intent.putExtra("RECIPE_TITLE", recipe.getTitle());
            intent.putExtra("RECIPE_IMAGE", recipe.getImage());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList == null ? 0 : recipeList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecipeImage, ivFavoriteIcon;
        TextView tvRecipeTitle;
        MaterialCardView btnFavoriteCard;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecipeImage = itemView.findViewById(R.id.ivRecipeImage);
            tvRecipeTitle = itemView.findViewById(R.id.tvRecipeTitle);
            btnFavoriteCard = itemView.findViewById(R.id.btnFavoriteCard);
            ivFavoriteIcon = itemView.findViewById(R.id.ivFavoriteIcon);
        }
    }
}