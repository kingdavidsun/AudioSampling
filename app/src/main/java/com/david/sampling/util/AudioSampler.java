package com.david.sampling.util;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by David on 2020/2/24
 */
public class AudioSampler {
    private static final String TAG = "AudioSampler";
    private final ExecutorService mExecutorService;
    private AudioRecord mAudioRecord;
    private int mSampleRate;//采样率
    private int mChannel;//声道
    private int mFormat;//录制格式
    private int mBufferSize;//缓存大小
    private String mPcmFilePath;//录制的pcm路径
    private boolean isRecord;//是否录制的标志
    private LameJni lameJni;
    private AudioSampler(Builder builder) {
        mSampleRate=builder.sampleRate;
        mChannel=builder.channel;
        mFormat=builder.format;
        lameJni=new LameJni();
        mExecutorService = Executors.newSingleThreadExecutor();
        mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannel, mFormat)*2;
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                mSampleRate,
                mChannel,//双声道
                //TODO api28以上可以直接录制MP3
                mFormat,
                mBufferSize
        );
    }
    public static class Builder{
        int sampleRate;//采样率
        int channel;//声道数
        int format;//音频格式
        Context context;//上下文
        public Builder(){

        }
        public Builder sampleRate(int sampleRate){
            this.sampleRate=sampleRate;
            return this;
        }
        public Builder channel(int channel){
            this.channel=channel;
            return this;
        }
        public Builder format(int format){
            this.format=format;
            return this;
        }
        public  AudioSampler create(){
            return new AudioSampler(this);
        }
    }

    /**
     * 录制pcm文件
     * @param pcmFilePath pcm文件路径
     */
    public void startRecord(String pcmFilePath) {
        if (isRecord) {
            return;
        }
        this.mPcmFilePath=pcmFilePath;
        isRecord = true;
        RecordThread recordThread = new RecordThread();
        mExecutorService.execute(recordThread);
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        isRecord = false;
    }
    /**
     * 释放资源
     */
    public void release() {
        if (mAudioRecord != null) {
            mAudioRecord.release();
        }
        if (lameJni!=null){
            lameJni.destroy();
        }
        if (mExecutorService!=null){
            mExecutorService.shutdown();
        }
    }

    /**
     * pcm文件转wav文件
     * @param pcmFile pcm文件
     * @param wavFile wav文件
     */
    public void pcm2Wav(File pcmFile,File wavFile) {
        if (!pcmFile.exists()){
            throw new RuntimeException(pcmFile.getAbsolutePath()+",there is no pcm file");
        }
        mExecutorService.execute(new PcmToWavThread(pcmFile,wavFile));
    }

    /**
     * pcm文件转换为mp3文件
     * @param pcmFile pcm文件
     * @param mp3File mp3文件
     */
    public void pcm2Mp3(File pcmFile,File mp3File){
        if (!pcmFile.exists()){
            throw new RuntimeException(pcmFile.getAbsolutePath()+",there is no pcm file");
        }
        mExecutorService.execute(new PcmToMp3Thread(pcmFile,mp3File));
    }

    class PcmToMp3Thread implements Runnable{
        File pcmFile;
        File mp3File;

        public PcmToMp3Thread(File pcmFile, File mp3File) {
            this.pcmFile = pcmFile;
            this.mp3File = mp3File;
        }

        @Override
        public void run() {
            if (!mp3File.exists()){
                try {
                    mp3File.createNewFile();
                    lameJni.pcmTomp3(pcmFile.getAbsolutePath(),mp3File.getAbsolutePath(),mSampleRate/2,mChannel==AudioFormat.CHANNEL_IN_MONO?1:2,128);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    class PcmToWavThread implements Runnable{
        File pcmFile;
        File wavFile;

        public PcmToWavThread(File pcmFile, File wavFile) {
            this.pcmFile = pcmFile;
            this.wavFile = wavFile;
        }

        @Override
        public void run() {
            if (!wavFile.exists()){
                try {
                    wavFile.createNewFile();
                    PcmToWavUtil util=new PcmToWavUtil(mSampleRate,mChannel,mFormat);
                    util.pcmToWav(pcmFile.getAbsolutePath(),wavFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    class RecordThread implements Runnable {

        @Override
        public void run() {
            mAudioRecord.startRecording();
            FileOutputStream fos = null;
            try {
                Log.i(TAG, "文件地址: " + mPcmFilePath);
                fos = new FileOutputStream(mPcmFilePath);
                byte[] bytes = new byte[mBufferSize];
                while (isRecord) {
                    mAudioRecord.read(bytes, 0, bytes.length);
                    fos.write(bytes, 0, bytes.length);
                    fos.flush();
                }
                Log.i(TAG, "停止录制");
                mAudioRecord.stop();
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}
