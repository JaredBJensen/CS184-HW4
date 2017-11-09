package edu.ucsb.cs.cs184.jaredbjensen.imageratingexplorer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ImageRatingDatabaseHelper extends SQLiteOpenHelper {
    private static final String CreateImageTable = "CREATE TABLE Image (Id integer PRIMARY KEY AUTOINCREMENT, Uri text NOT NULL UNIQUE, Rating real);";
    private static final String DatabaseName = "ImageRatingDatabase.db";
    private static ImageRatingDatabaseHelper Instance;
    private List<OnDatabaseChangeListener> Listeners;
    private static SQLiteDatabase db;
    private static Context context;

    private ImageRatingDatabaseHelper(Context context) {
        super(context, DatabaseName, null, 1);
        Listeners = new ArrayList<>();
        db = getWritableDatabase();
        this.context = context;
    }

    public static void Initialize(Context context) {
        Instance = new ImageRatingDatabaseHelper(context);
    }

    public static ImageRatingDatabaseHelper GetInstance() {
        return Instance;
    }

    public void Subscribe(OnDatabaseChangeListener listener) {
        Listeners.add(listener);
    }

    private boolean TryUpdate(Cursor cursor) {
        try {
            cursor.moveToFirst();
        } catch (SQLiteConstraintException exception) {
            return false;
        } finally {
            cursor.close();
        }
        NotifyListeners();
        return true;
    }

    private void NotifyListeners() {
        for (OnDatabaseChangeListener listener : Listeners) {
            listener.OnDatabaseChange();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateImageTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public interface OnDatabaseChangeListener {
        void OnDatabaseChange();
    }

    public static void insert(String table, String nullColumnHack, ContentValues values) {
        db.beginTransaction();
        db.insert(table, nullColumnHack, values);
        db.endTransaction();
    }

    public static void clearTable(String table) {
        db.beginTransaction();
        db.execSQL("DROP TABLE " + table);
        db.endTransaction();
    }

    public static ArrayList<Image> getImages() {
        ArrayList<Image> images = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM Image", null);
        if (cursor.moveToFirst()) {
            while(!cursor.isAfterLast()) {
                Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex("Uri")));
                int rating = cursor.getInt(cursor.getColumnIndex("Rating"));

                Bitmap src;
                try {
                    src = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                    images.add(new Image(src, "", rating));
                } catch(IOException e) {
                    e.printStackTrace();
                }

                cursor.moveToNext();
            }
        }
        cursor.close();

        return images;
    }
}