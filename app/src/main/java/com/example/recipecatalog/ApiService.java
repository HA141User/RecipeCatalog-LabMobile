package com.example.recipecatalog; // PENTING: Sesuaikan dengan nama package-mu!

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    // Rute untuk mencari banyak resep (Digunakan di HomeFragment)
    @GET("search.php")
    Call<MealResponse> searchRecipes(@Query("s") String query);

    // Rute BARU untuk mengambil detail 1 resep (Akan digunakan di DetailActivity)
    @GET("lookup.php")
    Call<MealResponse> getRecipeDetails(@Query("i") String recipeId);
}