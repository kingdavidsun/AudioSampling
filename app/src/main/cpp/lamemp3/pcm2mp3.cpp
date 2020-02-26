//
// Created by 23174 on 2020/2/25.
//

#include "pcm2mp3.h"

int pcm2mp3::init(const char *pcm_path, const char *mp3_path, int sample_rate, int channel,
                  int bitRate) {
    int result=-1;
    pcm_file=fopen(pcm_path,"rb");
    if(pcm_file!= NULL){
        mp3_file=fopen(mp3_path,"wb");
        if (mp3_file!= NULL){
            //开始初始化
            lame_client=lame_init();
            lame_set_in_samplerate(lame_client,sample_rate);
            lame_set_out_samplerate(lame_client,sample_rate);
            lame_set_num_channels(lame_client,channel);
            lame_set_brate(lame_client,bitRate);
            lame_set_quality(lame_client,2);
            lame_init_params(lame_client);
            result=0;
        }
    }
    return result;
}

pcm2mp3::pcm2mp3() {

}

pcm2mp3::~pcm2mp3() {
    //释放资源
}

//pcm文件转mp3文件
void pcm2mp3::pcm_to_mp3() {
    int bufferSize = 1024 * 256;
    short *buffer = new short[bufferSize / 2];
    short *leftBuffer = new short[bufferSize / 4];
    short *rightBuffer = new short[bufferSize / 4];
    unsigned char* mp3_buffer = new unsigned char[bufferSize];
    size_t readBufferSize = 0;
    while ((readBufferSize = fread(buffer, 2, bufferSize / 2, pcm_file)) > 0) {
        for (int i = 0; i < readBufferSize; i++) {
            if (i % 2 == 0) {
                leftBuffer[i / 2] = buffer[i];
            } else {
                rightBuffer[i / 2] = buffer[i];
            }
        }
        size_t wroteSize = lame_encode_buffer(lame_client, (short int *) leftBuffer, (short int *) rightBuffer, (int)(readBufferSize / 2), mp3_buffer, bufferSize);
        fwrite(mp3_buffer, 1, wroteSize, mp3_file);
    }

    delete [] buffer;
    delete [] leftBuffer;
    delete [] rightBuffer;
    delete [] mp3_buffer;


}

void pcm2mp3::release() {

    if (pcm_file){
        fclose(pcm_file);
    }
    if (mp3_file){
        fclose(mp3_file);
        lame_close(lame_client);
    }
}

