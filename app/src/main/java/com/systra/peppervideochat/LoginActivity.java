package com.systra.peppervideochat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

import static android.nfc.NfcAdapter.EXTRA_DATA;
import static android.widget.Toast.LENGTH_LONG;

public class LoginActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    Boolean flag = false;

    EditText email;
    EditText pass;

    private String _email;
    private String _pass;

    public void flag(Boolean result) {
        flag = result;
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_mainFlag_result_" + result);
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_mainFlag_flag_" + flag);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        QiSDK.register(this, this);

        // アクティビティ遷移時に自動的にキーボードが立ち上がらないようにする。
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // 戻るボタンの作成
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        email = findViewById(R.id.etMailaddress);
        pass = findViewById(R.id.etPassword);
    }

    // 戻るボタンの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onButtonClick(View view) {
        _email = String.valueOf(email.getText());
        _pass = String.valueOf(pass.getText());
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_main_email_" + _email);
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_main_pass_" + _pass);

        Uri.Builder builder = new Uri.Builder();
        GetLoginInfRequest glir = new GetLoginInfRequest(this);
        glir.add(_email, _pass);
        glir.execute(builder);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag == true) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(EXTRA_DATA, flag);
                    startActivity(intent);
                }
                if (flag == false) {
                    System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_main_flag_" + flag);
                    Toast toast = Toast.makeText(LoginActivity.this, "ログインできませんでした", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.rgb(128, 128, 128));
                    toast.show();
                }
            }
        }, 1000);

//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
    }

    public void execute(Uri.Builder builder) {
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_IOException_builder_" + builder);
        Toast toast = Toast.makeText(this, "ログインできませんでした", LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QiSDK.unregister(this, this);
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {

    }

    @Override
    public void onRobotFocusLost() {

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }
}
