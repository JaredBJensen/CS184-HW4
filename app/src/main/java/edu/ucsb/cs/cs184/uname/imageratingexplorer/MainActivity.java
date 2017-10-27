package edu.ucsb.cs.cs184.uname.imageratingexplorer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageRatingDatabaseHelper.Initialize(this);

        final TextView textView = (TextView)findViewById(R.id.textView);
        final ImageView imageView = (ImageView)findViewById(R.id.imageView);

        ImageRetriever.getImageList(new ImageRetriever.ImageListResultListener() {
            @Override
            public void onImageList(ArrayList<String> list) {
                textView.setText(String.format("Total number: %d", list.size()));
                ImageRetriever.getImageByIndex(0, new ImageRetriever.ImageResultListener() {
                    @Override
                    public void onImage(Bitmap image) {
                        try (FileOutputStream stream = openFileOutput("Test.jpg", Context.MODE_PRIVATE)){
                            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            image.recycle();
                        } catch (IOException e) {
                        }
                        Picasso.with(MainActivity.this).load(getFileStreamPath("Test.jpg")).resize(500,500).centerCrop().into(imageView);
                    }
                });
            }
        });
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
}
