package com.systra.peppervideochat;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class AsyncHttpsRequest extends AsyncTask<Uri.Builder, Void, String> {
    private final com.systra.peppervideochat.ChoiceActivity ChoiceActivity;
    private String email = "matsumoto@systra.co.jp";
    private String pass = "matsusys";

    private String displayName = null;

    public AsyncHttpsRequest(ChoiceActivity activity){
        this.ChoiceActivity = activity;
    }

    @Override
    protected String doInBackground(Uri.Builder... builder){
        String requestTokenUrl = "https://windfield.work/api/login?email=" + email + "&password=" + pass;
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_a_" + requestTokenUrl);
        URL tokenUrl = null;
        HttpsURLConnection tokenUrlConnection = null;
        BufferedReader tokenBr = null;



        try {
            //
            // URLオブジェクトを生成(トークン用)
            //
            tokenUrl = new URL(requestTokenUrl);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_a_" + tokenUrl);
            tokenUrlConnection = (HttpsURLConnection) tokenUrl.openConnection();
            tokenUrlConnection.setRequestMethod("POST");
            tokenUrlConnection.connect();
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_b_" + tokenUrlConnection);

            tokenBr = new  BufferedReader(new InputStreamReader(tokenUrlConnection.getInputStream()));
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_c_" + tokenBr);

            String tokenLine;
            StringBuffer tokenSb = new StringBuffer();

            // lineにトークンを含んだ配列を入れる
            while ((tokenLine = tokenBr.readLine()) != null){
                if (tokenLine != null){ tokenSb.append(tokenLine); }
            }
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_d_" + tokenSb);
            String tokenJsonText = tokenSb.toString();
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_e_" + tokenJsonText);

            // 全体のJSONObjectを取る
            JSONObject tokenParentJO = new JSONObject(tokenJsonText);
            // access_tokenのデータを取り出す
            String accessToken = tokenParentJO.getString("access_token");
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_f_" + accessToken);
            tokenBr.close();

            //
            // URLオブジェクトを生成
            //
            URL url = new URL("https://windfield.work/api/me");
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_1_" + url);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("GET");
            conn.connect();
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_2_" + conn);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_3_" + br);

            String line;
            StringBuffer sb = new StringBuffer();

            // lineに配列を入れる
            while ((line = br.readLine()) != null){
                if (line != null){ sb.append(line); }
            }
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_4_" + sb);
            String jsonText = sb.toString();
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_5_" + jsonText);
            // 全体のJSONObjectを受け取る
            JSONObject parentJsonObject = new JSONObject(jsonText);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_6_" + parentJsonObject);
            // データを取り出す
            displayName = parentJsonObject.getString("display_name");
            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_7_" + displayName);
        return displayName;
    }

    @Override
    protected void onPostExecute(String result){
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_12_" + result);
        TextView tv = ChoiceActivity.findViewById(R.id.tvDisplayName_1);
        tv.setText(result);
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_12_a_" + tv);
    }
}
