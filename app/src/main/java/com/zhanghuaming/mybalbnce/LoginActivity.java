package com.zhanghuaming.mybalbnce;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhanghuaming.mybalbnce.bean.LoginBack;
import com.zhanghuaming.mybalbnce.bean.MyAppInfo;
import com.zhanghuaming.mybalbnce.http.RetrofixHelper;
import com.zhanghuaming.mybalbnce.utils.LocalHelper;
import com.zhanghuaming.mybalbnce.utils.MD5Util;
import com.zhanghuaming.mybalbnce.utils.MySharedPreferences;
import com.zhanghuaming.mybalbnce.utils.UpdateApk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by zhang on 2017/12/18.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText etUsername, etPassword;
    private Button btnLogin ,btnClose ,btnUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        etUsername.setText("13715645207");
        etPassword.setText("gdfvb");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                PhoneInfoUtils phoneInfoUtils = new PhoneInfoUtils(LoginActivity.this);
//                Toast.makeText(LoginActivity.this,"手机号码"+phoneInfoUtils.getPhoneInfo(),Toast.LENGTH_SHORT).show();
                MySharedPreferences.save(LoginActivity.this, MySharedPreferences.DevCode, etUsername.getText().toString());
                MySharedPreferences.save(LoginActivity.this, MySharedPreferences.PASSWORD, etPassword.getText().toString());
                Intent i = new Intent(LoginActivity.this,SettingActivity.class);
                startActivity(i);
                finish();

            }
        });
        btnClose = (Button)findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        btnUpdate = (Button)findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstUpgradeApk();
            }
        });
      //  test();
    }
    void firstUpgradeApk() {
        int apk_res_id = R.raw.upgrade_apk;
        MyAppInfo info = UpdateApk.getAppInfo_ByPackageName(LoginActivity.this, "com.eto.upgrade");
        if (info == null) {
            install(apk_res_id);
        }

    }

    void install(int apk_res_id) {
        String path = UpdateApk.getRawFile(LoginActivity.this, apk_res_id);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        startActivity(intent);
    }


    void test(){
        int apk_res_id = R.raw.upgrade_apk;
        String _dirName = Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOWNLOADS;
        File target = new File(_dirName+"/upgrade_apk");
        InputStream is = null;
        FileOutputStream fos = null;
        try{
            //////////////////////////////////////////////////////////////////////
            // rId  R.raw.aa
            is = getResources().openRawResource(apk_res_id);
            fos = new FileOutputStream(target);
            byte[] b = new byte[1024*4];
            int len =0;
            while ((len = is.read(b)) >0){
                fos.write(b, 0, len);
            }
            FileInputStream fileInputStream = new FileInputStream( target.getAbsolutePath());
            String s = MD5Util.fileMD5(fileInputStream);
            Log.e(TAG,"md5Test--------"+s);
//            exec("chmod 777 "+target.getAbsolutePath());
        }
        catch (Exception e){

            e.printStackTrace();
        }finally
        {
            try {
                if(is != null) {
                    is.close();
                }
                if(fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}