package com.chen.mapper;

import java.util.List;

import com.chen.entity.OfflineMsg;
import com.chen.packet.ReceiptPacket;

public interface OfflineMsgMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(OfflineMsg record);

	int insertSelective(OfflineMsg record);

	OfflineMsg selectByPrimaryKey(Integer id);

	List<OfflineMsg> selectByReceiverId(String receiver_id);

	void deleteOfflineMsg(ReceiptPacket receipt);
}