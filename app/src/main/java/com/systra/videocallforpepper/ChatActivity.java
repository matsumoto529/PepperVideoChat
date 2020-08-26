package com.systra.videocallforpepper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.IOException;

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
    // ログインしているかの有無
    //true=ログイン状態、false=ログアウト状態
    private Boolean flag = true;
    // 呼び出ししているかの有無
    // true=呼出中、false=待機中
    private Boolean callFlag = false;
    // 呼出時の発信音の有無
    // true=呼出中、false=待機中
    private Boolean incomingFlag = false;
    // remoteStreamの受け取りの有無
    // true=、false=
    private Boolean answeredFlag = false;

    //
    // SkyWay関連の設定
    //
    private static final String API_KEY = "1949a178-d084-4566-830a-83c6d070dcfb"; // Pepperビデオ通話用のSkyWayのAPIキー
    private static final String DOMAIN = "windfield.work"; // Pepperビデオ通話用のSkyWayに登録している利用可能なドメイン
    private Peer _peer; // PeerID
    private MediaStream _localStream; // PC側の通話設定
    private MediaStream _remoteStream; // Pepper側の通話設定
    private MediaConnection _mediaConnection;

    private boolean _bConnected; // CALLボタンの設定
    private String peerId; // Pepper側のPeerID
    private String peerIdPc; // PC側のPeerID

    private String email; // メールアドレス保持用
    private String pass; // パスワード保持用
    private int volume; // 音量保持用

    private AudioManager mAudioManager;

    MediaPlayer mediaPlayer = new MediaPlayer();

    int callVol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        QiSDK.register(this, this);
        System.out.println("eeeeeeeeeeeee_【start】_onCreate>>>>>");

        // データ取得
        Intent getIntent = getIntent();
        flag = getIntent.getBooleanExtra("FLAG", true);
        peerIdPc = getIntent.getStringExtra("PEERID");
        email = getIntent.getStringExtra("EMAIL");
        pass = getIntent.getStringExtra("PASS");
        volume = getIntent.getIntExtra("VOL", 0);

        // 「HOME」ボタンの表示
        Button btHomeAction = findViewById(R.id.btHomeAction);
        btHomeAction.setBackground(ContextCompat.getDrawable(this, R.drawable.home));
        btHomeAction.setEnabled(false);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                btHomeAction.setEnabled(true);
            }
        }, 2500);

        // 音量調整
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 呼出中の発信音の音量(5が適正)
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
        // 発信音、通話音の音量
        callVol = volume + 2;
        // 通話の音量
        if (volume == 0) {
            callVol = 0;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, callVol, AudioManager.FLAG_SHOW_UI);

        String filePath = "incomingSound.mp3";
        try {
            AssetFileDescriptor afDescriptor = getAssets().openFd(filePath);
            mediaPlayer.setDataSource(afDescriptor.getFileDescriptor(), afDescriptor.getStartOffset(), afDescriptor.getLength());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // グレーアウト用のカバーの設定
        ImageView ivFrame = (ImageView) findViewById(R.id.viewCover);
        ivFrame.setAlpha(0);

        // gifの設定
        ImageView imageView = (ImageView) findViewById(R.id.gifView);
        imageView.setAlpha(0);
        GlideDrawableImageViewTarget target = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.raw.calling).into(target);

        // Windowタイトルの非表示
        Window window = getWindow();
        window.addFlags(Window.FEATURE_NO_TITLE);

        // UI handlerの設定
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
            peerId = object.toString();
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
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeee_受信_");
            if (!(object instanceof MediaConnection)) {
                return;
            }
            _mediaConnection = (MediaConnection) object;
            setMediaCallBack();
            _mediaConnection.answer(_localStream);

            _bConnected = true;
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

        // 「HOME」ボタンの処理
        btHomeAction.setOnClickListener(v -> {
            String PeerID = peerIdPc;
            onPeerSelected(PeerID);
            callFlag = false;
            // 通話を切る処理
            closeRemoteStream();
            _mediaConnection.close();
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            flag = true;
            intent.putExtra("FLAG", flag);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASS", pass);
            intent.putExtra("VOL", volume);
            finish();
            startActivity(intent);
        });
        System.out.println("eeeeeeeeeeeee_【END  】_onCreate<<<<<");
    }

    public void callStart() {
        System.out.println("eeeeeeeeeeeee_【start】_callStart>>>>>");
        ImageView imageView = findViewById(R.id.gifView);
        //
        // GUI event Listenerの設定
        //
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!_bConnected && !callFlag) {
                    //
                    // 呼出中
                    //
                    String PeerID = peerIdPc;
                    onPeerSelected(PeerID);

                    // 発信音
                    incoming(true);
                    // 通話待機中の処理
                    callFlag = true;
                    imageView.setAlpha(255);

                    ImageView ivFrame = (ImageView) findViewById(R.id.viewCover);
                    ivFrame.setAlpha(255);

                    // 通話待機中、30秒間応答がない場合はユーザー選択画面に戻りトースト表示
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!callFlag){
                            } else {
                                incomingFlag = false;
                                incoming(incomingFlag);
                                callFlag = false;
                                flag = true;
                                new AlertDialog.Builder(ChatActivity.this)
                                        .setTitle("")
                                        .setMessage("対応者が不在だったため切断しました。")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(ChatActivity.this, ChoiceActivity.class);
                                                intent.putExtra("FLAG", flag);
                                                intent.putExtra("EMAIL", email);
                                                intent.putExtra("PASS", pass);
                                                intent.putExtra("VOL", volume);
                                                finish();
                                                startActivity(intent);
                                            }
                                        }).show();
                            }
                        }
                    }, 30000);
                }
                else if (_bConnected && !callFlag){
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
                    // 発信音
                    incomingFlag = false;
                    incoming(incomingFlag);
                    // 通話を切る処理
                    closeRemoteStream();
                    _mediaConnection.close();
                }
            }
        }, 2000);
        System.out.println("eeeeeeeeeeeee_【END  】_onCreate<<<<<");
    }

    // 許可ダイアログの承認結果を受け取る(許可・不許可)
    // 不許可を選んだ場合、カメラとマイクの許可を促す
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        System.out.println("eeeeeeeeeeeee_【start】_onRequestPermissionResult>>>>>");
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

    public void incoming(Boolean result) {
        System.out.println("eeeeeeeeeeeee_【start】_incoming>>>>>");
        incomingFlag = result;
//        int _callVol;
            if (incomingFlag == true){
                if (volume == 0) {
                    callVol = 0;
                }
                // (前の時は9)
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, callVol, AudioManager.FLAG_SHOW_UI);
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            } else if (incomingFlag == false){
                mediaPlayer.stop();
            }
    }

    @Override
    protected void onStart() {
        System.out.println("eeeeeeeeeeeee_【start】_onStart>>>>>");
        super.onStart();
        // スリープと画面ロックを無効にする
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        System.out.println("eeeeeeeeeeeee_【start】_onResume>>>>>");
        super.onResume();
        // ボリュームコントロールストリームタイプをデフォルトに設定
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    @Override
    protected void onPause() {
        System.out.println("eeeeeeeeeeeee_【start】_onPause>>>>>");
        // ボリュームコントロールストリームタイプをデフォルトに設定
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        System.out.println("eeeeeeeeeeeee_【start】_onStop>>>>>");
        // 発信音
        incoming(false);

        super.onStop();
        // スリープと画面ロックを有効にする
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    protected void onDestroy() {
        System.out.println("eeeeeeeeeeeee_【start】_onDestroy>>>>>");
        super.onDestroy();
        QiSDK.unregister(this, this);
        destroyPeer();
    }

    //
    // ローカルのMediaEventsを取得
    //
    void startLocalStream() {
        System.out.println("eeeeeeeeeeeee_【start】_startLocalStream>>>>>");
        Navigator.initialize(_peer); // _peerを初期化
        MediaConstraints constraints = new MediaConstraints();
        _localStream = Navigator.getUserMedia(constraints);

        Canvas canvas = findViewById(R.id.vLocalView);
        _localStream.addVideoRenderer(canvas, 0);
        callStart();
    }

    //
    // MediaConnection.MediaEventのコールバックを設定
    //
    void setMediaCallBack() {
        System.out.println("eeeeeeeeeeeee_【start】_setMediaCallBack>>>>>");
        System.out.println("eeeeeeeeeeeee_answeredFlag_" + answeredFlag);
        if (answeredFlag == false) {
            answeredFlag = true;
            _mediaConnection.on(MediaConnection.MediaEventEnum.STREAM, object -> {
                _remoteStream = (MediaStream) object;
                Canvas canvas = findViewById(R.id.vRemoteView);
                _remoteStream.addVideoRenderer(canvas, 0);
                callFlag = false;
                ImageView ivFrame = (ImageView) findViewById(R.id.viewCover);
                ivFrame.setAlpha(0);
                ImageView imageView = (ImageView) findViewById(R.id.gifView);
                imageView.setAlpha(0);
                // 発信音
                incoming(false);
            });
            _mediaConnection.on(MediaConnection.MediaEventEnum.CLOSE, object -> {
                closeRemoteStream();
                _bConnected = false;
            });
            _mediaConnection.on(MediaConnection.MediaEventEnum.ERROR, object -> {
                PeerError peerError = (PeerError) object;
                Log.d(TAG, "[On/MediaError]" + peerError);
            });
        } else if (answeredFlag == true) {
            answeredFlag = false;
        }
    }

    //
    // オブジェクトをクリーンアップ
    //
    private void destroyPeer() {
        System.out.println("eeeeeeeeeeeee_【start】_destroyPeer>>>>>");
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
        System.out.println("eeeeeeeeeeeee_【start】_unsetPeerCallbacks>>>>>");
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
        System.out.println("eeeeeeeeeeeee_【start】_unsetMediaCallbacks>>>>>");
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
        System.out.println("eeeeeeeeeeeee_【start】_closeRemoteStream>>>>>");
        if (null == _remoteStream) {
            return;
        }
        Canvas canvas = findViewById(R.id.vRemoteView);
        _remoteStream.removeVideoRenderer(canvas, 0);
        _remoteStream.close();
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        flag = true;
        intent.putExtra("FLAG", flag);
        intent.putExtra("EMAIL", email);
        intent.putExtra("PASS", pass);
        intent.putExtra("VOL", volume);
        finish();
        startActivity(intent);
    }

    //
    // MediaConnectionを作成
    //
    void onPeerSelected(String strPeerId) {
        System.out.println("eeeeeeeeeeeee_【start】_onPeerSelected>>>>>");
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
