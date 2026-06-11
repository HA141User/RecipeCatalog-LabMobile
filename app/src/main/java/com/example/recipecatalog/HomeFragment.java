package com.example.recipecatalog; // PENTING: Sesuaikan dengan nama package-mu!

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvRecipes;
    private ProgressBar progressBar;
    private LinearLayout layoutError, layoutEmpty;
    private MaterialButton btnRefresh;
    private MaterialCardView btnThemeToggle;
    private ImageView ivThemeToggleIcon;
    private EditText etSearch;

    private TextView tvFilterPopular, tvFilterChicken, tvFilterBeef;
    private RecipeAdapter adapter;

    private static List<Recipe> cachedRecipes = new ArrayList<>();
    private static String cachedQuery = "";

    private String currentQuery;
    private boolean isProgrammaticChange = false;

    public HomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        rvRecipes = view.findViewById(R.id.rvRecipes);
        progressBar = view.findViewById(R.id.progressBar);
        layoutError = view.findViewById(R.id.layoutError);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        btnThemeToggle = view.findViewById(R.id.btnThemeToggle);
        ivThemeToggleIcon = view.findViewById(R.id.ivThemeToggleIcon);
        etSearch = view.findViewById(R.id.etSearch);
        tvFilterPopular = view.findViewById(R.id.tvFilterPopular);
        tvFilterChicken = view.findViewById(R.id.tvFilterChicken);
        tvFilterBeef = view.findViewById(R.id.tvFilterBeef);

        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- KUNCI PERBAIKAN: Set Icon Awal Sesuai Mode ---
        int initialNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean initialIsDark = (initialNightMode == Configuration.UI_MODE_NIGHT_YES);
        ivThemeToggleIcon.setImageResource(initialIsDark ? R.drawable.ic_sun : R.drawable.ic_moon);

        swipeRefresh.setColorSchemeColors(Color.parseColor("#E51D2A"));
        swipeRefresh.setOnRefreshListener(() -> {
            cachedRecipes.clear();
            fetchRecipes(currentQuery);
        });

        currentQuery = cachedQuery;

        if (!currentQuery.equals("") && !currentQuery.equals("chicken") && !currentQuery.equals("beef")) {
            isProgrammaticChange = true;
            etSearch.setText(currentQuery);
            isProgrammaticChange = false;
            resetAllChips();
        } else {
            if(currentQuery.equals("")) setActiveChip(tvFilterPopular);
            if(currentQuery.equals("chicken")) setActiveChip(tvFilterChicken);
            if(currentQuery.equals("beef")) setActiveChip(tvFilterBeef);
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isProgrammaticChange && s.toString().trim().isEmpty() && etSearch.hasFocus()) {
                    currentQuery = "";
                    setActiveChip(tvFilterPopular);
                    fetchRecipes(currentQuery);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        SharedPreferences prefs = requireActivity().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);

        btnThemeToggle.setOnClickListener(v -> {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean currentlyDark = (currentNightMode == Configuration.UI_MODE_NIGHT_YES);
            boolean newDark = !currentlyDark;

            prefs.edit().putBoolean("isDark", newDark).apply();
            AppCompatDelegate.setDefaultNightMode(newDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        tvFilterPopular.setOnClickListener(v -> { setActiveChip(tvFilterPopular); currentQuery = ""; isProgrammaticChange = true; etSearch.setText(""); isProgrammaticChange = false; fetchRecipes(currentQuery); });
        tvFilterChicken.setOnClickListener(v -> { setActiveChip(tvFilterChicken); currentQuery = "chicken"; isProgrammaticChange = true; etSearch.setText(""); isProgrammaticChange = false; fetchRecipes(currentQuery); });
        tvFilterBeef.setOnClickListener(v -> { setActiveChip(tvFilterBeef); currentQuery = "beef"; isProgrammaticChange = true; etSearch.setText(""); isProgrammaticChange = false; fetchRecipes(currentQuery); });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String query = etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    currentQuery = query;
                    resetAllChips();
                    fetchRecipes(currentQuery);
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    etSearch.clearFocus();
                }
                return true;
            }
            return false;
        });

        if (!cachedRecipes.isEmpty()) {
            adapter = new RecipeAdapter(getContext(), cachedRecipes);
            rvRecipes.setAdapter(adapter);
            rvRecipes.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
        } else {
            fetchRecipes(currentQuery);
        }

        btnRefresh.setOnClickListener(v -> fetchRecipes(currentQuery));
    }

    private void setActiveChip(TextView activeChip) {
        resetAllChips();
        activeChip.setBackgroundResource(R.drawable.bg_search_bar);
        activeChip.setTextColor(Color.parseColor("#E51D2A"));
    }

    private void resetAllChips() {
        tvFilterPopular.setBackground(null); tvFilterPopular.setTextColor(Color.parseColor("#808080"));
        tvFilterChicken.setBackground(null); tvFilterChicken.setTextColor(Color.parseColor("#808080"));
        tvFilterBeef.setBackground(null); tvFilterBeef.setTextColor(Color.parseColor("#808080"));
    }

    private void fetchRecipes(String query) {
        if (!swipeRefresh.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        layoutError.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        rvRecipes.setVisibility(View.GONE);

        String apiQuery = query;
        if (query.equals("")) {
            String[] alphabets = {"a", "b", "c", "e", "f", "p", "m", "r", "s"};
            apiQuery = alphabets[(int) (Math.random() * alphabets.length)];
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<MealResponse> call = apiService.searchRecipes(apiQuery);

        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<MealResponse.Meal> meals = response.body().getMeals();
                    if (meals == null || meals.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        cachedRecipes.clear();
                    } else {
                        List<Recipe> mappedList = new ArrayList<>();
                        for (MealResponse.Meal data : meals) {
                            Recipe r = new Recipe();
                            try { r.setId(Integer.parseInt(data.getIdMeal())); } catch (Exception e) { r.setId(data.getIdMeal().hashCode()); }
                            r.setTitle(data.getStrMeal());
                            r.setImage(data.getStrMealThumb());
                            mappedList.add(r);
                        }

                        cachedRecipes = mappedList;
                        cachedQuery = query;

                        adapter = new RecipeAdapter(getContext(), mappedList);
                        rvRecipes.setAdapter(adapter);
                        rvRecipes.setVisibility(View.VISIBLE);
                    }
                } else {
                    layoutError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                layoutError.setVisibility(View.VISIBLE);
            }
        });
    }
}