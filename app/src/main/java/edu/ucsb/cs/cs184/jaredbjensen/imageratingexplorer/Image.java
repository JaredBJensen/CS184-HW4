package edu.ucsb.cs.cs184.jaredbjensen.imageratingexplorer;


import android.graphics.Bitmap;

public class Image {

    private Bitmap src;
    private String name;
    private int rating;

    public Image() {}

    public Image(Bitmap src, String name, int rating) {
        this.src = src;
        this.name = name;
        this.rating = rating;
    }

    public Bitmap getSrc() {
        return src;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

}
