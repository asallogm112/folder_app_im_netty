package com.chen.packet;

import lombok.Data;

@Data
public class GroupPacket extends AbstractPacket {
    
    private int actionType;
    private String group_id;
    
    public enum ActionType {
        ActionType_Join,
        ActionType_Leave,
        ActionType_Destroy;
    }
}
