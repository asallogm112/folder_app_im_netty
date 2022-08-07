package com.chen.mapper;

import com.chen.entity.MsgReceipt;

public interface MsgReceiptMapper {
    int insert(MsgReceipt record);

    int insertSelective(MsgReceipt record);
}