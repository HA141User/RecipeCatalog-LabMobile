package com.example.recipecatalog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RecipeDb";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FAVORITE = "favorite";

    // Kolom tabel
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_IMAGE = "image";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat tabel dengan ID resep sebagai Primary Key
        String createTable = "CREATE TABLE " + TABLE_FAVORITE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_IMAGE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        onCreate(db);
    }

    // --- FUNGSI UNTUK MENGELOLA DATA ---

    // 1. Menambah ke favorit
    public void insertFavorite(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, recipe.getId());
        values.put(COLUMN_TITLE, recipe.getTitle());
        values.put(COLUMN_IMAGE, recipe.getImage());
        db.insert(TABLE_FAVORITE, null, values);
        db.close();
    }

    // 2. Menghapus dari favorit
    public void deleteFavorite(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITE, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // 3. Mengecek apakah resep sudah ada di favorit (untuk mengubah warna tombol)
    public boolean isFavorite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITE, new String[]{COLUMN_ID},
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    // 4. Mengambil semua data favorit untuk ditampilkan di FavoriteFragment
    public List<Recipe> getAllFavorites() {
        List<Recipe> favoriteList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FAVORITE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                recipe.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                recipe.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                recipe.setImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)));
                favoriteList.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteList;
    }
}