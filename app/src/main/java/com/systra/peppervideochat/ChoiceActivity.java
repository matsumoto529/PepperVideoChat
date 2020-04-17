package com.systra.peppervideochat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

public class ChoiceActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private String[] Name = new String[3];

    private RecyclerView recyclerView;
    private RecyclerView.Adapter rvAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private String email = "matsumoto@systra.co.jp";
    private String pass = "matsusys";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        QiSDK.register(this, this);

        // 戻るボタンの作成
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Uri.Builder builder = new Uri.Builder();

        // 以下は実装予定
//        AsyncHttpsRequest task = new AsyncHttpsRequest(this);
//        task.execute(builder);
//        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_9_" + task);

        GetGroupInfRequest ggir = new GetGroupInfRequest(this);
        ggir.add(email, pass);
        ggir.execute(builder);

        // 以下は実装しない可能性大
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                AsyncHttpsRequest asyncHttpsRequest = new AsyncHttpsRequest(ChoiceActivity.this);
//                System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_8_" + asyncHttpsRequest);
//            }
//        }).start();

//        StringBuffer nameSb = new StringBuffer();
//            if (task != null){ nameSb.append(task); }
//        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_10_" + task);
//        String nameText = nameSb.toString();
//        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_e_" + nameText);



//        List<String> fields = new ArrayList<>();
//        ResultSetMetaData rsmd = (ResultSetMetaData) task;
//        try {
//            for (int i = 0; i <= rsmd.getColumnCount(); i++){
//
//                    fields.add(rsmd.getColumnName(i));
//                    System.out.println(fields);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        recyclerView = findViewById(R.id.recycler_view);
//        int i = 0;
//        for (String field : fields){
//            System.out.println(field + " : " + task);
//            Name[i] = String.format(Locale.ENGLISH, field, i);
//            i++;
//            RecyclerView.Adapter rvAdapter = new RecyclerViewActivity(Name);
//            recyclerView.setAdapter(rvAdapter);
//        }

//        // ユーザーがサインインしているかどうか確認し、それに応じてUIを更新する。
//        RecyclerView recyclerView = findViewById(R.id.recycler_view);
//        // RecyclerViewのサイズを維持し続ける。
//        recyclerView.setHasFixedSize(true);
//        // linear layout managerを使用する。
//        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(rvLayoutManager);


        // 更新ボタンの処理内容の呼び出し
        Button updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(this::reload);
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

    // 更新ボタンの処理内容
    public void reload(View view) {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0,0);
        startActivity(intent);
    }
}

