package edu.ucsb.cs.cs184.jaredbjensen.imageratingexplorer;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<Image> images;
    private Context context;

    public ImageAdapter(List<Image> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View imageView = inflater.inflate(R.layout.image_view, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(imageView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Image image = images.get(position);

        // Set item views based on your views and data model
        viewHolder.ratingBar.setRating(image.getRating());
        ImageView imageView = viewHolder.imageView;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        try (FileOutputStream stream = context.openFileOutput(timestamp, Context.MODE_PRIVATE)){
            image.getSrc().compress(Bitmap.CompressFormat.JPEG, 100, stream);
            image.getSrc().recycle();
            Log.i("", "file opened");
        } catch (IOException e) {
            Log.e("fileoutput", e.getMessage());
        }
        Picasso.with(context).load(context.getFileStreamPath(timestamp)).resize(500,500).centerCrop().into(imageView);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView imageView;
        public RatingBar ratingBar;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageLarge);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBarLarge);
        }
    }

}
