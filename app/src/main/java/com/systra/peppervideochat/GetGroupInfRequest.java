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

public class GetGroupInfRequest extends AsyncTask<Uri.Builder, Void, String[]> {
    private final com.systra.peppervideochat.ChoiceActivity ChoiceActivity;

    private String email;
    private String pass;

    public GetGroupInfRequest(ChoiceActivity activity){
        this.ChoiceActivity = activity;
    }

    public void add(String _email, String _pass) {
        email = _email;
        pass = _pass;
    }

    @Override
    protected String[] doInBackground(Uri.Builder... builder) {
        String requestTokenUrl =  "https://windfield.work/api/login?email=" + email + "&password=" + pass;
        URL tokenUrl;
        HttpsURLConnection tokenUrlConnection;
        BufferedReader tokenBr;

        String[] displayName = new String[3];

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
                    displayName[i] = jsonObject.getString("display_name");
                } else if (i == 1) {
                    displayName[i] = jsonObject.getString("display_name");
                } else if (i == 2) {
                    displayName[i] = jsonObject.getString("display_name");
                }
            }
            br.close();
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_displayName[0]_" + displayName[0]);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_displayName[1]_" + displayName[1]);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_displayName[2]_" + displayName[2]);
        } catch (MalformedURLException e)  {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return displayName;
    }

    @Override
    protected void onPostExecute(String[] ressult){
        for (int i = 0; i < ressult.length; i++){
            if (i == 0){
                TextView tv = ChoiceActivity.findViewById(R.id.tvDisplayName_1);
                tv.setText(ressult[i]);
                Button btn = ChoiceActivity.findViewById(R.id.tvDisplayName_1);
                btn.setOnClickListener(this::transitionChat);
            } else if (i == 1){
                Button btn = ChoiceActivity.findViewById(R.id.tvDisplayName_2);
                if (ressult[i] == null){
                    btn.setVisibility(View.INVISIBLE);
                    btn.setVisibility(View.GONE);
                    break;
                }
                TextView tv = ChoiceActivity.findViewById(R.id.tvDisplayName_2);
                tv.setText(ressult[i]);
                btn.setOnClickListener(this::transitionChat);
            } else if (i == 2){
                Button btn = ChoiceActivity.findViewById(R.id.tvDisplayName_3);
                if (ressult[i] == null){
                    btn.setVisibility(View.INVISIBLE);
                    btn.setVisibility(View.GONE);
                    break;
                }
                TextView tv = ChoiceActivity.findViewById(R.id.tvDisplayName_3);
                tv.setText(ressult[i]);
                btn.setOnClickListener(this::transitionChat);
            }
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_result_" + ressult[i]);
        }
    }

    public void transitionChat(View view){
        Intent intent = new Intent(ChoiceActivity, ChatActivity.class);
        ChoiceActivity.startActivity(intent);
    }
}
