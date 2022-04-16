package com.chen.handler;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class FrameDecoder extends LengthFieldBasedFrameDecoder {
    
    public FrameDecoder() {
        super(1024 * 1024, 4, 4);
    }
}
