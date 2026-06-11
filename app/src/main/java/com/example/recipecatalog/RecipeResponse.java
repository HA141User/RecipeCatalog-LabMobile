package com.example.recipecatalog;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RecipeResponse {

    // "results" adalah nama array bawaan dari JSON Spoonacular API
    @SerializedName("results")
    private List<Recipe> results;

    public List<Recipe> getResults() {
        return results;
    }

    public void setResults(List<Recipe> results) {
        this.results = results;
    }
}