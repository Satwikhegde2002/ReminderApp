package com.example.remainderapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String TAG = "ReminderReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String activity = intent.getStringExtra("activity");

        Log.d(TAG, "Reminder received, activity: " + activity);

        if (activity != null) {
            Toast.makeText(context, "Time for: " + activity, Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, "Error: Activity is null");
            Toast.makeText(context, "Error: Activity is null", Toast.LENGTH_SHORT).show();
        }

        // Play the chime sound if available
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.chime_sound);
            if (mediaPlayer != null) {
                mediaPlayer.start();
                Log.d(TAG, "Chime sound started");
            } else {
                Log.e(TAG, "Chime sound not found");
                Toast.makeText(context, "Chime sound not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error playing chime sound", e);
        }
    }
}


