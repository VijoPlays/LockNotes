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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * This app allows you to open OneNote and set a reminder on your lock screen, if your short term memory is as good as mine.
 *
 * @version 1.1
 * @author Alexander "Vijo" Ott
 * @license GNU GPLv3
 * @contact twitter.com/vijoplays
 * @youtube youtube.com/c/vijoplays
 */
public class MainActivity extends AppCompatActivity {
    private final String channelID = "personal_notifications";
    private String notification = "";
    private final int vijoGreenInInt = Color.rgb(0, 255, 160);
    private EditText reminderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        notification = sharedPreferences.getString("notification", "");
        reminderText = findViewById(R.id.reminderText);
        reminderText.setText(notification);
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
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
