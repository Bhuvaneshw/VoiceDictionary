package com.acutecoder.voicedictionary;

/*
 *Created by Bhuvaneshwaran
 *on 11:23 PM, 12/5/2022
 *AcuteCoder
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.acutecoder.voicedictionary.task.BackgroundTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Objects;

public class DBHelper extends SQLiteOpenHelper {

    private final Context context;

    public DBHelper(Context context) {
        super(context, context.getFilesDir() + "/dictionary.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    public void addAllWords(OnProgressUpdate progressUpdate) {
        addAllWords("words/adj.sql", progressUpdate, 1 / 10f, 0);
        addAllWords("words/adv.sql", progressUpdate, 1 / 10f, 10);
        addAllWords("words/verb.sql", progressUpdate, 1 / 10f, 20);
        addAllWords("words/noun.sql", progressUpdate, 7 / 10f, 30);
    }

    public boolean isDatabaseSet() {
        try {
            getReadableDatabase().rawQuery("select * from noun where word='A';", null).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void addAllWords(String fileName, OnProgressUpdate progressUpdate, float scale, long finishedPercent) {
        try {
            AssetFileDescriptor fd = context.getAssets().openFd(fileName);
            long sizeInBytes = fd.getLength();
            long currentBytes = 0;
            long percent = 0;
            String line;

            publishProgress(percent, currentBytes, sizeInBytes, scale, finishedPercent, progressUpdate);
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            while ((line = reader.readLine()) != null) {
                executeSql(line);
                currentBytes += line.length();
                if (progressUpdate != null) {
                    percent = currentBytes * 100 / sizeInBytes;
                    publishProgress(percent, currentBytes, sizeInBytes, scale, finishedPercent, progressUpdate);
                }
            }
            publishProgress(100, currentBytes, sizeInBytes, scale, finishedPercent, progressUpdate);
            Thread.sleep(500);
            reader.close();
        } catch (Exception ignored) {
        }
    }

    private void publishProgress(long percent, long read, long total, float scale, long finishedPercent, OnProgressUpdate progressUpdate) {
        BackgroundTask.onMainThread(() -> progressUpdate.onProgress((int) (percent * scale + finishedPercent), read, total));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public WordSet findWord(String word) {
        word = word.toLowerCase(Locale.ROOT).trim();
        WordMeaning noun = null, verb = null, adj = null, adv = null;

        try {
            Set set = getWordMeaning("select * from noun where lower(word)='" + word + "' limit 1;", false);
            noun = Objects.requireNonNull(set).meaning;
            word = set.crtWord;
        } catch (Exception ignored) {
        }
        try {
            Set set = getWordMeaning("select * from verb where lower(word)='" + word + "' limit 1;", true);
            verb = Objects.requireNonNull(set).meaning;
            word = set.crtWord;
        } catch (Exception ignored) {
        }
        try {
            Set set = getWordMeaning("select * from adj where lower(word)='" + word + "' limit 1;", true);
            adj = Objects.requireNonNull(set).meaning;
            word = set.crtWord;
        } catch (Exception ignored) {
        }
        try {
            Set set = getWordMeaning("select * from adv where lower(word)='" + word + "' limit 1;", true);
            adv = Objects.requireNonNull(set).meaning;
            word = set.crtWord;
        } catch (Exception ignored) {
        }

        return new WordSet(word, noun, verb, adj, adv);
    }

    @SuppressLint("Range")
    @Nullable
    private Set getWordMeaning(String sql, boolean includeExample) {
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        cursor.moveToFirst();
        try {
            String crtWord = cursor.getString(cursor.getColumnIndex("word"));
            String meaning = cursor.getString(cursor.getColumnIndex("definition"));
            String example = "";
            if (includeExample)
                example = cursor.getString(cursor.getColumnIndex("example"));
            if (!cursor.isClosed()) {
                cursor.close();
            }
            return new Set(new WordMeaning(meaning, example), crtWord);
        } catch (Exception e) {
            return null;
        }
    }

    public void executeSql(String sql) {
        getWritableDatabase().execSQL(sql);
    }

    public Context getContext() {
        return context;
    }

    public interface OnProgressUpdate {
        void onProgress(long progress, long read, long total);
    }

    private static class Set {

        private final WordMeaning meaning;
        private final String crtWord;

        public Set(WordMeaning meaning, String crtWord) {
            this.meaning = meaning;
            this.crtWord = crtWord;
        }
    }
}