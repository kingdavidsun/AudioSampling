//
// Created by 23174 on 2020/2/25.
//

#ifndef AUDIAOSAMPLING_PCM2MP3_H
#define AUDIAOSAMPLING_PCM2MP3_H

#include <stdio.h>
#include <iostream>
#include "lame.h"
#include <pthread.h>

class pcm2mp3 {
public:
    FILE *pcm_file;
    FILE *mp3_file;
    lame_t lame_client;
    pthread_t *encode_thread;
    pcm2mp3();
    ~pcm2mp3();
    int init(const char*pcm_path, const char*mp3_path,int sample_rate,int channel,int bitRate);
    void pcm_to_mp3();
    void release();
};


#endif //AUDIAOSAMPLING_PCM2MP3_H
