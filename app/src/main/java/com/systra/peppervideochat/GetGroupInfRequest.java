package com.systra.peppervideochat;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private final com.systra.peppervideochat.ChoiceActivity ChoiceActivity;

    private String email;
    private String pass;

    private String peerId_1;
    private String peerId_2;
    private String peerId_3;

    private Boolean btnFlag_1 = false;
    private Boolean btnFlag_2 = false;
    private Boolean btnFlag_3 = false;


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

        String[][] user = new String[3][2];

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
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_data_" + data);

            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                if (i == 0) {
                    user[i][0] = jsonObject.getString("display_name");
                    user[i][1] = jsonObject.getString("caller_peer_id");
                    if (user[i][1] != null){
                        btnFlag_1 = true;
                    }
                } else if (i == 1) {
                    user[i][0] = jsonObject.getString("display_name");
                    user[i][1] = jsonObject.getString("caller_peer_id");
                    if (user[i][1] != null){
                        btnFlag_2 = true;
                    }
                } else if (i == 2) {
                    user[i][0] = jsonObject.getString("display_name");
                    user[i][1] = jsonObject.getString("caller_peer_id");
                    if (user[i][1] != null){
                        btnFlag_3 = true;
                    }
                }
            }

            br.close();
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_user[0]_" + user[0][0] + " : " + user[0][1] + " : " + btnFlag_1);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_user[1]_" + user[1][0] + " : " + user[1][1] + " : " + btnFlag_2);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_user[2]_" + user[2][0] + " : " + user[2][1] + " : " + btnFlag_3);
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

    @Override
    protected void onPostExecute(String[][] result){
        for (int i = 0; i < result.length; i++){
            //
            // ユーザーの表示・非表示
            //
            if (i == 0){
                // ユーザー１
                Button btn1 = ChoiceActivity.findViewById(R.id.tvDisplayName_1);
                if (btnFlag_1 == false){
                    btn1.setVisibility(View.INVISIBLE);
                    btn1.setVisibility(View.GONE);
                    continue;
                } else {
                    peerId_1 = result[i][1];
                    TextView tv = ChoiceActivity.findViewById(R.id.tvDisplayName_1);
                    tv.setText(result[i][0]);
                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_peerId_1_" + peerId_1);
                            Intent intent = new Intent(ChoiceActivity, ChatActivity.class);
                            intent.putExtra("PEERID", peerId_1);
                            intent.putExtra("EMAIL", email);
                            intent.putExtra("PASS", pass);
                            ChoiceActivity.finish();
                            ChoiceActivity.startActivity(intent);
                        }
                    });
                }
            } else if (i == 1){
                // ユーザー２
                Button btn2 = ChoiceActivity.findViewById(R.id.tvDisplayName_2);
                if (btnFlag_2 == false){
                    btn2.setVisibility(View.INVISIBLE);
                    btn2.setVisibility(View.GONE);
                    return;
                } else {
                    peerId_2 = result[i][1];
                    TextView tv = ChoiceActivity.findViewById(R.id.tvDisplayName_2);
                    tv.setText(result[i][0]);
                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_peerId_2_" + peerId_2);
                            Intent intent = new Intent(ChoiceActivity, ChatActivity.class);
                            intent.putExtra("PEERID", peerId_2);
                            intent.putExtra("EMAIL", email);
                            intent.putExtra("PASS", pass);
                            ChoiceActivity.finish();
                            ChoiceActivity.startActivity(intent);
                        }
                    });
                }
            } else if (i == 2){
                // ユーザー３
                Button btn3 = ChoiceActivity.findViewById(R.id.tvDisplayName_3);
                if (btnFlag_3 == false){
                    btn3.setVisibility(View.INVISIBLE);
                    btn3.setVisibility(View.GONE);
                    return;
                } else {
                    peerId_3 = result[i][1];
                    TextView tv = ChoiceActivity.findViewById(R.id.tvDisplayName_3);
                    tv.setText(result[i][0]);
                    btn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_peerId_3_" + peerId_3);
                            Intent intent = new Intent(ChoiceActivity, ChatActivity.class);
                            intent.putExtra("PEERID", peerId_3);
                            intent.putExtra("EMAIL", email);
                            intent.putExtra("PASS", pass);
                            ChoiceActivity.finish();
                            ChoiceActivity.startActivity(intent);
                        }
                    });
                }
            }
        }
    }
}
