package com.systra.peppervideochat;

import android.content.Intent;
import android.graphics.Color;
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

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    Boolean flag;
    Boolean logoutFlag;
    public static Boolean finishFlag = false;

    private String email;
    private String pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);

        Intent getIntent = getIntent();
        flag = getIntent.getBooleanExtra("FLAG", false);
        email = getIntent.getStringExtra("EMAIL");
        pass = getIntent.getStringExtra("PASS");
        email = "matsumoto@systra.co.jp";
        pass = "matsusys";

        TextView tvLog = findViewById(R.id.tvLogDisplay);
        ImageButton bt = findViewById(R.id.btNext);

        // 後で消す
        flag = true;

        if (flag == false){
            tvLog.setText("お近くの受付カウンターから受付を行ってください。");
            bt.setEnabled(false);
            bt.setBackground(ContextCompat.getDrawable(this, R.drawable.main_screen_illustration_false));
        }
        if (flag == true){
            tvLog.setText("来客の方は画面をタッチしてください。");
            bt.setEnabled(true);
            bt.setBackground(ContextCompat.getDrawable(this, R.drawable.main_screen_illustration_true));
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (finishFlag){
            finishApp();
        }
    }

    protected void finishApp(){
        finish();
    }

    // オペレーター選択画面に遷移するボタンの処理
    // ログインしていない場合は遷移せず、トーストを表示する。
    public void onButtonClick(View v) {
        // 後で消す
        flag = true;
        if (flag == true){
            Intent intent = new Intent(this, ChoiceActivity.class);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASS", pass);
            startActivity(intent);
        } else if (flag == false){
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
        // 後で消す
        flag = true;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu_list, menu);
        MenuItem setLog = menu.findItem(R.id.menuListOptionLogin);
        if (flag == true) {
            setLog.setTitle("ログアウト");
            logoutFlag = true;

        }
        if (flag == false) {
            setLog.setTitle("ログイン");
            logoutFlag = false;
        }
        return super.onCreateOptionsMenu(menu);
    }

    // オーバーフローメニューの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (logoutFlag == false) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if (logoutFlag == true) {
            logout();
            Toast toast = Toast.makeText(this, "ログアウトしました。", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            View view = toast.getView();
            view.setBackgroundColor(Color.rgb(128, 128, 128));
            toast.show();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
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
//        Say say = SayBuilder.with(qiContext)
//                .withText("こんにちは、Pepperです。")
//                .build();
//        say.async().run();
//        Animation animation = AnimationBuilder.with(qiContext)
//                .withResources(R.raw.raise_right_hand_b001).build();
//        Animate animate = AnimateBuilder.with(qiContext)
//                .withAnimation(animation).build();
//        animate.async().run();
    }

    @Override
    public void onRobotFocusLost() {

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
