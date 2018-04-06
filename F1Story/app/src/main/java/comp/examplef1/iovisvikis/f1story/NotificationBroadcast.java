package comp.examplef1.iovisvikis.f1story;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import comp.examplef1.iovisvikis.f1story.R;

/**
 * Created by ioannisvisvikis on 15/7/17.
 */

public class NotificationBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String circuitName = intent.getStringExtra("CIRCUIT");
        String eventKind = intent.getStringExtra("KIND");
        String timeOff = intent.getStringExtra("TIME_OFF");

        int notificationID = intent.getIntExtra("ID", 0);

        int largeImageCode = (eventKind.equalsIgnoreCase("race")) ?
                                                        R.drawable.start_grid_white : R.drawable.notify_timer_small;

        Notification notif = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.f1_ticker)
                .setTicker("F1 " + eventKind + " " + "starts in" + " " + timeOff)
                .setContentTitle(circuitName)
                .setContentText(eventKind + " " + "starts in" + " " + timeOff + "!!")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeImageCode))
                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/raw/notification_in"))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .build();


        //erase the notification from record
        SQLiteDatabase f1Database = context.openOrCreateDatabase(MainActivity.DATABASE_NAME, Context.MODE_PRIVATE, null);

        try{
            f1Database.execSQL("delete from notifications_table where notif_id = " + notificationID + ";");

        }
        catch (SQLiteException sql){
            Log.e("NotifBroadSqliteExc", sql.getMessage());
        }

        NotificationManager notiMan = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notiMan.notify(notificationID, notif);


    }


}
