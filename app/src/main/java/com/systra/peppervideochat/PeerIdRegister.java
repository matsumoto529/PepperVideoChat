package com.systra.peppervideochat;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFormatException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class PeerIdRegister extends AsyncTask<Uri.Builder, Void, String[]> {
    private final com.systra.peppervideochat.ChatActivity ChatActivity;

    private String email;
    private String pass;
    private String peerId;

    public PeerIdRegister(ChatActivity activity) {
        this.ChatActivity = activity;
    }

    public void add(String _email, String _pass, String _peerId) {
        email = _email;
        pass = _pass;
        peerId = _peerId;
    }

    @Override
    protected String[] doInBackground(Uri.Builder... builder) {
        String requestTokenUrl =  "https://windfield.work/api/login?email=" + email + "&password=" + pass;
        URL tokenUrl;
        HttpsURLConnection tokenUrlConnection;
        BufferedReader tokenBr;

        try {
            tokenUrl = new URL(requestTokenUrl);
            tokenUrlConnection = (HttpsURLConnection) tokenUrl.openConnection();
            tokenUrlConnection.setRequestMethod("POST");
            tokenUrlConnection.connect();
            tokenBr = new BufferedReader(new InputStreamReader(tokenUrlConnection.getInputStream()));
            String tokenLine;
            StringBuffer tokenSb = new StringBuffer();
            while ((tokenLine = tokenBr.readLine()) != null) {
                if (tokenLine != null) {
                    tokenSb.append(tokenLine);
                }
            }
            String tokenJsonText = tokenSb.toString();
            JSONObject tokenParentJO = new JSONObject(tokenJsonText);
            String accessToken = tokenParentJO.getString("access_token");
            tokenBr.close();

            URL url = new URL("https://windfield.work/api/store_peer?my_peer_id=" + peerId);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_url_" + url);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("GET");
            conn.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                if (line != null) { sb.append(line); }
            }
            String jsonText = sb.toString();
            JSONObject data = new JSONObject(jsonText);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_data_" + data);

            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParcelFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new String[0];
    }
}
