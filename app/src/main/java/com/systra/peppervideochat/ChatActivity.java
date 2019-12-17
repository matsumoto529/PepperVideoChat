package com.systra.peppervideochat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;

import org.webrtc.MediaStream;

import io.skyway.Peer.MediaConnection;
import io.skyway.Peer.Peer;
import io.skyway.Peer.PeerOption;


public class ChatActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    //
    // SkyWay関連の設定
    //
    private static final String API_KEY = "APIkey"; // Pepperビデオ通話用のSkyWayのAPIキー
    private static final String DOMAIN = "DOMAIN"; // Pepperビデオ通話用のSkyWayに登録している利用可能なドメイン

    private Peer _peer;
    private MediaStream _localStream; // PC側の通話設定
    private MediaStream _remoteStream; // Pepper側の通話設定
    private MediaConnection _mediaConnection;

    private boolean _bConnected; // CALLボタンの設定
    private Handler _handler; //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        QiSDK.register(this, this);

        // 戻るボタンの作成
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // UI handlerの設定
        _handler = new Handler(Looper.getMainLooper());
        final Activity activity = this;

        // Peerの初期化
        PeerOption peerOption = new PeerOption();
        peerOption.key = API_KEY;
        peerOption.domain = DOMAIN;
        _peer = new Peer(this, peerOption);

        //
        // Peer event callbacksの設定
        //

        // OPEN
        _peer.on(Peer.PeerEventEnum.OPEN, object -> {
            // 許可をリクエスト
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO }, 0);
            } else {
                // localのMediaStreamを取得して表示
                startLocalStream();
            }
        });

        // CALL(電話の着信)
        _peer.on(Peer.PeerEventEnum.CALL, object -> {
            if (!(object instanceof MediaConnection)){
                return;
            }

            _mediaConnection = (MediaConnection) object;
            setMediaCallBack();
        });
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
