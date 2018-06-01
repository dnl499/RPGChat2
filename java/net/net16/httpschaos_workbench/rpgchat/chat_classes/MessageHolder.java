package net.net16.httpschaos_workbench.rpgchat.chat_classes;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Daniel on 25/05/2018.
 *
 */

public class MessageHolder {

    private LinearLayout layout;
    private TextView date, message, name;

    public MessageHolder(Context context, boolean send) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        layout = new LinearLayout(context);
        date = new TextView(context);
        message = new TextView(context);
        name = new TextView(context);
        layout.addView(name);
        layout.addView(message);
        layout.addView(date);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(0xAA606090);
        name.setTextSize(Float.valueOf(String.valueOf(message.getTextSize()*0.4)));
        date.setTextSize(Float.valueOf(String.valueOf(message.getTextSize()*0.4)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (send) params.setMargins(metrics.widthPixels/5, 0, metrics.widthPixels/20, metrics.heightPixels/50);
        else params.setMargins(metrics.widthPixels/20, 0, metrics.widthPixels/5, metrics.heightPixels/50);
        layout.setLayoutParams(params);
    }

    public void setMessage(String msg) {message.setText(Html.fromHtml(msg));}

    public void setSender(String sender) {name.setText(sender);}

    public void setDate(String date) {this.date.setText(date);}

    public LinearLayout getView() {return layout;}
}
