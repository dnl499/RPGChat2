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
import java.util.Calendar;
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

            Calendar cal = Calendar.getInstance();

            MessageHolder mh;
            if (b.contains("\tme")) mh = new MessageHolder(activity, true);
            else mh = new MessageHolder(activity, false);
            mh.setMessage(b.split("\t")[0]);
            mh.setDate(cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + " (" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1) + " de " + cal.get(Calendar.YEAR) + ")");
            messageList.addView(mh.getView());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
