package com.acutecoder.voicedictionary;

/*
 *Created by Bhuvaneshwaran
 *on 11:23 PM, 12/5/2022
 *AcuteCoder
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.acutecoder.voicedictionary.task.BackgroundTask;
import com.acutecoder.voicedictionary.task.InstallDictionaryTask;
import com.acutecoder.voicedictionary.view.LicenseView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ImageView search;
    private FloatingActionButton recognizeVoice;
    private EditText word;
    private TextView wordText;
    private TextView noun;
    private TextView verb;
    private TextView adj;
    private TextView adv;
    private TextView nounMeaning;
    private TextView verbMeaning;
    private TextView adjMeaning;
    private TextView advMeaning;
    private DBHelper helper;
    private WordSet result;
    private Speech voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.search);
        recognizeVoice = findViewById(R.id.recognizeVoice);
        word = findViewById(R.id.word);
        wordText = findViewById(R.id.wordText);
        noun = findViewById(R.id.noun);
        verb = findViewById(R.id.verb);
        adj = findViewById(R.id.adj);
        adv = findViewById(R.id.adv);
        nounMeaning = findViewById(R.id.nounMeaning);
        verbMeaning = findViewById(R.id.verbMeaning);
        adjMeaning = findViewById(R.id.adjMeaning);
        advMeaning = findViewById(R.id.advMeaning);
        helper = new DBHelper(this);
        voice = new Speech(this);

        init();
        changeVisibility();
    }

    private void init() {
        if (!helper.isDatabaseSet()) {
            new AlertDialog.Builder(this, R.style.DialogTheme)
                    .setTitle("Setup Dictionary")
                    .setMessage("Dictionary is not set yet! This process does not require Internet connection. Do you like to continue?")
                    .setPositiveButton("Continue", (d, i) ->
                            new InstallDictionaryTask(helper).install())
                    .setNegativeButton("Exit", (d, i) -> finishAffinity())
                    .setCancelable(false)
                    .create()
                    .show();
        }
        search.setOnClickListener(v -> findWord(word.getText().toString()));
        word.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                findWord(word.getText().toString());
            return actionId == EditorInfo.IME_ACTION_SEARCH;
        });
        voice.setOnActionListener(new Speech.OnActionListener() {
            @Override
            public void onStart() {
                Toast.makeText(MainActivity.this, "Recognizing...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResult(String result) {
                findWord(result.trim());
            }

            @Override
            public void onError(int code) {
                if (code == 9)
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 101);
                if (!voice.isPaused())
                    Toast.makeText(MainActivity.this, "Error in voice recognizer.", Toast.LENGTH_SHORT).show();
            }
        });
        recognizeVoice.setOnClickListener(v -> {
            if (voice.isPaused())
                voice.reInit();
            voice.recognize();
        });
    }

    @SuppressLint("SetTextI18n")
    private void findWord(String word) {
        this.word.setText(word);
        BackgroundTask.execute(() -> result = helper.findWord(word), () -> {
            voice.stop();
            WordMeaning noun = result.getNoun(), verb = result.getVerb(), adj = result.getAdj(), adv = result.getAdv();
            wordText.setText(result.getWord());
            if (noun != null) {
                nounMeaning.setText(noun.getMeaning());
                voice.speak(result.getWord(), "Noun", noun.getMeaning());
            } else
                nounMeaning.setText("-");
            if (verb != null) {
                String text = verb.getMeaning() + "\n\nExample:\n" + verb.getExample();
                verbMeaning.setText(text);
                voice.speak(result.getWord(), "Verb", text);
            } else
                verbMeaning.setText("-");
            if (adj != null) {
                String text = adj.getMeaning() + "\n\nExample:\n" + adj.getExample();
                adjMeaning.setText(text);
                voice.speak(result.getWord(), "Adjective", text);
            } else
                adjMeaning.setText("-");
            if (adv != null) {
                String text = adv.getMeaning() + "\n\nExample:\n" + adv.getExample();
                advMeaning.setText(text);
                voice.speak(result.getWord(), "Adverb", text);
            } else
                advMeaning.setText("-");
            if (allEqual()) {
                wordText.setText("Not found!");
            }
            changeVisibility();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.install:
                new InstallDictionaryTask(helper).install();
                break;
            case R.id.about:
                new AlertDialog.Builder(this, R.style.DialogTheme)
                        .setTitle("About")
                        .setMessage("This project is developed by AcuteCoder as an open source project!")
                        .setPositiveButton("Visit Website", (d, i2) -> {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://acutecoder.ml/about.html"));
                            startActivity(i);
                        })
                        .setNegativeButton("View Source", (d, i2) -> {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://github.com/Bhuvaneshw/VoiceDictionary"));
                            startActivity(i);
                        })
                        .create().show();
                break;
            case R.id.license:
                LicenseView licenseView = new LicenseView(this);
                licenseView.setPadding(0, 40, 0, 0);
                new AlertDialog.Builder(this, R.style.DialogTheme)
                        .setTitle("License")
                        .setPositiveButton("Close", (d, i) -> {
                        })
                        .setView(licenseView)
                        .create()
                        .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        voice.onPause();
    }

    private boolean allEqual() {
        return nounMeaning.getText().toString().equals(verbMeaning.getText().toString())
                && nounMeaning.getText().toString().equals(adjMeaning.getText().toString())
                && nounMeaning.getText().toString().equals(advMeaning.getText().toString());
    }

    private void changeVisibility() {
        nounMeaning.setVisibility(nounMeaning.getText().equals("-") ? View.GONE : View.VISIBLE);
        noun.setVisibility(nounMeaning.getText().equals("-") ? View.GONE : View.VISIBLE);
        verbMeaning.setVisibility(verbMeaning.getText().equals("-") ? View.GONE : View.VISIBLE);
        verb.setVisibility(verbMeaning.getText().equals("-") ? View.GONE : View.VISIBLE);
        adjMeaning.setVisibility(adjMeaning.getText().equals("-") ? View.GONE : View.VISIBLE);
        adj.setVisibility(adjMeaning.getText().equals("-") ? View.GONE : View.VISIBLE);
        advMeaning.setVisibility(advMeaning.getText().equals("-") ? View.GONE : View.VISIBLE);
        adv.setVisibility(advMeaning.getText().equals("-") ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}