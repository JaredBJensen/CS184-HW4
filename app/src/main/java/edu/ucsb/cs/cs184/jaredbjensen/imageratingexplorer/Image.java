package edu.ucsb.cs.cs184.jaredbjensen.imageratingexplorer;


import android.graphics.Bitmap;

public class Image {

    private Bitmap src;
    private int rating;

    public Image() {}

    public Image(Bitmap src, int rating) {
        this.src = src;
        this.rating = rating;
    }

    public Bitmap getSrc() {
        return src;
    }

    public int getRating() {
        return rating;
    }

}
