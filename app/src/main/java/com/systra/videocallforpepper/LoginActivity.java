package com.systra.videocallforpepper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class LoginActivity extends AppCompatActivity implements RobotLifecycleCallbacks {
    // ログインしているかの有無
    //true=ログイン状態、false=ログアウト状態
    Boolean flag = false;

    EditText email; // メールアドレスの文字列取得に使用
    EditText pass; // パスワードの文字列取得に使用

    private String _email; // メールアドレス保持用
    private String _pass; // パスワード保持用
    private int volume; // 音量保持用

    ImageButton ibLogin;

    private AudioManager audioManager;

    public void flag(Boolean result) {
        flag = result;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        QiSDK.register(this, this);

        // アクティビティ遷移時に自動的にキーボードが立ち上がらないようにする。
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // データ取得
        Intent getIntent = getIntent();
        volume = getIntent.getIntExtra("VOL", 0);

        // 変更された音量をセットする(Pepperセリフ用)
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.AUDIOFOCUS_NONE);

        // 戻るボタンの作成
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ibLogin = findViewById(R.id.btLogin);
        ibLogin.setBackground(ContextCompat.getDrawable(this, R.drawable.login));

        email = findViewById(R.id.etMailaddress);
        pass = findViewById(R.id.etPassword);
    }

    //
    // 戻るボタンの処理
    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //
    // ログイン処理
    //
    public void onButtonClick(View view) {
        _email = String.valueOf(email.getText());
        _pass = String.valueOf(pass.getText());

        ibLogin.setEnabled(false);
        ibLogin.setBackground(ContextCompat.getDrawable(this, R.drawable.login_pushed));
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ibLogin.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.login));
            }
        }, 300);

        // WebAPIリクエスト
        Uri.Builder builder = new Uri.Builder();
        GetLoginInfRequest glir = new GetLoginInfRequest(this);
        glir.add(_email, _pass);
        glir.execute(builder);
        // ログイン有無の処理(ボタンを押されて3秒待機)
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("FLAG", flag);
                    intent.putExtra("EMAIL", _email);
                    intent.putExtra("PASS", _pass);
                    intent.putExtra("VOL", volume);
                    finish();
                    startActivity(intent);
                    Toast toast = Toast.makeText(LoginActivity.this, "ログインしました。", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.rgb(128, 128, 128));
                    toast.show();
                }
                if (!flag) {
                    Toast toast = Toast.makeText(LoginActivity.this, "ログインできませんでした。\nメールアドレス、パスワードが正しいかご確認ください。", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.rgb(128, 128, 128));
                    toast.show();
                }
                ibLogin.setEnabled(true);
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
        Say say = SayBuilder.with(qiContext)
                .withText("メールアドレスとパスワードを入力してください？")
                .build();
        say.async().run();
        Animation animation = AnimationBuilder.with(qiContext)
                .withResources(R.raw.gesture_both_hands_b001).build();
        Animate animate = AnimateBuilder.with(qiContext)
                .withAnimation(animation).build();
        animate.async().run();

    }

    @Override
    public void onRobotFocusLost() {
    }

    @Override
    public void onRobotFocusRefused(String reason) {
    }
}
