package com.acutecoder.voicedictionary.view;

/*
 *Created by Bhuvaneshwaran
 *on 1:43 AM, 12/6/2022
 *AcuteCoder
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.acutecoder.voicedictionary.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.DecimalFormat;

@SuppressWarnings("unused")
public class ProgressDialog extends AlertDialog {

    private final TextView title, progressText, progressDetail;
    private final LinearProgressIndicator progressBar;

    @SuppressLint("InflateParams")
    public ProgressDialog(Context context) {
        this(context, -1);
    }

    public ProgressDialog(Context context, int dialogTheme) {
        super(context, dialogTheme);
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_progress, null);
        setView(view);
        title = view.findViewById(R.id.title);
        progressDetail = view.findViewById(R.id.percentDetail);
        progressText = view.findViewById(R.id.percentText);
        progressBar = view.findViewById(R.id.progressBar);
    }

    @Override
    public void setTitle(int titleId) {
        title.setText(titleId);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title.setText(title);
    }

    @SuppressLint("SetTextI18n")
    public void setProgress(int progress) {
        progressBar.setProgress(progress);
        progressText.setText(progress + "%");
    }

    @SuppressLint("SetTextI18n")
    public void setProgressDetail(String detail) {
        progressDetail.setText(detail);
    }
}
