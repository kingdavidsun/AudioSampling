#include <jni.h>
#include <string>
#include <android/log.h>
#include <lame.h>
#include <pcm2mp3.h>

lame_t lame;
extern "C"
JNIEXPORT jstring JNICALL
Java_com_david_sampling_util_LameJni_getVersion(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(get_lame_version());
}
pcm2mp3 *mp3_encoder;
extern "C"
JNIEXPORT int JNICALL
Java_com_david_sampling_util_LameJni_pcmTomp3(JNIEnv *env, jclass clazz, jstring pcm_path,
                                          jstring mp3_path, jint sample_rate, jint channel,
                                          jint bit_rate) {
    int result=-1;
    const char *pcm_path_=env->GetStringUTFChars(pcm_path,0);
    const char *mp3_path_=env->GetStringUTFChars(mp3_path,0);
    mp3_encoder=new pcm2mp3();
    mp3_encoder->init(pcm_path_,mp3_path_,sample_rate,channel,bit_rate);
    mp3_encoder->pcm_to_mp3();
    env->ReleaseStringUTFChars(pcm_path,pcm_path_);
    env->ReleaseStringUTFChars(mp3_path,mp3_path_);
    return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_david_sampling_util_LameJni_destroy(JNIEnv *env, jclass clazz) {
    if(mp3_encoder!= NULL){
        mp3_encoder->release();
        delete mp3_encoder;
        mp3_encoder= NULL;
    }
}