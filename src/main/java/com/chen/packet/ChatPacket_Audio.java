package com.chen.packet;

import lombok.Data;

@Data
public class ChatPacket_Audio extends ChatPacket {
    
    static String url_host_prefix = "https://folder-app.oss-cn-shanghai.aliyuncs.com/";
    
    private short duration;
    private String audio_url;
    
    public String getAudio_url() {
        return url_host_prefix + audio_url;
    }
}
