package com.example.test;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private NotificationManager mManager;
    private Context mContext;
    private static final String CHANNAL_ID="shop_notification_channel";
    private final int NOTIFICATION_ID=0;

    public NotificationHandler(Context context) {
        this.mContext=context;
        this.mManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel();
    }

    private void createChannel(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }

        NotificationChannel channel = new NotificationChannel(CHANNAL_ID, "Bútor WebShop Notification", NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.RED);
        channel.setDescription("Akció a webshopban");

        this.mManager.createNotificationChannel(channel);
    }

    public void send(String message){
        Intent intent=new Intent(mContext, ShopListActivity.class);
        PendingIntent pendingIntet = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext, CHANNAL_ID)
                .setContentTitle("Bútor webshop")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_shopping_cart)
                .setContentIntent(pendingIntet);

        this.mManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void cancel(){
        this.mManager.cancel(NOTIFICATION_ID);
    }
}
