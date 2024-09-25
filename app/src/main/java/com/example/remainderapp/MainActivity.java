package com.example.remainderapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Spinner daySpinner, activitySpinner;
    private TimePicker timePicker;
    private Button setReminderButton;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        daySpinner = findViewById(R.id.day_spinner);
        activitySpinner = findViewById(R.id.activity_spinner);
        timePicker = findViewById(R.id.time_picker);
        setReminderButton = findViewById(R.id.set_reminder_button);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Listener for setting the reminder
        setReminderButton.setOnClickListener(v -> {
            int hour, minute;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hour = timePicker.getHour();
                minute = timePicker.getMinute();
            } else {
                hour = timePicker.getCurrentHour();
                minute = timePicker.getCurrentMinute();
            }

            String selectedDay = daySpinner.getSelectedItem().toString();
            String selectedActivity = activitySpinner.getSelectedItem().toString();

            setReminder(hour, minute, selectedActivity);
        });

        // Check if we can schedule exact alarms in Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkExactAlarmPermission();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void checkExactAlarmPermission() {
        // Check if the app has permission to schedule exact alarms
        if (!alarmManager.canScheduleExactAlarms()) {
            // If not, request the user to enable the permission in system settings
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }

    private void setReminder(int hour, int minute, String activity) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("activity", activity);

        int pendingIntentFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, pendingIntentFlag);

        if (alarmManager != null) {
            try {
                // Schedule the alarm
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(this, "Reminder set for " + activity, Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                Toast.makeText(this, "Exact alarm permission denied", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Error setting alarm", Toast.LENGTH_SHORT).show();
        }
    }
}
