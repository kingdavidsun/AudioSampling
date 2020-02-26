package com.david.sampling;

import android.Manifest;
import android.media.AudioFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.david.sampling.util.AudioSampler;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    AudioSampler mAudioSampler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        mAudioSampler = new AudioSampler.Builder()
                .sampleRate(44100)
                .channel(AudioFormat.CHANNEL_IN_MONO)
                .format(AudioFormat.ENCODING_PCM_16BIT)
                .create();
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        }
    }

    public void startRecord(View view) {
        String pcmFilePath = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/mytest.pcm";
        mAudioSampler.startRecord(pcmFilePath);
    }

    public void stopRecord(View view) {
        mAudioSampler.stopRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAudioSampler.release();
    }

    public void encodeWAV(View view) {
        String pcmFilePath = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/mytest.pcm";
        String wavFilePath = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/mytest.wav";
        File pcmFile = new File(pcmFilePath);
        File wavFile = new File(wavFilePath);
        if (pcmFile.exists()) {
            mAudioSampler.pcm2Wav(pcmFile, wavFile);
        } else {
            Toast.makeText(this, "要转换的文件不在", Toast.LENGTH_SHORT).show();
        }


    }

    public void encodeMp3(View view) {
        String pcmFilePath = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/mytest.pcm";
        String mp3FilePath = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/mytest.mp3";
        File pcmFile = new File(pcmFilePath);
        File mp3File = new File(mp3FilePath);

        if (pcmFile.exists()) {
            mAudioSampler.pcm2Mp3(pcmFile, mp3File);
        } else {
            Toast.makeText(this, "要转换的文件不在", Toast.LENGTH_SHORT).show();
        }


    }
}
