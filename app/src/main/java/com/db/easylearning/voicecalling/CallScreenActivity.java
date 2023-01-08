package com.db.easylearning.voicecalling;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.db.easylearning.HomeActivity;
import com.db.easylearning.R;
import com.db.easylearning.ReportUserActivity;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallScreenActivity extends BaseActivity {

    static final String TAG = CallScreenActivity.class.getSimpleName();

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId;
    private TextView mCallDuration;
    private TextView mCallState;
    CardView endCallButton;
    ImageView imgSpeakerOn, imgSpeakerOff, imgMute, imgUnmute, imgSpeakerDefault;
    AudioController audioController;
    AudioManager audioManager;
    ImageView imgReportUser;
    String userName, status;
    TextView txtDecline;

    int reportFlag = 0;
    String historyFlag = "1";


    //proximity Sensor

    SensorManager sensorManager;
    Sensor proximitySensor;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;

    static final int REQUEST_CODE = 123;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Yeah, this is hidden field.
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());
        setContentView(R.layout.speakify_callscreen);

        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = findViewById(R.id.callDuration);
        mCallState = findViewById(R.id.callState);
        endCallButton = findViewById(R.id.hangupButton);
        imgSpeakerOff = findViewById(R.id.imgSpeakerOff);
        imgSpeakerOff.setClickable(false);
        imgSpeakerOn = findViewById(R.id.imgSpeakerOn);
        imgSpeakerOn.setClickable(false);
        imgMute = findViewById(R.id.imgMute);
        imgUnmute = findViewById(R.id.imgUnmute);
        imgReportUser = findViewById(R.id.imgReportImage);
        imgReportUser.setVisibility(View.GONE);
        txtDecline = findViewById(R.id.txtDecline);
        imgSpeakerDefault = findViewById(R.id.imgSpeakerDefault);

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMicrophoneMute(false);


        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        historyFlag = getIntent().getStringExtra("calling");
        Log.d("planH", "History flag: " + historyFlag);


        // calling sensor service.
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // from sensor service we are
        // calling proximity sensor
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


        // handling the case if the proximity
        // sensor is not present in users device.
        if (proximitySensor == null) {
            Toast.makeText(this, "No proximity sensor found in device.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // registering our sensor with sensor manager.
            sensorManager.registerListener(proximitySensorEventListener,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        addListeners();

    }


    // calling the sensor event class to detect
    // the change in data when sensor starts working.
    SensorEventListener proximitySensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // method to check accuracy changed in sensor.
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // check if the sensor type is proximity sensor.
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0) {
                    // here we are setting our status to our textview..
                    // if sensor event return 0 then object is closed
                    // to sensor else object is away from sensor.
                    if (!wakeLock.isHeld()) {
                        wakeLock.acquire();
                    }
                } else {

                    if (wakeLock.isHeld()) {
                        wakeLock.release();
                    }
                }
            }
        }
    };

    private void addListeners() {

        imgSpeakerOn.setOnClickListener(v -> {
            imgSpeakerOff.setVisibility(View.VISIBLE);
            imgSpeakerOn.setVisibility(View.GONE);
            audioController.enableAutomaticAudioRouting(true, AudioController.UseSpeakerphone.SPEAKERPHONE_AUTO);
        });

        imgMute.setOnClickListener(v -> {
            imgUnmute.setVisibility(View.VISIBLE);
            imgMute.setVisibility(View.GONE);
            audioManager.setMicrophoneMute(true);

        });

        imgUnmute.setOnClickListener(v -> {
            imgMute.setVisibility(View.VISIBLE);
            imgUnmute.setVisibility(View.GONE);

            audioManager.setMicrophoneMute(false);


        });

        imgSpeakerOn.setOnClickListener(v -> {
            imgSpeakerOff.setVisibility(View.VISIBLE);
            imgSpeakerOn.setVisibility(View.GONE);
            audioController.enableAutomaticAudioRouting(true, AudioController.UseSpeakerphone.SPEAKERPHONE_AUTO);
        });

        imgSpeakerOff.setOnClickListener(v -> {
            imgSpeakerOff.setVisibility(View.GONE);
            imgSpeakerOn.setVisibility(View.VISIBLE);
            audioController.disableSpeaker();
        });

        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });


        imgReportUser.setOnClickListener(v -> {
            reportFlag = 1;
            endCall();

        });
    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            mCallState.setText(call.getState().toString());
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDurationTask.cancel();
        mTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private void endCall() {
        String type = "0";
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }

        if (reportFlag == 1) {
            Intent intent = new Intent(CallScreenActivity.this, ReportUserActivity.class);
            intent.putExtra("callingUser", userName);
            finish();
            startActivity(intent);
        } else {

            audioManager.setMicrophoneMute(false);
            assert call != null;
            if (historyFlag != null) {

                if (String.valueOf(call.getDetails().getEndCause()).equals("NO_ANSWER")) {
                    type = "1";
                } else if (String.valueOf(call.getDetails().getEndCause()).equals("HUNG_UP")) {
                    type = "2";
                } else if (String.valueOf(call.getDetails().getEndCause()).equals("DENIED")) {
                    type = "3";
                } else if (String.valueOf(call.getDetails().getEndCause()).equals("CANCELED")) {
                    type = "4";
                }
                else {
                    type="0";
                }
                Bundle bundle = new Bundle();

                bundle.putString("to_id", call.getRemoteUserId());
                bundle.putString("status", String.valueOf(call.getDetails().getEndCause()));
                bundle.putString("type", type);
                Intent intent = new Intent(CallScreenActivity.this, HomeActivity.class);
                intent.putExtras(bundle);
                finish();
                startActivity(intent);
            } else {
                Intent intent = new Intent(CallScreenActivity.this, HomeActivity.class);
                finish();
                startActivity(intent);
            }
        }

    }

    private String formatTimespan(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            mCallDuration.setText(formatTimespan(call.getDetails().getDuration()));
        }
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d("planCall", "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopRingtone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().getEndCause();

            Log.d("planCall", String.valueOf(call.getDetails().getEndCause()));
            if (String.valueOf(call.getDetails().getEndCause()).equals("NO_ANSWER")) {
                Toast.makeText(CallScreenActivity.this, "Please call again , That users has not answered your call.", Toast.LENGTH_LONG).show();
            } else if (String.valueOf(call.getDetails().getEndCause()).equals("DENIED")) {
                Toast.makeText(CallScreenActivity.this, "Please call again , That user has declined your call.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CallScreenActivity.this, "Call ended", Toast.LENGTH_LONG).show();
            }

            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {

            imgSpeakerDefault.setVisibility(View.GONE);
            imgSpeakerOn.setVisibility(View.VISIBLE);
            imgReportUser.setVisibility(View.VISIBLE);
            imgSpeakerOn.setClickable(true);
            imgSpeakerOff.setClickable(true);
            txtDecline.setText("End");
            Log.d(TAG, "Call established");
            mAudioPlayer.stopRingtone();
            mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            audioController = getSinchServiceInterface().getAudioController();
            audioController.disableSpeaker();
            //    audioController.enableAutomaticAudioRouting(true, AudioController.UseSpeakerphone.SPEAKERPHONE_AUTO);
            userName = call.getRemoteUserId();
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d("planCall", "Call progressing" + call.getDetails());
            mAudioPlayer.playRingtoneForCalling();
        }

    }
}
