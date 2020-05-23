package com.eladariaStudios.locknotes;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This class is (for now) not used. I mostly wanted to toy around a little.
 * If you feel like it, you could add a functionality to save multiple notes, but for my purposes, I only required one note.
 */
public class SecondNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_note); // Haha for whatever reason IntelliJ imported the wrong R class thanks for nothing
    }


}
