package com.zhanghuaming.mybalbnce;

import android.content.Intent;
import android.os.Bundle;
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
import com.zhanghuaming.mybalbnce.http.RetrofixHelper;
import com.zhanghuaming.mybalbnce.utils.LocalHelper;
import com.zhanghuaming.mybalbnce.utils.MySharedPreferences;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by zhang on 2017/12/18.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText etUsername, etPassword;
    private Button btnLogin ,btnClose;

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
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
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
    }
}