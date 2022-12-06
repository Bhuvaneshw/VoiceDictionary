package com.acutecoder.voicedictionary.task;

/*
 *Created by Bhuvaneshwaran
 *on 11:07 PM, 12/5/2022
 *AcuteCoder
 */

import android.widget.Toast;

import com.acutecoder.voicedictionary.DBHelper;
import com.acutecoder.voicedictionary.R;
import com.acutecoder.voicedictionary.view.ProgressDialog;

import java.text.DecimalFormat;

public class InstallDictionaryTask {

    private final DBHelper helper;
    private final ProgressDialog dialog;
    private boolean completed;

    public InstallDictionaryTask(DBHelper helper) {
        this.helper = helper;
        dialog = new ProgressDialog(helper.getContext(), R.style.DialogTheme);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Setting up dictionary...");
    }

    public void install() {
        dialog.show();
        dialog.setProgress(0);
        BackgroundTask.execute(() -> {
            try {
                completed = false;
                helper.addAllWords((progress, read, total) -> {
                    dialog.setProgress((int) progress);
                    dialog.setProgressDetail(readableFileSize(read) + "/" + readableFileSize(total));
                });
            } catch (Exception e) {
                BackgroundTask.onMainThread(() -> Toast.makeText(helper.getContext(), e.toString(), Toast.LENGTH_SHORT).show());
            }
            completed = true;
        }, () -> {
            if (completed)
                Toast.makeText(helper.getContext(), "Dictionary is ready!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    // From StackOverflow :-)
    private String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
