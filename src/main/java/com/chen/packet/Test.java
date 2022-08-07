package com.chen.packet;

import com.chen.handler.ChatHandler;
import com.chen.handler.FrameDecoder;
import com.chen.handler.PacketDecoder;
import com.chen.handler.PacketEncoder;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.jmx.export.naming.SelfNaming;

import java.nio.charset.Charset;
import java.util.List;

public class Test {
    
    public static void main(String[] args) throws InterruptedException {
    
    
    }
}
