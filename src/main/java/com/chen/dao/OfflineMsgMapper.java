package com.chen.dao;

import com.chen.entity.OfflineMsg;

public interface OfflineMsgMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OfflineMsg record);

    int insertSelective(OfflineMsg record);

    OfflineMsg selectByPrimaryKey(Integer id);
}