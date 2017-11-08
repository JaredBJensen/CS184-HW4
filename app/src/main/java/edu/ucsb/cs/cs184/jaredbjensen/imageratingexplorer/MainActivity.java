package edu.ucsb.cs.cs184.jaredbjensen.imageratingexplorer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final int DEFAULT_RATING = 0;

    RecyclerView recyclerView;
    ArrayList<Image> images;
    ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("ImageRatingExplorer");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        images = new ArrayList<>();

        imageAdapter = new ImageAdapter(images, this);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageRatingDatabaseHelper.Initialize(this);

        ImageRetriever.getImageList(new ImageRetriever.ImageListResultListener() {
            @Override
            public void onImageList(ArrayList<String> list) {
                for (int i=0; i<list.size(); i++) {
                    ImageRetriever.getImageByIndex(i, new ImageRetriever.ImageResultListener() {
                        @Override
                        public void onImage(Bitmap image) {
//                            try (FileOutputStream stream = openFileOutput("Test.jpg", Context.MODE_PRIVATE)){
//                                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                                image.recycle();
//                            } catch (IOException e) {
//                            }
//                            Picasso.with(MainActivity.this).load(getFileStreamPath("Test.jpg")).resize(500,500).centerCrop().into(imageView);
                            images.add(new Image(image, DEFAULT_RATING));
                        }
                    });
                }
            }
        });

        imageAdapter.notifyDataSetChanged();
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

    }

    public void clearDB() {

    }
}
