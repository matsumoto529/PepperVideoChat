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

public class LoginActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    Boolean flag = false;

    EditText email;
    EditText pass;

    private String _email;
    private String _pass;

    public void flag(Boolean result) {
        flag = result;
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

        findViewById(R.id.appFinishButton).setOnClickListener(v -> {
            MainActivity.finishFlag = true;
            finish();
        });
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

        Uri.Builder builder = new Uri.Builder();
        GetLoginInfRequest glir = new GetLoginInfRequest(this);
        glir.add(_email, _pass);
        glir.execute(builder);
        // ログイン有無時の処理
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag == true) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("FLAG", flag);
                    intent.putExtra("EMAIL", _email);
                    intent.putExtra("PASS", _pass);
                    finish();
                    startActivity(intent);
                    Toast toast = Toast.makeText(LoginActivity.this, "ログインしました。", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.rgb(128, 128, 128));
                    toast.show();
                }
                if (flag == false) {
                    Toast toast = Toast.makeText(LoginActivity.this, "ログインできませんでした。\nメールアドレス、パスワードが正しいかご確認ください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.rgb(128, 128, 128));
                    toast.show();
                }
            }
        }, 3000);
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
