package com.systra.peppervideochat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    // ログインしているかの有無
    // true=ログイン状態、false=ログアウト状態
    Boolean flag;
    // 音楽の再生と停止
    // true=再生、false=停止
    Boolean musicFlag;

    private String email; // メールアドレス保持用
    private String pass; // パスワード保持用
    private int volume; // 音量保持用

    private int callVol = 0;

    private TextView textView;
    private SeekBar seekBar;
    private ImageButton musicButton;

    private AudioManager audioManager;

    MediaPlayer mediaPlayer = new MediaPlayer();


    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // データ取得
        Intent getIntent = getIntent();
        flag = getIntent.getBooleanExtra("FLAG", false);
        email = getIntent.getStringExtra("EMAIL");
        pass = getIntent.getStringExtra("PASS");
        volume = getIntent.getIntExtra("VOL", 3);


        // 戻るボタンの処理
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            intent.putExtra("FLAG", flag);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASS", pass);
            intent.putExtra("VOL", volume);
            System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee_setVol_ " + volume);
            finish();
            startActivity(intent);
            mediaPlayer.stop();
        });

        // アプリを終了する処理
        findViewById(R.id.appFinishButton).setOnClickListener(v -> {
            MainActivity.finishFlag = true;
            finish();
        });

        //
        // SeekBarの処理
        //
        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.tvVolume);


        // 初期値
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBar.setProgress(volume);
        String stNowVol = String.valueOf(volume);
        textView.setText(stNowVol);

        musicFlag = false;
        music(musicFlag);
        musicButton = findViewById(R.id.musicButton);
        musicButton.setBackground(ContextCompat.getDrawable(this, R.drawable.music_stop));
        musicButton.setOnClickListener(v -> {
            if (musicFlag == false) {
                musicButton.setBackground(ContextCompat.getDrawable(this, R.drawable.music_playback));
                music(true);
            } else if (musicFlag == true) {
                musicButton.setBackground(ContextCompat.getDrawable(this, R.drawable.music_stop));
                volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                music(false);
            }
        });

        // 最大値(デフォルトは6、7くらい)
        seekBar.setMax(7);

        String filePath = "incomingSound.mp3";
        try {
            AssetFileDescriptor afDescriptor = getAssets().openFd(filePath);
            mediaPlayer.setDataSource(afDescriptor.getFileDescriptor(), afDescriptor.getStartOffset(), afDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // ドラッグされた時
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String str = String.format(Locale.US, "%d", progress);
                textView.setText(str);
                volume = progress;
                System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee_onProgressChanged_ " + volume);
                if (progress == 0 && musicFlag == false) {
                } else if (musicFlag == true) {
                    music(true);
                }
            }
            // タッチされた時
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            // リリースされた時
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void music(Boolean _musicFlag) {
        musicFlag = _musicFlag;
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee_music_ " + volume);
        if (musicFlag == true) {
            // 呼出中の発信音の音量(6が適正)
            callVol = volume + 3;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, callVol, AudioManager.FLAG_SHOW_UI);
        } else if (musicFlag == false) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
        }
    }
}