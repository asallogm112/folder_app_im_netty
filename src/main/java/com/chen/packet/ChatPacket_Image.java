package com.chen.packet;

import lombok.Data;

@Data
public class ChatPacket_Image extends ChatPacket {
    
    static String url_host_prefix = "https://folder-app.oss-cn-shanghai.aliyuncs.com/";
    
    private Integer image_width;
    private Integer image_height;
    
    private String image_url; //suffix app自己拼接
    
    public String getImage_url() {
        return url_host_prefix + image_url;
    }
}
