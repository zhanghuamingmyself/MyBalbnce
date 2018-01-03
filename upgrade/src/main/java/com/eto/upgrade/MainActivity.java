package com.eto.upgrade;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.eto.upgrade.utils.InstallApk;

import java.io.File;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(new File("/system/bin/su").exists()||new File("/system/xbin/su").exists()){

        }else {
            boolean isAccess = isAccessibilitySettingsOn("com.eto.upgrade.MyAccessibilityService",MainActivity.this);
            if(isAccess ==false){
                openAccessibility("com.eto.upgrade.MyAccessibilityService",MainActivity.this);
            }
            Intent intent = new Intent(MainActivity.this, UpdateService.class);
            startService(intent);
        }

        btn = (Button)findViewById(R.id.btn_test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName= Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS+"/app-debug.apk";
                InstallApk.openFile(MainActivity.this, new File(fileName));
            }
        });
        finish();
    }

    private boolean isAccessibilitySettingsOn(String accessibilityServiceName, Context context) {
        int accessibilityEnable = 0;
        String serviceName = context.getPackageName() + "/" + accessibilityServiceName;
        try {
            accessibilityEnable = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        } catch (Exception e) {
            Log.e(TAG, "get accessibility enable failed, the err:" + e.getMessage());
        }
        if (accessibilityEnable == 1) {
            TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.d(TAG, "Accessibility service disable");
        }
        return false;
    }

    /**
     * 跳转到系统设置页面开启辅助功能
     *
     * @param accessibilityServiceName：指定辅助服务名字
     * @param context：上下文
     */
    private void openAccessibility(String accessibilityServiceName, Context context) {
        if (!isAccessibilitySettingsOn(accessibilityServiceName, context)) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }
    }
}
