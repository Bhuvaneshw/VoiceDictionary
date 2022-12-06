package com.acutecoder.voicedictionary.view;

/*
 *Created by Bhuvaneshwaran
 *on 11:23 PM, 12/6/2022
 *AcuteCoder
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class LicenseView extends LinearLayout {

    public LicenseView(Context context) {
        super(context);
        init();
    }

    public LicenseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LicenseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        WebView web = new WebView(getContext());
        addView(web, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        web.loadUrl("file:///android_asset/license/index.html");
    }

}
