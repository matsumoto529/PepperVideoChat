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

public class GetLoginInfRequest extends AsyncTask<Uri.Builder, Void, Boolean> {

    private final com.systra.peppervideochat.LoginActivity LoginActivity;

    private String email;
    private String pass;

    Boolean flag = true;

    public GetLoginInfRequest(LoginActivity loginActivity) {
        this.LoginActivity = loginActivity;
    }

    public void add(String _email, String _pass) {
        email = _email;
        pass = _pass;
    }

    @Override
    protected Boolean doInBackground(Uri.Builder... builder) {
        String requestTokenUrl = "https://windfield.work/api/login?email=" + email + "&password=" + pass;
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_a_" + requestTokenUrl);
        URL tokenUrl = null;
        HttpsURLConnection tokenUrlConnection = null;
        BufferedReader tokenBr = null;

        try {
            tokenUrl = new URL(requestTokenUrl);
            tokenUrlConnection = (HttpsURLConnection) tokenUrl.openConnection();
            tokenUrlConnection.setRequestMethod("POST");
            tokenUrlConnection.connect();
            tokenBr = new BufferedReader(new InputStreamReader(tokenUrlConnection.getInputStream()));
            String tokenLine;
            StringBuffer tokenSb = new StringBuffer();
            while ((tokenLine = tokenBr.readLine()) != null) {
                if (tokenLine != null) { tokenSb.append(tokenLine); }
            }
            tokenBr.close();
            String tokenJsonText = tokenSb.toString();
            JSONObject tokenParentJO = null;
            String accessToken = "";
            try {
                System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_例外処理前_");
                tokenParentJO = new JSONObject(tokenJsonText);
                accessToken = tokenParentJO.getString("access_token");
                System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_accessToken_" + accessToken);
            } catch (JSONException e) {
                System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_例外処理後_");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_MalformedURLException_");
        } catch (ParcelFormatException e) {
            e.printStackTrace();
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_ParcelFormatException_");
        } catch (IOException e) {
            flag = false;
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_IOException_");
        } finally {
            return flag;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_result_flag_" + result);
        LoginActivity la = LoginActivity;
        la.flag(result);
    }
}
