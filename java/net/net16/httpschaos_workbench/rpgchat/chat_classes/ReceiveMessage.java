package net.net16.httpschaos_workbench.rpgchat.chat_classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import net.net16.httpschaos_workbench.rpgchat.R;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Daniel on 24/05/2018.
 *
 */

public class ReceiveMessage implements ValueEventListener {

    private Activity activity;
    private DisplayMetrics metrics;

    public ReceiveMessage(Activity activity, DisplayMetrics metrics) {
        this.activity = activity;
        this.metrics = metrics;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        LinearLayout messageList = activity.findViewById(R.id.chat_messages);
        messageList.setPadding(0, metrics.heightPixels/50, 0, metrics.heightPixels/50);
        messageList.removeAllViews();

        for (DataSnapshot data: dataSnapshot.getChildren()) {
            String b = "" + data.getValue(String.class);
            Date date = new Date();
            date.setTime((-1)*Long.parseLong(data.getKey()));
            DateFormat format = DateFormat.getDateInstance();
            TextView neo = new TextView(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(metrics.widthPixels/20, 0, metrics.widthPixels/20, metrics.heightPixels/50);
            neo.setLayoutParams(params);
            neo.setBackgroundColor(0xAA606090);
            neo.setText(Html.fromHtml(format.format(date) + " - " + b.split("\t")[0]));
            if (b.contains("\tme")) {
                neo.setGravity(Gravity.END);
                neo.setPadding(metrics.widthPixels/10, 10, metrics.widthPixels/50, 10);
            } else neo.setPadding(metrics.widthPixels/50, 10, metrics.widthPixels/10, 10);
            messageList.addView(neo);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
