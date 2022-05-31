package com.chen.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class MyLogging extends LoggingHandler {
    
    public MyLogging(){
        super(LogLevel.INFO);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        if (msg instanceof ByteBuf){
            System.err.println("writerIndex : " + ((ByteBuf) msg).writerIndex());
    
        }
    }
}
