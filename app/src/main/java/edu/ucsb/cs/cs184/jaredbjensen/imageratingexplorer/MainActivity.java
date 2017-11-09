package edu.ucsb.cs.cs184.jaredbjensen.imageratingexplorer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final int DEFAULT_RATING = 0;

    RecyclerView recyclerView;
    ArrayList<Image> images;
    public ImageAdapter imageAdapter;

    File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("ImageRatingExplorer");

        dir = getExternalFilesDir(null);

        ImageRatingDatabaseHelper.Initialize(this);

        images = ImageRatingDatabaseHelper.getImages();
        for (Image i : images) {
            Log.i("", i.getName());
        }
        imageAdapter = new ImageAdapter(images, this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbarmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_populate:
                populateDB();
                return true;
            case R.id.action_clear:
                clearDB();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageRatingDatabaseHelper.GetInstance().close();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // Show your dialog here (this is called right after onActivityResult)
    }

    public void populateDB() {
        ImageRetriever.getImageList(new ImageRetriever.ImageListResultListener() {
            @Override
            public void onImageList(ArrayList<String> list) {
                for (int i=0; i<list.size(); i++) {
                    final String name = list.get(i);
                    ImageRetriever.getImageByIndex(i, new ImageRetriever.ImageResultListener() {
                        @Override
                        public void onImage(Bitmap image) {
                            File file = new File(getExternalFilesDir(null), name);
                            try (FileOutputStream stream = new FileOutputStream(file)){
                                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                image.recycle();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ContentValues values = new ContentValues();
                            values.put("Uri", Uri.fromFile(getFileStreamPath(name)).toString());
                            values.put("Rating", 0);

                            ImageRatingDatabaseHelper.insert("Image", null, values);

                            images.add(new Image(image, name, DEFAULT_RATING));
                            imageAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    public void clearDB() {
        ImageRatingDatabaseHelper.clearTable("Image");
        images.clear();
        imageAdapter.notifyDataSetChanged();

        File[] files = getExternalFilesDir(null).listFiles();
        for (int i=0; i<files.length; i++) {
            files[i].delete();
        }
    }

    public void cameraButtonClicked(View v) {
        // camera intent
    }

    public void galleryButtonClicked(View v) {
        // gallery intent
    }

}
