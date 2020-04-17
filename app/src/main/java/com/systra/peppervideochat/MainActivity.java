package com.systra.peppervideochat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

import static android.nfc.NfcAdapter.EXTRA_DATA;
import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    Boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);

        Intent intent = getIntent();
        flag = intent.getBooleanExtra(EXTRA_DATA, false);
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_flag_" + flag);
    }

    // オペレーター選択画面に遷移するボタンの処理
    // ログインしていない場合は遷移せず、トーストを表示する。
    public void onButtonClick(View view) {
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_click_flag_" + flag);
        if (flag == true){
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_clickTrue_flag_" + flag);
            Intent intent = new Intent(this, ChoiceActivity.class);
            startActivity(intent);
        } else if (flag == false){
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_clickFalse_flag_" + flag);
            Toast toast = Toast.makeText(this, "ログインしてください", LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    // オーバーフローメニューの表示
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // オーバーフローメニューの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
