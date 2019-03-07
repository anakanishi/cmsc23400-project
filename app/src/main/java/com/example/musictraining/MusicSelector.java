package com.example.musictraining;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MusicSelector extends AppCompatActivity {

    public class StringWithTag {
        public String string;
        public Object tag;

        public StringWithTag(String stringPart, Object tagPart) {
            string = stringPart;
            tag = tagPart;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    private String activityType;
    private ContentResolver cr;
    private SharedPreferences.Editor prefseditor;
    TextView errorText;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* standard activity stuff */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_selector);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* initialize both the error text and spinner to invisible so they dont show up lol */
        errorText = findViewById(R.id.music_error_text);
        errorText.setVisibility(View.INVISIBLE);
        lv = findViewById(R.id.music_items);
        lv.setVisibility(View.INVISIBLE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefseditor = prefs.edit();

        Intent intent = getIntent();
        activityType = intent.getExtras().get("activity").toString();

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1390284982);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique
            return;
        }
        getMusicInfo();
    }

    private void getMusicInfo() {
        cr = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = cr.query(uri, null, null, null, null);
        List<StringWithTag> spinnerData = new ArrayList<StringWithTag>();
        if (cursor == null) {
            // something blew up, set error text
            errorText.setText("Error finding music :(");
            errorText.setVisibility(View.VISIBLE);
        } else if (!cursor.moveToFirst()) {
            // no music, set error text
            errorText.setText("No music on device!");
            errorText.setVisibility(View.VISIBLE);
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            do {
                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                spinnerData.add(new StringWithTag(thisTitle, thisId));
            } while (cursor.moveToNext());
            ArrayAdapter<StringWithTag> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerData);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            lv.setAdapter(adapter);
            lv.setVisibility(View.VISIBLE);
        }
        cursor.close();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getMusicInfo();
        }
    }

}
