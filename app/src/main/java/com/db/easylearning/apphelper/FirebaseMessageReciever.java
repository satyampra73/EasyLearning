package com.db.easylearning.apphelper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.db.easylearning.HomeActivity;
import com.db.easylearning.R;
import com.db.easylearning.voicecalling.SinchService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.SinchHelpers;
import com.sinch.android.rtc.calling.CallNotificationResult;

import java.util.Date;
import java.util.Map;

public class FirebaseMessageReciever extends FirebaseMessagingService {
    private static final String PRIMARY_CHANNEL = "channel_namer";

    public static String CHANNEL_ID = "Speakify Push Notification Channel";
    private static final String TAG = FirebaseMessageReciever.class.getSimpleName();

    private SharedPreferences sharedPreferences;


//    @Override
//    public void onNewToken(@NonNull String token) {
//        super.onNewToken(token);
//        Log.d("newToken",token);
//        sharedPreferences = getSharedPreferences(Preferences.PREFER_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(Preferences.token, token);
//        editor.apply();
//    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            showNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }
        else {
            Map data = remoteMessage.getData();

            // Optional: inspect the payload w/o starting Sinch Client and thus avoiding onIncomingCall()
            // e.g. useful to fetch user related polices (blacklist), resources (to show a picture, etc).
            NotificationResult result = SinchHelpers.queryPushNotificationPayload(getApplicationContext(), data);
            if (result.isValid() && result.isCall()) {
                CallNotificationResult callResult = result.getCallResult();
                Log.d(TAG, "queryPushNotificationPayload() -> display name: " + result.getDisplayName());
                if (callResult != null) {
                    Log.d(TAG, "queryPushNotificationPayload() -> headers: " + result.getCallResult().getHeaders());
                    Log.d(TAG, "queryPushNotificationPayload() -> remote user ID: " + result.getCallResult().getRemoteUserId());
                }
            }

            // Mandatory: forward payload to the SinchClient.
            if (SinchHelpers.isSinchPushPayload(data)) {
                new ServiceConnection() {
                    private Map payload;

                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        if (payload != null) {
                            SinchService.SinchServiceInterface sinchService = (SinchService.SinchServiceInterface) service;
                            if (sinchService != null) {
                                NotificationResult result = sinchService.relayRemotePushNotificationPayload(payload);
                                if (result.isValid() && result.isCall()) {
                                    // Optional: handle result, e.g. show a notification or similar.
                                }
                            }
                        }
                        payload = null;
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {}

                    public void relayMessageData(Map<String, String> data) {
                        payload = data;
                        createNotificationChannel(NotificationManager.IMPORTANCE_MAX);
                        getApplicationContext().bindService(new Intent(getApplicationContext(), SinchService.class), this, BIND_AUTO_CREATE);
                    }
                }.relayMessageData(data);
            }
        }
    }

    // Method to get the custom Design for the display of
    // notification.
    private RemoteViews getCustomDesign(String title,
                                        String message) {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon,
                R.drawable.logo);
        return remoteViews;
    }

    // Method to display the notifications
    public void showNotification(String title,
                                 String message) {
//        // Pass the intent to switch to the MainActivity
//        Intent intent
//                = new Intent(this, HomeActivity.class);
//        // Assign channel ID
//        String channel_id = "notification_channel";
//        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
//        // the activities present in the activity stack,
//        // on the top of the Activity that is to be launched
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        // Pass the intent to PendingIntent to start the
//        // next Activity
////        PendingIntent pendingIntent
////                = PendingIntent.getActivity(
////                this, 0, intent,
////                PendingIntent.FLAG_ONE_SHOT);
//
//        PendingIntent pendingIntent;
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
//                     pendingIntent
//                = PendingIntent.getActivity(
//                this, 0, intent,
//                PendingIntent.FLAG_IMMUTABLE);
//        }
//        else {
//             pendingIntent
//                    = PendingIntent.getActivity(
//                    this, 0, intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//        }
//
//        // Create a Builder object using NotificationCompat
//        // class. This will allow control over all the flags
//        NotificationCompat.Builder builder
//                = new NotificationCompat
//                .Builder(getApplicationContext(),
//                channel_id)
//                .setSmallIcon(R.drawable.logo)
//                .setAutoCancel(true)
//                .setVibrate(new long[]{1000, 1000, 1000,
//                        1000, 1000})
//                .setOnlyAlertOnce(true)
//                .setContentIntent(pendingIntent);
//
//        // A customized design for the notification can be
//        // set only for Android versions 4.1 and above. Thus
//        // condition for the same is checked here.
//        builder = builder.setContent(
//                getCustomDesign(title, message));
//        // Create an object of NotificationManager class to
//        // notify the
//        // user of events that happen in the background.
//        NotificationManager notificationManager
//                = (NotificationManager) getSystemService(
//                Context.NOTIFICATION_SERVICE);
//        // Check if the Android Version is greater than Oreo
//        if (Build.VERSION.SDK_INT
//                >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel
//                    = new NotificationChannel(
//                    channel_id, "web_app",
//                    NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(
//                    notificationChannel);
//        }
//
//        notificationManager.notify(0, builder.build());



        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        String channelId = PRIMARY_CHANNEL;
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
//        String channelId = PRIMARY_CHANNEL;
//        String channelName = getString(R.string.default_channel);
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//

        Intent intent = new Intent(this, HomeActivity.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo).setAutoCancel(true)
                .setContentTitle(title);
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
//                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(intent);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
            mBuilder.setContentIntent(resultPendingIntent);
        }
        else {
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
        }


        notificationManager.notify(notificationId, mBuilder.build());
    }

    private void createNotificationChannel(int importance) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sinch";
            String description = "Incoming Sinch Push Notifications.";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
