package com.chen.packet;

import lombok.Data;

@Data
public class ChatPacket_Video extends ChatPacket {
    
    static String url_host_prefix = "https://folder-app.oss-cn-shanghai.aliyuncs.com/";
    
    private short duration;
    private String video_url;
    
    public String getVideo_url() {
        return url_host_prefix + video_url;
    }
}
