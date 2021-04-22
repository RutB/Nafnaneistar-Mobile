package xyz.nafnaneistar.controller;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import xyz.nafnaneistar.loginactivity.R;

public class NotificationController extends Application {
    public static final String Partner_Notification_Channel = "Partner__Channel";
    public static final String Name_Notification_Channel = "NameApproved__Channel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel partnerChannel = new NotificationChannel(
                    Partner_Notification_Channel,
                    "Vinalisti",
                    NotificationManager.IMPORTANCE_HIGH
            );

            partnerChannel.setDescription("Sér um Vinalistann");

            NotificationChannel nameLikedChannel = new NotificationChannel(
                    Name_Notification_Channel,
                    "Líkað Sameiginlegt Nafn",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            nameLikedChannel.setDescription("Sér um Sameiginleg líkuð nöfn");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(partnerChannel);
            manager.createNotificationChannel(nameLikedChannel);
        }
    }

    public Notification createPartnerNotification(Context context, String message) {
        Notification notification = new NotificationCompat.Builder(context, Partner_Notification_Channel)
                .setSmallIcon(R.drawable.ic_partner)
                .setContentTitle("Ný Tenging á lista")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        return  notification;
    }

    public Notification createApprovedNameNotification(Context context, String message) {
        Notification notification = new NotificationCompat.Builder(context, Name_Notification_Channel)
                .setSmallIcon(R.drawable.ic_heart)
                .setContentTitle("Nýtt sameiginlegt nafn fundið")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        return  notification;
    }
}
