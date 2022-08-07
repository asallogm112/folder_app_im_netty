package com.chen.handler;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class FrameDecoder extends LengthFieldBasedFrameDecoder {
    
    public FrameDecoder(SocketChannel ch) {
        super(1024 * 1024, 1, 2);
    }
}
