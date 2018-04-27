package net.net16.httpschaos_workbench.rpgchat.main_activity_classes;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Daniel on 12/04/2018.
 * This class will download the image of a given URL and display it in the target ImageView
 */

public class ImageDownloader implements Runnable {

    private String img;
    private Activity activity;
    private ImageView picture;

    public ImageDownloader(Activity activity, String img, ImageView picture) {
        this.activity = activity;
        this.img = img;
        this.picture = picture;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(img);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            InputStream is = conn.getInputStream();
            final Bitmap img = BitmapFactory.decodeStream(is);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    picture.setImageBitmap(img);
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
