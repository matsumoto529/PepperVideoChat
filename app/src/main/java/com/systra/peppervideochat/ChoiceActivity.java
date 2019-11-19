package com.systra.peppervideochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ChoiceActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private String[] name = new String[3];

    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        QiSDK.register(this, this);

        // ユーザーがサインインしているかどうか確認し、それに応じてUIを更新する。
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        // RecyclerViewのサイズを維持し続ける。
        recyclerView.setHasFixedSize(true);
        // linear layout managerを使用する。
        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rvLayoutManager);

        //
        // APIにリクエストする。
        //
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
