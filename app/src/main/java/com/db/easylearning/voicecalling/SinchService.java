package com.db.easylearning.voicecalling;

import static com.db.easylearning.apphelper.FirebaseMessageReciever.CHANNEL_ID;
import static com.db.easylearning.voicecalling.IncomingCallScreenActivity.ACTION_ANSWER;
import static com.db.easylearning.voicecalling.IncomingCallScreenActivity.ACTION_IGNORE;
import static com.db.easylearning.voicecalling.IncomingCallScreenActivity.EXTRA_ID;
import static com.db.easylearning.voicecalling.IncomingCallScreenActivity.MESSAGE_ID;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.db.easylearning.R;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Internals;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.NotificationResult;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

import java.util.List;
import java.util.Map;

public class SinchService extends Service {

//    static final String APP_KEY = "b99b92fc-650f-41c4-9fb1-8dac598eafe7";
//    static final String APP_SECRET = "+nP0IEJFAEirnSiZKpHtzA==";
//    static final String ENVIRONMENT = "ocra.api.sinch.com";

    //old keys are replaced with new cause of not working


    public static void setAppKey(String appKey) {
        APP_KEY = appKey;
    }

    public static void setAppSecret(String appSecret) {
        APP_SECRET = appSecret;
    }

    public static  String APP_KEY = "";
    public static  String APP_SECRET = "";
    public static final String ENVIRONMENT = "ocra.api.sinch.com";


    public static final int MESSAGE_PERMISSIONS_NEEDED = 1;
    public static final String REQUIRED_PERMISSION = "REQUIRED_PERMISSION";
    public static final String MESSENGER = "MESSENGER";
    private Messenger messenger;

    public static final String CALL_ID = "CALL_ID";
    static final String TAG = SinchService.class.getSimpleName();

    private SinchServiceInterface mSinchServiceInterface = new SinchServiceInterface();
    private SinchClient mSinchClient;
    private String mUserId;

    private StartFailedListener mListener;
    private PersistedSettings mSettings;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SeceretKeys","App key and app seceret: "+ APP_KEY+APP_SECRET);
        mSettings = new PersistedSettings(getApplicationContext());
        attemptAutoStart();
    }

    private void attemptAutoStart() {
        if (messenger != null) {
            start();
        }
    }

    private void createClient(String username) {
        mUserId = username;
        mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(username)
                .applicationKey(APP_KEY)
                .environmentHost(ENVIRONMENT).build();

        mSinchClient.setSupportManagedPush(true);

        mSinchClient.addSinchClientListener(new MySinchClientListener());
        mSinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        mSinchClient.setPushNotificationDisplayName("User " + username);
    }

    @Override
    public void onDestroy() {
        if (mSinchClient != null && mSinchClient.isStarted()) {
            mSinchClient.terminateGracefully();
        }
        super.onDestroy();
    }

    private boolean hasUsername() {
        if (mSettings.getUsername().isEmpty()) {
            Log.e(TAG, "Can't start a SinchClient as no username is available!");
            return false;
        }
        return true;
    }

    private void createClientIfNecessary() {
        if (mSinchClient != null)
            return;
        if (!hasUsername()) {
            throw new IllegalStateException("Can't create a SinchClient as no username is available!");
        }
        createClient(mSettings.getUsername());
    }

    private void start() {
        boolean permissionsGranted = true;
        createClientIfNecessary();
        try {
            //mandatory checks
            mSinchClient.checkManifest();
        } catch (MissingPermissionException e) {
            permissionsGranted = false;
            if (messenger != null) {
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString(REQUIRED_PERMISSION, e.getRequiredPermission());
                message.setData(bundle);
                message.what = MESSAGE_PERMISSIONS_NEEDED;
                try {
                    messenger.send(message);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (permissionsGranted) {
            Log.d(TAG, "Starting SinchClient");
            try {
                mSinchClient.start();
            } catch (IllegalStateException e) {
                Log.w(TAG, "Can't start SinchClient - " + e.getMessage());
            }
        }
    }

    private void stop() {
        if (mSinchClient != null) {
            mSinchClient.terminateGracefully();
            mSinchClient = null;
        }
    }

    private boolean isStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    @Override
    public IBinder onBind(Intent intent) {
        messenger = intent.getParcelableExtra(MESSENGER);
        return mSinchServiceInterface;
    }

    public class SinchServiceInterface extends Binder {

        public Call callUser(String userId) {
            return mSinchClient.getCallClient().callUser(userId);
        }

        public String getUsername() { return mSettings.getUsername(); }

        public void setUsername(String username) { mSettings.setUsername(username);}

        public void retryStartAfterPermissionGranted() { SinchService.this.attemptAutoStart(); }

        public boolean isStarted() {
            return SinchService.this.isStarted();
        }

        public void startClient() { start(); }

        public void stopClient() {
            stop();
        }

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

        public Call getCall(String callId) {
            return mSinchClient != null ? mSinchClient.getCallClient().getCall(callId) : null;
        }

        public AudioController getAudioController() {
            if (!isStarted()) {
                return null;
            }
            return mSinchClient.getAudioController();
        }

        public NotificationResult relayRemotePushNotificationPayload(final Map payload) {
            if (!hasUsername()) {
                Log.e(TAG, "Unable to relay the push notification!");
                return null;
            }
            createClientIfNecessary();
            return mSinchClient.relayRemotePushNotificationPayload(payload);
        }
    }

    public interface StartFailedListener {

        void onFailed(SinchError error);

        void onStarted();
    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientFailed(SinchClient client, SinchError error) {
            if (mListener != null) {
                mListener.onFailed(error);
            }
            Internals.terminateForcefully(mSinchClient);
            mSinchClient = null;
        }

        @Override
        public void onClientStarted(SinchClient client) {
            Log.d(TAG, "SinchClient started");
            if (mListener != null) {
                mListener.onStarted();
            }
        }

        @Override
        public void onLogMessage(int level, String area, String message) {
            switch (level) {
                case Log.DEBUG:
                    Log.d(area, message);
                    break;
                case Log.ERROR:
                    Log.e(area, message);
                    break;
                case Log.INFO:
                    Log.i(area, message);
                    break;
                case Log.VERBOSE:
                    Log.v(area, message);
                    break;
                case Log.WARN:
                    Log.w(area, message);
                    break;
            }
        }

        @Override
        public void onCredentialsRequired(ClientRegistration clientRegistration) {
            // Normally, should not be asked again, since the user / push token is registered via UserController,
            // but you have to provide valid registration here anyway, in case previous registration
            // is somehow compromised.
            clientRegistration.register(JWT.create(APP_KEY, APP_SECRET, mUserId));
        }

        @Override
        public void onUserRegistered() {
            Log.d(TAG, "User registered.");
        }

        @Override
        public void onUserRegistrationFailed(SinchError sinchError) {
            Log.e(TAG, "User registration failed: " + sinchError.getMessage());
        }

        @Override
        public void onPushTokenRegistered() {
            Log.d(TAG, "Push token registered.");
        }

        @Override
        public void onPushTokenRegistrationFailed(SinchError sinchError) {
            Log.e(TAG, "Push token registration failed." + sinchError.getMessage());
        }
    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Log.d(TAG, "onIncomingCall: " + call.getCallId());
            Intent intent = new Intent(SinchService.this, IncomingCallScreenActivity.class);
            intent.putExtra(EXTRA_ID, MESSAGE_ID);
            intent.putExtra(CALL_ID, call.getCallId());
            boolean inForeground = isAppOnForeground(getApplicationContext());
            if (!inForeground) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !inForeground) {
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(MESSAGE_ID, createIncomingCallNotification(call.getRemoteUserId(), intent));
            } else {
                SinchService.this.startActivity(intent);
            }
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
        private Bitmap getBitmap(Context context, int resId) {

            Drawable d = context.getResources().getDrawable(resId);
            Bitmap b = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            d.setBounds(0, 0, 64, 64);
            d.draw(c);
            return b;
        }

        private PendingIntent getPendingIntent(Intent intent, String action) {
            intent.setAction(action);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }

        @TargetApi(29)
        private Notification createIncomingCallNotification(String userId, Intent fullScreenIntent) {
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 112, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setContentTitle("Incoming call")
                            .setContentText(userId)
                            .setLargeIcon(getBitmap(getApplicationContext(), R.drawable.call_pressed))
                            .setSmallIcon(R.drawable.call_pressed)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setContentIntent(pendingIntent)
                            .setFullScreenIntent(pendingIntent, true)
                            .addAction(R.drawable.button_accept, "Answer",  getPendingIntent(fullScreenIntent, ACTION_ANSWER))
                            .addAction(R.drawable.button_decline, "Ignore", getPendingIntent(fullScreenIntent, ACTION_IGNORE))
                            .setOngoing(true);
            return builder.build();
        }
    }


    private class PersistedSettings {

        private SharedPreferences mStore;

        private static final String PREF_KEY = "Sinch";

        public PersistedSettings(Context context) {
            mStore = context.getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        }

        public String getUsername() {
            return mStore.getString("Username", "");
        }

        public void setUsername(String username) {
            SharedPreferences.Editor editor = mStore.edit();
            editor.putString("Username", username);
            editor.commit();
        }
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