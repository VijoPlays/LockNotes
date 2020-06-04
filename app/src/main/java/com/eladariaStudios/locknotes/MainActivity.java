package com.eladariaStudios.locknotes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * This app allows you to open OneNote and set a reminder on your lock screen, if your short term memory is as good as mine.
 *
 * @version 1.2
 * @author Alexander "Vijo" Ott
 * @license GNU GPLv3
 * @contact twitter.com/vijoplays
 * @youtube youtube.com/c/vijoplays
 */
public class MainActivity extends AppCompatActivity {
    private final String channelID = "personal_notifications";
    private String notification = "";
    private final int vijoGreenInInt = Color.rgb(0, 255, 160);
    private int nightMode = 0;
    private EditText reminderText;
    private boolean autoRemind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }   //TODO: Add clear button to clear editbox
        //TODO: Clear notifications if editbox is empty
        //TODO: Dark Mode

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        autoRemind = sharedPreferences.getBoolean("autoRemind", false);
        Switch switch_autoReminder = findViewById(R.id.switch_autoReminder);
        switch_autoReminder.setChecked(autoRemind);
        notification = sharedPreferences.getString("notification", "");
        reminderText = findViewById(R.id.reminderText);
        reminderText.setText(notification);
        nightMode = sharedPreferences.getInt("nightMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(autoRemind){
            newReminder(findViewById(android.R.id.content).getRootView());
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("notification", notification);
            editor.apply();
        }
    }

    public void openOneNote(View view){
        String appPackage = "com.microsoft.office.onenote";
        Intent intent = getPackageManager().getLaunchIntentForPackage(appPackage);

        if(intent != null){
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "OneNote does not exist on this device, bromigo. Please download it from the store to use this feature.", Toast.LENGTH_LONG).show();
        }
    }
    //TODO: Add Dark Mode slider
    public void changeDarkMode(View view){
        nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("nightMode", nightMode);
        editor.apply();
    }


    public void changeAutoRemind(View view){
        autoRemind = !autoRemind;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("autoRemind", autoRemind);
        editor.apply();
    }

    // Currently not in use
    public void newNote(View view){
    //    Intent intent = new Intent(MainActivity.this, SecondNoteActivity.class);
    //    startActivity(intent);
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    /**
     * Saves the current text in the edit box and displays it as a pop up notification.
     */
    public void newReminder(View view){
        setNotification(String.valueOf(reminderText.getText()));

        createNotificationChannel();
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("LockNotes");
        bigTextStyle.bigText(notification);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        builder.setContentTitle("LockNotes");
        builder.setContentText(notification);
        builder.setStyle(bigTextStyle);
        builder.setSmallIcon(R.drawable.ic_locknotes_small);
        builder.setColor(vijoGreenInInt);

        if(!notification.equals("")){
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(false); //If you want the notification to disappear after clicking it, set this to true

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(1, builder.build());
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("notification", notification);
        editor.apply();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = "Personal Notifications";
            String description = "Include all personal notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(channelID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }
}
