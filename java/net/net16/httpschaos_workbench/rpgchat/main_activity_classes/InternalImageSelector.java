package net.net16.httpschaos_workbench.rpgchat.main_activity_classes;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Daniel on 03/05/2018.
 *
 */

public class InternalImageSelector implements DialogInterface.OnClickListener {

    private Activity activity;
    private ImageView imageView;
    private File selected;
    private File current;

    InternalImageSelector(Activity activity, ImageView imageView) {
        this.activity = activity;
        this.imageView = imageView;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        final ListView fileList = new ListView(activity);
        final ArrayAdapter<String> fileListAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1);
        current = Environment.getExternalStorageDirectory();
        fileListAdapter.add("...");
        fileListAdapter.addAll(current.list());
        fileList.setAdapter(fileListAdapter);

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(current.getAbsolutePath())
                .setView(fileList)
                .setNegativeButton("Cancelar", null)
                .show();

        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if ("...".equals(fileListAdapter.getItem(i)) && current != Environment.getExternalStorageDirectory()) {
                    selected = current.getParentFile();
                    current = current.getParentFile();
                    fileListAdapter.clear();
                    fileListAdapter.add("...");
                    if (selected.list() != null) fileListAdapter.addAll(selected.list());
                    fileList.setAdapter(fileListAdapter);

                } else if ("...".equals(fileListAdapter.getItem(i)) && current == Environment.getExternalStorageDirectory()) {
                    Toast.makeText(activity, "Esse diretório já é o raiz...", Toast.LENGTH_SHORT).show();
                } else {
                    selected = new File(current, ""+fileListAdapter.getItem(i));
                    String n = selected.getName();
                    if (!selected.isFile()) {
                        current = selected;
                        fileListAdapter.clear();
                        fileListAdapter.add("...");
                        fileListAdapter.addAll(current.list());
                        fileList.setAdapter(fileListAdapter);
                    } else if (n.endsWith(".jpg") || n.endsWith(".bitmap") || n.endsWith(".bmp") || n.endsWith(".jpeg") || n.endsWith(".jpe") || n.endsWith(".png")) {
                        Bitmap img = BitmapFactory.decodeFile(selected.getPath());
                        imageView.setImageBitmap(img);
                        byte data[] = new byte[8];

                        try {
                            FileInputStream fis = new FileInputStream(selected);
                            data = new byte[fis.available()];
                            fis.read(data);
                            fis.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;
                        final StorageReference ppRef = FirebaseStorage.getInstance().getReference("Users/" + user.getDisplayName() + "/Profile Picture");
                        ppRef.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                ppRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        user.updateProfile(new UserProfileChangeRequest.Builder().setPhotoUri(task.getResult()).build());
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(activity, "Formato de arquivo não suportado", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.setTitle(current.getAbsolutePath());
            }
        });
    }
}
