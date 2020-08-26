package com.systra.videocallforpepper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends AppCompatActivity implements RobotLifecycleCallbacks {
    // ログインしているかの有無
    //true=ログイン状態、false=ログアウト状態
    Boolean flag;
    // ログアウトしてるかどうかの有無(上記とは別の役割)
    // true=ログアウト状態、true=ログイン状態
    Boolean logoutFlag;
    // アプリを終了するかどうかの有無
    // true=終了、false=継続
    public static Boolean finishFlag = false;

    private String email; // メールアドレス保持用
    private String pass; // パスワード保持用
    private int volume; // 音量保持用

    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // データ取得
        Intent getIntent = getIntent();
        flag = getIntent.getBooleanExtra("FLAG", false);
        email = getIntent.getStringExtra("EMAIL");
        pass = getIntent.getStringExtra("PASS");
        // 基本は6でOK
        volume = getIntent.getIntExtra("VOL", 3);
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee_mainVol_ " + volume);

        // 変更された音量をセットする(Pepperセリフ用)
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.AUDIOFOCUS_NONE);

        TextView tvLog = findViewById(R.id.tvLogDisplay);
        ImageButton bt = findViewById(R.id.btNext);

        // ログイン有無によるテキストの変更
        // false=ログアウト状態、true=ログイン状態
        if (!flag){
            tvLog.setText("お近くの受付カウンターから受付を行ってください。");
            bt.setEnabled(false);
            bt.setBackground(ContextCompat.getDrawable(this, R.drawable.main_screen_illustration_false_2));
        }
        if (flag){
            tvLog.setText("来客の方は画面をタッチしてください。");
            bt.setEnabled(true);
            bt.setBackground(ContextCompat.getDrawable(this, R.drawable.main_screen_illustration_true_2));
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        // trueの時アプリ終了
        if (finishFlag){
            finishApp();
        }
    }

    // アプリ終了の処理
    protected void finishApp(){
        finish();
    }

    // オペレーター選択画面に遷移するボタンの処理
    // ログインしていない場合は遷移せず、トーストを表示する。
    public void onButtonClick(View v) {
        if (flag){
            Intent intent = new Intent(this, ChoiceActivity.class);
            intent.putExtra("FLAG", flag);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASS", pass);
            intent.putExtra("VOL", volume);
            startActivity(intent);
        } else if (!flag){
            Toast toast = Toast.makeText(this, "ログインしてください。", LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            View view = toast.getView();
            view.setBackgroundColor(Color.rgb(128, 128, 128));
            toast.show();
        }

    }

    // オーバーフローメニューの表示
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu_list, menu);
        MenuItem setLog = menu.findItem(R.id.menuListOptionLogin);
        if (flag) {
            setLog.setTitle("ログアウト");
            logoutFlag = true;

        }
        if (!flag) {
            setLog.setTitle("ログイン");
            logoutFlag = false;
        }
        return super.onCreateOptionsMenu(menu);
    }

    // オーバーフローメニューの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menuListOptionLogin:
                if (!logoutFlag) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("VOL", volume);
                    startActivity(intent);
                }
                if (logoutFlag) {
                    logout();
                    Toast toast = Toast.makeText(this, "ログアウトしました。", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    View view = toast.getView();
                    view.setBackgroundColor(Color.rgb(128, 128, 128));
                    toast.show();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("VOL", volume);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
                break;
            case R.id.menuListOptionSetting:
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("FLAG", flag);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASS", pass);
                intent.putExtra("VOL", volume);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void logout() {
        flag = false;
        logoutFlag = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QiSDK.unregister(this, this);
        finishFlag = false;
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Say say = SayBuilder.with(qiContext)
                .withText("受付は|こちらですよー？")
                .build();
        say.async().run();
        Animation animation = AnimationBuilder.with(qiContext)
                .withResources(R.raw.hello_a009).build();
        Animate animate = AnimateBuilder.with(qiContext)
                .withAnimation(animation).build();
        animate.async().run();
        while (true) {
            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            say.async().run();
            animate.async().run();
        }
    }

    @Override
    public void onRobotFocusLost() {
    }

    @Override
    public void onRobotFocusRefused(String reason) {
    }
}
