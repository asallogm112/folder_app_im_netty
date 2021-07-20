package com.chen.mapper;

import com.chen.packet.ReceiptPacket;

public interface MsgReceiptMapper {
    int insert(ReceiptPacket record);

    int insertSelective(ReceiptPacket record);
    
}