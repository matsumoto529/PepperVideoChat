package com.systra.peppervideochat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

public class MainActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);
    }

    // オペレーター選択画面に遷移するボタンの処理
    // ログインしていない場合は遷移せず、トーストを表示する。
    public void onButtonClick(View view){
        Intent intent = new Intent(this, ChoiceActivity.class);
        startActivity(intent);
    }

    // オーバーフローメニューの表示
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // オーバーフローメニューの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy(){
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
