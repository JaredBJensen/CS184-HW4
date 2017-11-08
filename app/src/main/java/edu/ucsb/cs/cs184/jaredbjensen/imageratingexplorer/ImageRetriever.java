package edu.ucsb.cs.cs184.jaredbjensen.imageratingexplorer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;


public class ImageRetriever {
    private static String baseUrl = "http://ec2-52-53-191-204.us-west-1.compute.amazonaws.com:8080/";

    public static void getImageList(final ImageListResultListener listener) {
        RetrieveImageListTask retrieveImageListTask = new RetrieveImageListTask(listener);
        retrieveImageListTask.execute();
    }

    public static void getImageByIndex(int index, final ImageResultListener listener) {
        RetrieveImageByIndexTask retrieveTaggedImageTask = new RetrieveImageByIndexTask(listener);
        retrieveTaggedImageTask.execute(new Integer(index));
    }

    interface ImageListResultListener {
        void onImageList(ArrayList<String> list);
    }

    interface ImageResultListener {
        void onImage(Bitmap image);
    }

    private static class RetrieveImageListTask extends AsyncTask<Void, Void, ArrayList<String>> {
        private ImageListResultListener listener;

        public RetrieveImageListTask(ImageListResultListener listener) {
            super();
            this.listener = listener;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            try {
                URL pageUrl = new URL(baseUrl + "ls");
                URLConnection connection = pageUrl.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder pageHtmlBuilder = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    pageHtmlBuilder.append(inputLine);
                    pageHtmlBuilder.append('\n');
                }
                reader.close();
                String[] files = pageHtmlBuilder.toString().split("\n");
                ArrayList<String> picNames = new ArrayList<>();
                for (String file : files) {
                    picNames.add(file);
                }
                return picNames;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> picNames) {
            if (listener != null) {
                listener.onImageList(picNames);
            }
        }
    }

    private static class RetrieveImageByIndexTask extends AsyncTask<Integer, Void, Bitmap> {
        private ImageResultListener listener;

        public RetrieveImageByIndexTask(ImageResultListener listener) {
            super();
            this.listener = listener;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            try {
                URL imageUrl = new URL(baseUrl + "img/" + params[0] + "/pic");
                URLConnection imageConnection = imageUrl.openConnection();
                Bitmap image = BitmapFactory.decodeStream(imageConnection.getInputStream());

                URL pageUrl = new URL(baseUrl + "img/" + params[0] + "/tags");
                URLConnection connection = pageUrl.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder pageHtmlBuilder = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    pageHtmlBuilder.append(inputLine);
                    pageHtmlBuilder.append('\n');
                }
                reader.close();
                String[] tags = pageHtmlBuilder.toString().split("\n");
                ArrayList<String> picTags = new ArrayList<String>(Arrays.asList(tags));
                return image;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            if (listener != null) {
                listener.onImage(image);
            }
        }
    }
}