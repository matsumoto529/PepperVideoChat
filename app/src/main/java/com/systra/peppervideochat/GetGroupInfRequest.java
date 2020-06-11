package com.systra.peppervideochat;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetGroupInfRequest extends AsyncTask<Uri.Builder, Void, String[][]> {
    private static com.systra.peppervideochat.ChoiceActivity ChoiceActivity;

    private Boolean flag = false; // ログインしているユーザーが1人以上いるかどうかの有無

    private String email; // メールアドレス保持用
    private String pass; // パスワード保持用

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    public GetGroupInfRequest(ChoiceActivity activity){
        this.ChoiceActivity = activity;
    }

    public void add(String _email, String _pass) {
        email = _email;
        pass = _pass;
    }

    @Override
    protected String[][] doInBackground(Uri.Builder... builder) {
        String requestTokenUrl =  "https://windfield.work/api/login?email=" + email + "&password=" + pass;
        URL tokenUrl;
        HttpsURLConnection tokenUrlConnection;
        BufferedReader tokenBr;

        int userCount;
        String[][] user = new String[2][];

        try {
            tokenUrl = new URL(requestTokenUrl);
            tokenUrlConnection = (HttpsURLConnection) tokenUrl.openConnection();
            tokenUrlConnection.setRequestMethod("POST");
            tokenUrlConnection.connect();
            tokenBr = new BufferedReader(new InputStreamReader(tokenUrlConnection.getInputStream()));
            String tokenLine;
            StringBuffer tokenSb = new StringBuffer();
            while ((tokenLine = tokenBr.readLine()) != null) {
                if (tokenLine != null){ tokenSb.append(tokenLine); }
            }
            String tokenJsonText = tokenSb.toString();
            JSONObject tokenParentJO = new JSONObject(tokenJsonText);
            String accessToken = tokenParentJO.getString("access_token");
            tokenBr.close();

            URL url = new URL("https://windfield.work/api/group");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("GET");
            conn.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                if (line != null) { sb.append(line); }
            }
            String jsonText = sb.toString();
            JSONArray data = new JSONArray(jsonText);

            userCount = data.length();
            user = new String[2][userCount];

            // 表示アカウント名とPeerIDを取り出す
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                user[0][i] = jsonObject.getString("display_name");
                user[1][i] = jsonObject.getString("caller_peer_id");
                System.out.println("eeeeeeeeeeeeeeeeee_user[0][i]_ " + i + " : " + user[0][i]);
                System.out.println("eeeeeeeeeeeeeeeeee_user[1][i]_ " + i + " : " + user[1][i]);
            }

//            // 試し用データ
//            user[0][2] = "3人目";
//            user[1][2] = "cneuwhewcn";
//            user[0][3] = "4人目";
//            user[1][3] = "jkfandlknda";
//            user[0][4] = "5人目";
//            user[1][4] = "cwrcnrwcvd";
//
//            System.out.println("eeeeeeeeeeeeeeeeee_user[0][2]_ " + user[0][2]);
//            System.out.println("eeeeeeeeeeeeeeeeee_user[1][2]_ " + user[1][2]);
//            System.out.println("eeeeeeeeeeeeeeeeee_user[0][2]_ " + user[0][3]);
//            System.out.println("eeeeeeeeeeeeeeeeee_user[1][2]_ " + user[1][3]);
//            System.out.println("eeeeeeeeeeeeeeeeee_user[0][2]_ " + user[0][4]);
//            System.out.println("eeeeeeeeeeeeeeeeee_user[1][2]_ " + user[1][4]);

            // オンラインのユーザーが0人の時
            if (data.length() == 0) {
                flag = true;
            }
        } catch (MalformedURLException e)  {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    //
    // オンライン状態の人数に合わせてボタンを作成
    //
    @Override
    protected void onPostExecute(String[][] result){
        TextView tvDisplaySentence_1 = ChoiceActivity.findViewById(R.id.tvDisplaySentence_1);
        TextView tvDisplaySentence_2 = ChoiceActivity.findViewById(R.id.tvDisplaySentence_2);

        recyclerView = ChoiceActivity.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(ChoiceActivity);
        recyclerView.setLayoutManager(layoutManager);

        String[] disName = new String[result[0].length];
        String[] peerId = new String[result[0].length];
        System.out.println("eeeeeeeeeeeeeeeeee_result[0].length_ " + result[0].length);

        // オンラインのユーザーが0人の時の処理
        if (flag == true) {
            tvDisplaySentence_1.setText("対応者が不在です。");
            tvDisplaySentence_2.setText("お近くの受付カウンターから受付を行ってください。");
        }
        int count;
        for (count = 0; count < result[0].length; count++) {
            System.out.println("eeeeeeeeeeeeeeeeee_result[0][i]_ " + count + " : " + result[0][count]);
            System.out.println("eeeeeeeeeeeeeeeeee_result[1][i]_ " + count + " : " + result[1][count]);
            disName[count] = result[0][count];
            peerId[count] = result[1][count];
            if (disName[count] == null) {
                break;
            }
        }
        mAdapter = new MyAdapter(disName, peerId, email, pass, count);
        recyclerView.setAdapter(mAdapter);
    }

    public static void AppFinish(){
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee________________________ ");
        Boolean flag = false;
        ChoiceActivity.automaticTransition(flag);
        ChoiceActivity.finish();
    }
}
