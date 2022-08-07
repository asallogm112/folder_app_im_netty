package com.chen.packet;

import lombok.Data;

@Data
public class ChatPacket_Location extends ChatPacket {
    
    private Double latitude;
    private Double longitude;
    
    private String name;
    private String address;
}
