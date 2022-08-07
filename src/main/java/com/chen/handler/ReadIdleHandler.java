package com.chen.handler;

import com.chen.logic.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.InetSocketAddress;

public class ReadIdleHandler extends IdleStateHandler {
    
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    AttributeKey<String> key = AttributeKey.valueOf("user_id");
    
    public ReadIdleHandler() {
        super(60, 0, 0);
    }
    
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    
        System.err.println("=== userEventTriggered read idle handler");
        
        // 心跳包检测读超时
        if (evt instanceof IdleStateEvent) {
    
            ctx.channel().close();
            String user_id = ctx.channel().attr(key).getAndSet(null);
            SessionManager.unlinkChannel(user_id);
    
            redisTemplate.opsForValue().set(user_id, null);
    
            ctx.pipeline().remove(this);
    
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            System.err.println("=== IM连接超时了..." + socketAddress.getAddress().getAddress().toString());
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // TODO: 2022/6/21
        System.err.println("=== exceptionCaught");
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    
        System.err.println("=== channelInactive read idle handler");
        
        ctx.channel().close();
        String user_id = ctx.channel().attr(key).getAndSet(null);
        SessionManager.unlinkChannel(user_id);
    }
}
