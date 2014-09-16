/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.afeilulu.stone.settings;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.afeilulu.stone.R;
//import com.afeilulu.stone.render.MuzeiRendererFragment;
import com.afeilulu.stone.util.AnimatedMuzeiLogoFragment;
import com.afeilulu.stone.util.LogUtil;

public class AboutActivity extends Activity {
    private static final String TAG = LogUtil.makeLogTag(AboutActivity.class);

    private static final String VERSION_UNAVAILABLE = "N/A";

    private Handler mHandler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.demo_view_container,
//                            MuzeiRendererFragment.createInstance(true, false))
//                    .commit();
//        }

        // Get app version
        PackageManager pm = getPackageManager();
        String packageName = getPackageName();
        String versionName;
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = VERSION_UNAVAILABLE;
        }

        // Build the about body view and append the link to see OSS licenses
        TextView versionView = (TextView) findViewById(R.id.app_version);
        versionView.setText(Html.fromHtml(
                getString(R.string.about_version_template, versionName)));

        TextView aboutBodyView = (TextView) findViewById(R.id.about_body);
        aboutBodyView.setText(Html.fromHtml(getString(R.string.about_body)));
        aboutBodyView.setMovementMethod(new LinkMovementMethod());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        View demoContainerView = findViewById(R.id.demo_view_container);
        demoContainerView.setAlpha(0);
        demoContainerView.animate()
                .alpha(1)
                .setStartDelay(250)
                .setDuration(1000);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimatedMuzeiLogoFragment logoFragment = (AnimatedMuzeiLogoFragment)
                        getFragmentManager().findFragmentById(R.id.animated_logo_fragment);
                logoFragment.start();
            }
        }, 1000);
    }
}
