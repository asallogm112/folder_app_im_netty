package com.chen.handler;

import com.chen.packet.HeartBeatPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class WriteIdleHandler extends IdleStateHandler {
    public WriteIdleHandler() {
        super(0, 20, 0, TimeUnit.SECONDS);
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        
        System.err.println("userEventTriggered");
        if (evt instanceof IdleStateEvent) {
            
            ctx.channel().writeAndFlush(new HeartBeatPacket());
        }
        
    }
}
