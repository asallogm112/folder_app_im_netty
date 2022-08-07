package com.chen.mapper.extend;

import java.util.List;

import com.chen.entity.OfflineMsg;
import com.chen.mapper.OfflineMsgMapper;
import com.chen.packet.ReceiptPacket;

public interface OfflineMsgMapperExtend extends OfflineMsgMapper{

	List<OfflineMsg> selectListByReceiver_id(String receiver_id);

	void deleteOfflineMsg(ReceiptPacket receipt);
	
}
