package com.chen.handler;

import com.chen.logic.SessionManager;
import com.chen.packet.GroupPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;

@ChannelHandler.Sharable
public class GroupEventHandler extends SimpleChannelInboundHandler<GroupPacket> {
    
    private int port;
    private String host;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public GroupEventHandler(RedisTemplate redisTemplate, String host, int port) {
        this.redisTemplate = redisTemplate;
        this.host = host;
        this.port = port;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupPacket pkt) throws Exception {
        
        String group_id = pkt.getGroup_id();
        
        if (pkt.getActionType() == GroupPacket.ActionType.ActionType_Join.ordinal()) {
            SessionManager.joinGroup(group_id, ctx.channel());
            //redisTemplate.opsForSet().add(group_id, host + ":" + port);
        }
        else if (pkt.getActionType() == GroupPacket.ActionType.ActionType_Leave.ordinal()) {
            HashSet set = SessionManager.leaveGroup(group_id, ctx.channel());
            if (set == null || set.size() == 0) {
                //redisTemplate.opsForSet().remove(group_id, host + ":" + port);
            }
        }
        else if (pkt.getActionType() == GroupPacket.ActionType.ActionType_Destroy.ordinal()) {
            SessionManager.destroyGroup(group_id);
            
            Long size = redisTemplate.opsForSet().size(group_id);
            //redisTemplate.opsForSet().pop(group_id, size);
        }
    }
}
