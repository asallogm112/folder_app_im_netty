package com.chen.mapper.extend;

import java.util.List;

import com.chen.mapper.MsgReceiptMapper;
import com.chen.packet.ReceiptPacket;

public interface MsgReceiptMapperExtend extends MsgReceiptMapper{

	List<ReceiptPacket> selectReceiptListBySender_id(String sender_id);
    
    void deleteReceiptListBy_id(String sender_id);
}
