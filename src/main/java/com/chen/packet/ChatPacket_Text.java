package com.chen.packet;

import lombok.Data;

@Data
public class ChatPacket_Text extends ChatPacket {
    
    private String text;
    
}
