package com.systra.peppervideochat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

public class ChoiceActivity extends AppCompatActivity implements RobotLifecycleCallbacks {
    private Boolean flag = true;
    private Boolean serifFlag;

    private String email;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        QiSDK.register(this, this);

        Intent getIntent = getIntent();
        flag = getIntent.getBooleanExtra("FLAG", true);
        email = getIntent.getStringExtra("EMAIL");
        pass = getIntent.getStringExtra("PASS");

        // 戻るボタンの作成
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Uri.Builder builder = new Uri.Builder();

        GetGroupInfRequest ggir = new GetGroupInfRequest(this);
        ggir.add(email, pass);
        ggir.execute(builder);

        // 更新ボタンの処理内容の呼び出し
        Button updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(this::reload);
    }

    // 戻るボタンの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            Intent intent = new Intent(ChoiceActivity.this, MainActivity.class);
            flag = true;
            intent.putExtra("FLAG", flag);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASS", pass);
            finish();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    // 更新ボタンの処理内容
    public void reload(View view) {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0,0);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QiSDK.unregister(this, this);
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
//        System.out.println("eeeeeeeeeeeeeeeeeeeee_serifFlag_" + serifFlag);
//        if (serifFlag = false) {
//            Say say = SayBuilder.with(qiContext)
//                    .withText("ごめんなさい。対応者が不在です。")
//                    .build();
//            say.async().run();
//        } else {
//            Say say = SayBuilder.with(qiContext)
//                    .withText("対応者を選んでください！")
//                    .build();
//            say.async().run();
//        }
    }

    @Override
    public void onRobotFocusLost() {

    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }
}

