package com.example.recipecatalog; // PENTING: Sesuaikan dengan nama package-mu!

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView ivDetailImage;
    private TextView tvDetailTitle, tvDetailIngredients, tvDetailInstructions;
    private MaterialCardView btnBack;
    private FloatingActionButton fabFavoriteDetail;

    private DatabaseHelper dbHelper;
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    private String recipeId;
    private String recipeTitle;
    private String recipeImage;
    private Recipe currentRecipeObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_recipe_detail);

        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailIngredients = findViewById(R.id.tvDetailIngredients);
        tvDetailInstructions = findViewById(R.id.tvDetailInstructions);
        btnBack = findViewById(R.id.btnBack);
        fabFavoriteDetail = findViewById(R.id.fabFavoriteDetail);

        dbHelper = new DatabaseHelper(this);
        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        recipeId = getIntent().getStringExtra("RECIPE_ID");
        recipeTitle = getIntent().getStringExtra("RECIPE_TITLE");
        recipeImage = getIntent().getStringExtra("RECIPE_IMAGE");

        tvDetailTitle.setText(recipeTitle != null ? recipeTitle : "Loading...");
        if (recipeImage != null) {
            Glide.with(this).load(recipeImage).centerCrop().into(ivDetailImage);
        }

        currentRecipeObj = new Recipe();
        try {
            currentRecipeObj.setId(Integer.parseInt(recipeId));
        } catch (Exception e) {
            currentRecipeObj.setId(recipeId.hashCode());
        }
        currentRecipeObj.setTitle(recipeTitle);
        currentRecipeObj.setImage(recipeImage);

        btnBack.setOnClickListener(v -> onBackPressed());

        checkFavoriteStatus();

        fabFavoriteDetail.setOnClickListener(v -> toggleFavorite());

        if (recipeId != null) {
            fetchRecipeDetails(recipeId);
        }
    }

    private void fetchRecipeDetails(String id) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getRecipeDetails(id).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    MealResponse.Meal detailData = response.body().getMeals().get(0);
                    tvDetailIngredients.setText(detailData.getIngredientsFormatted());
                    if (detailData.getStrInstructions() != null) {
                        tvDetailInstructions.setText(detailData.getStrInstructions());
                    } else {
                        tvDetailInstructions.setText("Instructions not available.");
                    }
                } else {
                    tvDetailIngredients.setText("Failed to load ingredients.");
                    tvDetailInstructions.setText("Failed to load instructions.");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                tvDetailIngredients.setText("Please check your internet connection.");
                tvDetailInstructions.setText("Please check your internet connection.");
            }
        });
    }

    private void checkFavoriteStatus() {
        executorService.execute(() -> {
            boolean isFav = dbHelper.isFavorite(currentRecipeObj.getId());
            mainThreadHandler.post(() -> {
                if (isFav) {
                    fabFavoriteDetail.setImageResource(android.R.drawable.star_on);
                    fabFavoriteDetail.setColorFilter(Color.parseColor("#E51D2A"));
                } else {
                    fabFavoriteDetail.setImageResource(android.R.drawable.star_off);
                    fabFavoriteDetail.setColorFilter(Color.parseColor("#808080"));
                }
            });
        });
    }

    private void toggleFavorite() {
        executorService.execute(() -> {
            boolean isFav = dbHelper.isFavorite(currentRecipeObj.getId());
            if (isFav) {
                dbHelper.deleteFavorite(currentRecipeObj.getId());
                mainThreadHandler.post(() -> {
                    fabFavoriteDetail.setImageResource(android.R.drawable.star_off);
                    fabFavoriteDetail.setColorFilter(Color.parseColor("#808080"));
                    Toast.makeText(RecipeDetailActivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                });
            } else {
                dbHelper.insertFavorite(currentRecipeObj);
                mainThreadHandler.post(() -> {
                    fabFavoriteDetail.setImageResource(android.R.drawable.star_on);
                    fabFavoriteDetail.setColorFilter(Color.parseColor("#E51D2A"));
                    Toast.makeText(RecipeDetailActivity.this, "Saved to favorites", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}