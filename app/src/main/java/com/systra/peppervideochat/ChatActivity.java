package com.systra.peppervideochat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;

import io.skyway.Peer.Browser.Canvas;
import io.skyway.Peer.Browser.MediaConstraints;
import io.skyway.Peer.Browser.MediaStream;
import io.skyway.Peer.Browser.Navigator;
import io.skyway.Peer.CallOption;
import io.skyway.Peer.MediaConnection;
import io.skyway.Peer.Peer;
import io.skyway.Peer.PeerError;
import io.skyway.Peer.PeerOption;

public class ChatActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private Boolean flag = true;

    //
    // SkyWay関連の設定
    //
    private static final String API_KEY = "1949a178-d084-4566-830a-83c6d070dcfb"; // Pepperビデオ通話用のSkyWayのAPIキー
    private static final String DOMAIN = "windfield.work"; // Pepperビデオ通話用のSkyWayに登録している利用可能なドメイン

    private Peer _peer;
    private MediaStream _localStream; // PC側の通話設定
    private MediaStream _remoteStream; // Pepper側の通話設定
    private MediaConnection _mediaConnection;

    private boolean _bConnected; // CALLボタンの設定
    private Handler _handler; //
    private String peerId; // Pepper側のPeerID

    private String peerIdPc; // PC側のPeerID
    private String email;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QiSDK.register(this, this);

        Intent getIntent = getIntent();
        flag = getIntent.getBooleanExtra("FLAG", true);
        peerIdPc = getIntent.getStringExtra("PEERID");
        email = getIntent.getStringExtra("EMAIL");
        pass = getIntent.getStringExtra("PASS");
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_getPeerId_" + peerIdPc);
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_getEmail_" + email);
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_getPass_" + pass);

        // 戻るボタンの作成
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        // Windowタイトルの非表示
        Window window = getWindow();
        window.addFlags(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);

        // UI handlerの設定
        _handler = new Handler(Looper.getMainLooper());
        final Activity activity = this;

        // Peerの初期化
        PeerOption peerOption = new PeerOption();
        peerOption.key = API_KEY;
        peerOption.domain = DOMAIN;
        _peer = new Peer(this, peerOption);
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_peerOp_" + peerOption);

        //
        // Peer event callbacksの設定
        //

        // OPEN
        _peer.on(Peer.PeerEventEnum.OPEN, object -> {
            // 許可をリクエスト
            peerId = object.toString();
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_object_" + object);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_peerId_" + peerId);
            // PeerIDの登録
            Uri.Builder builder = new Uri.Builder();
            PeerIdRegister pir = new PeerIdRegister(this);
            pir.add(email, pass, peerId);
            pir.execute(builder);

            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 0);
            } else {
                // localのMediaStreamを取得して表示
                startLocalStream();
            }
        });

        // CALL(電話の着信)
        _peer.on(Peer.PeerEventEnum.CALL, object -> {
//            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_peerIdCall_" + peerId);
            if (!(object instanceof MediaConnection)) {
                return;
            }
            _mediaConnection = (MediaConnection) object;
            setMediaCallBack();
            _mediaConnection.answer(_localStream);

            _bConnected = true;
            updateActionButtonTitle();
        });

        // CLOSE(切断)
        _peer.on(Peer.PeerEventEnum.CLOSE, object -> Log.d(TAG, "[On/Close]"));
        // DISCONNECTED(接続できない)
        _peer.on(Peer.PeerEventEnum.DISCONNECTED, object -> Log.d(TAG, "[On/Disconnected]"));
        // ERROR(エラー)
        _peer.on(Peer.PeerEventEnum.ERROR, object -> {
            PeerError error = (PeerError) object;
            Log.d(TAG, "[On/Error]" + error.message);
        });

        //
        // GUI event Listenerの設定
        //
        Button btCallAction = findViewById(R.id.btCallAction);
        btCallAction.setEnabled(true);
        btCallAction.setOnClickListener(v -> {
            v.setEnabled(false);
            if (!_bConnected) {
                Toast toast = Toast.makeText(this, "呼び出しています。\nしばらくお待ちください。", Toast.LENGTH_SHORT); // テキスト内容は変更する
                toast.setGravity(Gravity.CENTER, 0, 0);
                View view = toast.getView();
                view.setBackgroundColor(Color.rgb(128, 128, 128));
                toast.show();
//                Intent intent = getIntent();
//                System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_intent_" + intent);
                String PeerID = peerIdPc;
//                String PeerID = intent.getStringExtra("PeerID");
                System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_PeerID_" + PeerID);
                onPeerSelected(PeerID);
            } else {
                // 電話を切る
                closeRemoteStream();
                _mediaConnection.close();
            }
            v.setEnabled(true);
        });
        Button btBackAction = findViewById(R.id.btBackAction);
        btBackAction.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            flag = true;
            intent.putExtra("FLAG", flag);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASS", pass);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_putEmail_" + email);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_putPass_" + pass);
            startActivity(intent);
        });
    }

    // 許可ダイアログの承認結果を受け取る(許可・不許可)
    // 不許可を選んだ場合、カメラとマイクの許可を促す
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocalStream();
            } else {
                Toast toast = Toast.makeText(this, "カメラとマイクにアクセスできませんでした。\n許可を求められたら、許可をクリックします。", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                View view = toast.getView();
                view.setBackgroundColor(Color.rgb(128, 128, 128));
                toast.show();
            }
        }
    }

    // 戻るボタンの処理
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemId = item.getItemId();
//        if (itemId == android.R.id.home) {
//            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
//            flag = true;
//            intent.putExtra("FLAG", flag);
//            intent.putExtra("EMAIL", email);
//            intent.putExtra("PASS", pass);
//            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_putEmail_" + email);
//            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_putPass_" + pass);
//            startActivity(intent);
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        // スリープと画面ロックを無効にする
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ボリュームコントロールストリームタイプをデフォルトに設定
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    @Override
    protected void onPause() {
        // ボリュームコントロールストリームタイプをデフォルトに設定
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // スリープと画面ロックを有効にする
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QiSDK.unregister(this, this);
        destroyPeer();
    }

    //
    // ローカルのMediaEventsを取得して表示
    //
    void startLocalStream() {
        Navigator.initialize(_peer); // _peerを初期化
        MediaConstraints constraints = new MediaConstraints();
        _localStream = Navigator.getUserMedia(constraints);

        Canvas canvas = (Canvas) findViewById(R.id.vLocalView);
        _localStream.addVideoRenderer(canvas, 0);
    }

    //
    // MediaConnection.MediaEventのコールバックを設定
    //
    void setMediaCallBack() {
        _mediaConnection.on(MediaConnection.MediaEventEnum.STREAM, object -> {
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_object_STREAM_" + object);
            _remoteStream = (MediaStream) object;
            Canvas canvas = findViewById(R.id.vRemoteView);
            _remoteStream.addVideoRenderer(canvas, 0);
        });
        _mediaConnection.on(MediaConnection.MediaEventEnum.CLOSE, object -> {
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_object_CLOSE_" + object);
            closeRemoteStream();
            _bConnected = false;
            updateActionButtonTitle();
        });
        _mediaConnection.on(MediaConnection.MediaEventEnum.ERROR, object -> {
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeee_object_ERROR_" + object);
            PeerError peerError = (PeerError) object;
            Log.d(TAG, "[On/MediaError]" + peerError);
        });
    }

    //
    // オブジェクトをクリーンアップ
    //
    private void destroyPeer() {
        closeRemoteStream();
        if (null != _localStream) {
            Canvas canvas = findViewById(R.id.vLocalView);
            _localStream.removeVideoRenderer(canvas, 0);
            _localStream.close();
        }
        if (null != _mediaConnection) {
            if (_mediaConnection.isOpen()) {
                _mediaConnection.close();
            }
            unsetMediaCallbacks();
        }
        Navigator.terminate();
        if (null != _peer) {
            unsetPeerCallbacks(_peer);
            if (!_peer.isDisconnected()) {
                _peer.disconnect();
            }
            if (!_peer.isDestroyed()) {
                _peer.destroy();
            }
            _peer = null;
        }
    }

    //
    // PeerEventsのコールバックの設定を解除
    //
    void unsetPeerCallbacks(Peer peer) {
        if (null == _peer) {
            return;
        }
        peer.on(Peer.PeerEventEnum.OPEN, null);
        peer.on(Peer.PeerEventEnum.CONNECTION, null);
        peer.on(Peer.PeerEventEnum.CALL, null);
        peer.on(Peer.PeerEventEnum.CLOSE, null);
        peer.on(Peer.PeerEventEnum.DISCONNECTED, null);
        peer.on(Peer.PeerEventEnum.ERROR, null);
    }

    //
    // MediaConnection.MediaEventsのコールバックの設定を解除
    //
    void unsetMediaCallbacks() {
        if (null == _mediaConnection) {
            return;
        }
        _mediaConnection.on(MediaConnection.MediaEventEnum.STREAM, null);
        _mediaConnection.on(MediaConnection.MediaEventEnum.CLOSE, null);
        _mediaConnection.on(MediaConnection.MediaEventEnum.ERROR, null);
    }

    //
    // リモートのMediaStreamを閉じる
    //
    void closeRemoteStream() {
        if (null == _remoteStream) {
            return;
        }
        Canvas canvas = findViewById(R.id.vRemoteView);
        _remoteStream.removeVideoRenderer(canvas, 0);
        _remoteStream.close();
        Toast toast = Toast.makeText(this, "キャンセルされました。", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = toast.getView();
        view.setBackgroundColor(Color.rgb(128, 128, 128));
        toast.show();
    }

    //
    // MediaConnectionを作成
    //
    void onPeerSelected(String strPeerId) {
        if (null == _peer) {
            return;
        }
        if (null != _mediaConnection) {
            _mediaConnection.close();
        }
        CallOption callOption = new CallOption();
        _mediaConnection = _peer.call(strPeerId, _localStream, callOption);
        if (null != _mediaConnection) {
            setMediaCallBack();
            _bConnected = true;
        }
        updateActionButtonTitle();
    }

    //
    // CALLボタンを更新
    //
    @SuppressLint("SetTextI18n")
    void updateActionButtonTitle() {
        _handler.post(() -> {
            Button btTextCallChange = findViewById(R.id.btCallAction);
            if (null != btTextCallChange) {
                if (!_bConnected) {
                    btTextCallChange.setText("CALL");
                } else {
                    btTextCallChange.setText("END");
                }
            }
        });
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
