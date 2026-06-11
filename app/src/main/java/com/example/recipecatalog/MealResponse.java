package com.example.recipecatalog; // PENTING: Sesuaikan dengan nama package-mu!

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MealResponse {
    @SerializedName("meals")
    private List<Meal> meals;

    public List<Meal> getMeals() {
        return meals;
    }

    public static class Meal {
        @SerializedName("idMeal") private String idMeal;
        @SerializedName("strMeal") private String strMeal;
        @SerializedName("strMealThumb") private String strMealThumb;
        @SerializedName("strInstructions") private String strInstructions;
        @SerializedName("strYoutube") private String strYoutube;

        // 10 Bahan Pertama (API TheMealDB)
        @SerializedName("strIngredient1") private String strIngredient1;
        @SerializedName("strIngredient2") private String strIngredient2;
        @SerializedName("strIngredient3") private String strIngredient3;
        @SerializedName("strIngredient4") private String strIngredient4;
        @SerializedName("strIngredient5") private String strIngredient5;
        @SerializedName("strIngredient6") private String strIngredient6;
        @SerializedName("strIngredient7") private String strIngredient7;
        @SerializedName("strIngredient8") private String strIngredient8;
        @SerializedName("strIngredient9") private String strIngredient9;
        @SerializedName("strIngredient10") private String strIngredient10;

        // 10 Takaran Pertama
        @SerializedName("strMeasure1") private String strMeasure1;
        @SerializedName("strMeasure2") private String strMeasure2;
        @SerializedName("strMeasure3") private String strMeasure3;
        @SerializedName("strMeasure4") private String strMeasure4;
        @SerializedName("strMeasure5") private String strMeasure5;
        @SerializedName("strMeasure6") private String strMeasure6;
        @SerializedName("strMeasure7") private String strMeasure7;
        @SerializedName("strMeasure8") private String strMeasure8;
        @SerializedName("strMeasure9") private String strMeasure9;
        @SerializedName("strMeasure10") private String strMeasure10;

        public String getIdMeal() { return idMeal; }
        public String getStrMeal() { return strMeal; }
        public String getStrMealThumb() { return strMealThumb; }
        public String getStrInstructions() { return strInstructions; }
        public String getStrYoutube() { return strYoutube; }

        // Helper Cerdas: Menyusun bahan & takaran menjadi teks rapi secara otomatis
        public String getIngredientsFormatted() {
            StringBuilder sb = new StringBuilder();
            appendIngredient(sb, strIngredient1, strMeasure1);
            appendIngredient(sb, strIngredient2, strMeasure2);
            appendIngredient(sb, strIngredient3, strMeasure3);
            appendIngredient(sb, strIngredient4, strMeasure4);
            appendIngredient(sb, strIngredient5, strMeasure5);
            appendIngredient(sb, strIngredient6, strMeasure6);
            appendIngredient(sb, strIngredient7, strMeasure7);
            appendIngredient(sb, strIngredient8, strMeasure8);
            appendIngredient(sb, strIngredient9, strMeasure9);
            appendIngredient(sb, strIngredient10, strMeasure10);
            return sb.toString();
        }

        private void appendIngredient(StringBuilder sb, String ingredient, String measure) {
            if (ingredient != null && !ingredient.trim().isEmpty()) {
                sb.append("• ").append(ingredient);
                if (measure != null && !measure.trim().isEmpty()) {
                    sb.append(" (").append(measure.trim()).append(")");
                }
                sb.append("\n");
            }
        }
    }
}