package com.david.sampling.util;

/**
 * Created by David on 2020/2/25
 */
public class LameJni {
    static {
        System.loadLibrary("native-lib");
    }
    public native String getVersion();
    //初始化lame
    public native int pcmTomp3(String pcmPath,String mp3Path,int sampleRate, int channel,  int bitRate);
    public native void destroy();

}
