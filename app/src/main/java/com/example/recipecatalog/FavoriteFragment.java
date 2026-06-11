package com.example.recipecatalog; // PENTING: Sesuaikan dengan nama package-mu!

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteFragment extends Fragment {

    private RecyclerView rvFavoriteRecipes;
    private LinearLayout layoutEmptyFavorite;
    private RecipeAdapter adapter;
    private DatabaseHelper dbHelper;
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFavoriteRecipes = view.findViewById(R.id.rvFavoriteRecipes);
        layoutEmptyFavorite = view.findViewById(R.id.layoutEmptyFavorite);

        rvFavoriteRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DatabaseHelper(getContext());

        // Setup background thread agar akses SQLite tidak bikin lag layar
        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    // onResume akan dipanggil SETIAP KALI user membuka tab Favorit ini
    @Override
    public void onResume() {
        super.onResume();
        loadFavoriteRecipes();
    }

    private void loadFavoriteRecipes() {
        executorService.execute(() -> {
            // Tarik data dari SQLite (Background Thread)
            List<Recipe> favoriteList = dbHelper.getAllFavorites();

            // Tampilkan ke layar (Main Thread)
            mainThreadHandler.post(() -> {
                if (favoriteList == null || favoriteList.isEmpty()) {
                    // Jika kosong, tampilkan Empty State
                    layoutEmptyFavorite.setVisibility(View.VISIBLE);
                    rvFavoriteRecipes.setVisibility(View.GONE);
                } else {
                    // Jika ada isinya, panggil RecipeAdapter (Kartu Premium otomatis terpakai)
                    layoutEmptyFavorite.setVisibility(View.GONE);
                    adapter = new RecipeAdapter(getContext(), favoriteList);
                    rvFavoriteRecipes.setAdapter(adapter);
                    rvFavoriteRecipes.setVisibility(View.VISIBLE);
                }
            });
        });
    }
}