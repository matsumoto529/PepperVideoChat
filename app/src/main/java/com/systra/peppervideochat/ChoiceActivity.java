package com.systra.peppervideochat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChoiceActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private static String[] name = new String[3];

    Connection connection = null;
    Statement statement = null;
    static ResultSet resultSet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        QiSDK.register(this, this);

        // 戻るボタンの作成
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        // ユーザーがサインインしているかどうか確認し、それに応じてUIを更新する。
//        RecyclerView recyclerView = findViewById(R.id.recycler_view);
//        // RecyclerViewのサイズを維持し続ける。
//        recyclerView.setHasFixedSize(true);
//        // linear layout managerを使用する。
//        RecyclerView.LayoutManager rvLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(rvLayoutManager);

        //
        // WebAPIにリクエストする。
        //
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recycler_view, new PlaceholderFragment())
                    .commit();
        }

        // 更新ボタンの処理
        Button updateButton = findViewById(R.id.update_button);
    }

    public static class PlaceholderFragment extends Fragment{
        private final String uri = "";                                                              // APIのURLを挿入

        public PlaceholderFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.activity_recycler_view, container, false);
            return rootView;
        }

        @Override
        public void onStart(){
            super.onStart();
            AsyncJsonLoader asyncJsonLoader = new AsyncJsonLoader(new AsyncJsonLoader.AsyncCallback() {
                // 実行前
                @Override
                public void preExecute() {
                }
                // 実行後
                @Override
                public void postExecute(JSONObject result) {
                    if (result == null){
                        showLoadError(); // エラーメッセージを表示
                        return;
                    }
                    try {
                        // フィールド一覧を取得
                        List<String> fields = new ArrayList<>();
                        ResultSetMetaData rsmd = resultSet.getMetaData();
                        for (int i = 0; i <= rsmd.getColumnCount(); i++){
                            fields.add(rsmd.getColumnName(i));
                            name[i] = "";                                                           // usersテーブルのdisplay_nameを挿入
                        }
                        // 結果の表示
                        for (String field : fields){
                            int i = 0;
                            System.out.println(field + ":" + resultSet.getString(field));
                            name[i] = String.format(Locale.ENGLISH, field, i);
                            i++;
                            RecyclerView.Adapter rvAdapter = new RecyclerViewActivity(name, i);
                            RecyclerView recyclerView = getActivity().findViewById(R.id.recycler_view);
                            recyclerView.setAdapter(rvAdapter);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (resultSet != null)resultSet.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // 実行中
                @Override
                public void progressUpdate(int progress) {
                }
                // キャンセル
                @Override
                public void cancel() {
                }
            });
            // 処理を実行
            asyncJsonLoader.execute(uri);
        }

        // エラーメッセージ表示の処理
        private void showLoadError(){
            Toast toast = Toast.makeText(getActivity(), "データを取得できませんでした。", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // 戻るボタンの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        if (itemId == android.R.id.home){
            finish();
        }
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
