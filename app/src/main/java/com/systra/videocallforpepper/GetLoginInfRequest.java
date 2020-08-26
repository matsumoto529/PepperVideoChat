package com.systra.videocallforpepper;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetLoginInfRequest extends AsyncTask<Uri.Builder, Void, Boolean> {

    private final com.systra.videocallforpepper.LoginActivity LoginActivity;

    private String email; // リクエスト用メールアドレス
    private String pass; // リクエスト用パスワード

    Boolean flag = true; // ログインの有無

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
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParcelFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            flag = false;
        } finally {
            return flag;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        LoginActivity la = LoginActivity;
        la.flag(result);
    }
}
