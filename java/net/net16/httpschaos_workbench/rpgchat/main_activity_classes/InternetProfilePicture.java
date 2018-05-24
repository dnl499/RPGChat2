package net.net16.httpschaos_workbench.rpgchat.main_activity_classes;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Daniel on 03/05/2018.
 *
 */

public class InternetProfilePicture implements DialogInterface.OnClickListener {

    private Activity activity;
    private ImageView imageView;

    InternetProfilePicture(Activity activity, ImageView imageView) {
        this.activity = activity;
        this.imageView = imageView;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        final EditText urlInput = new EditText(activity);
        new AlertDialog.Builder(activity)
                .setTitle("Digite a URL")
                .setView(urlInput)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Pronto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new ImageDownloader(activity, urlInput.getText().toString(), imageView)).start();
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        assert user != null;
                        user.updateProfile(new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(urlInput.getText().toString())).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Here the new profile picture will be stored in the FirebaseStorage
                                        try {
                                            FirebaseStorage storage = FirebaseStorage.getInstance();
                                            StorageReference profilePic = storage.getReference("Users/" + user.getDisplayName() + "/Profile Picture");
                                            assert user.getPhotoUrl() != null;
                                            URL url = new URL(user.getPhotoUrl().toString());
                                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                            conn.setDoInput(true);
                                            InputStream is = conn.getInputStream();
                                            Bitmap img = BitmapFactory.decodeStream(is);
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            profilePic.putBytes(baos.toByteArray());
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                    }
                })
                .show();
    }
}
