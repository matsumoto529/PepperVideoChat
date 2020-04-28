package com.systra.peppervideochat;

import android.app.DownloadManager;
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

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    Boolean flag;
    Boolean logoutFlag;

    private String email;
    private String pass;

    private DownloadManager.Request setContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QiSDK.register(this, this);

        Intent getIntent = getIntent();
        flag = getIntent.getBooleanExtra("FLAG", false);
        email = getIntent.getStringExtra("EMAIL");
        pass = getIntent.getStringExtra("PASS");
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_getEmail_" + email);
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_getPass_" + pass);
    }

    // オペレーター選択画面に遷移するボタンの処理
    // ログインしていない場合は遷移せず、トーストを表示する。
    public void onButtonClick(View view) {
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_click_flag_" + flag);
        if (flag == true){
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_clickTrue_flag_" + flag);
            Intent intent = new Intent(this, ChoiceActivity.class);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASS", pass);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_putEmail_" + email);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_putPass_" + pass);
            startActivity(intent);
        } else if (flag == false){
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_clickFalse_flag_" + flag);
            Toast toast = Toast.makeText(this, "ログインしてください。", LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    // オーバーフローメニューの表示
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu_list, menu);
        MenuItem setLog = menu.findItem(R.id.menuListOptionLogin);
//        // 後で消す
//        flag = true;
        if (flag == true) {
            setLog.setTitle("ログアウト");
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_setLog_true_" + setLog);
            logoutFlag = true;

        }
        if (flag == false) {
            setLog.setTitle("ログイン");
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_setLog_false_" + setLog);
            logoutFlag = false;
        }
        return super.onCreateOptionsMenu(menu);
    }

    // オーバーフローメニューの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
//        // 後で消す
//        logoutFlag = true;
        if (logoutFlag == false) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if (logoutFlag == true) {
            logout();
            Toast toast = Toast.makeText(this, "ログアウトしました。", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
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
