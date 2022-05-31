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
    
        PacketType.initPacketTypes();
        
        EmbeddedChannel channel = new EmbeddedChannel(
                new LoggingHandler(), new FrameDecoder()
                , new PacketDecoder(), new PacketEncoder()
                , new ChatHandler());
    
    
        System.err.println("-------------- client pipeline : "+channel.pipeline());
    
        ChatPacket msg = new ChatPacket();
        msg.setReceiver_id("aaa");
        msg.setSender_id("bbb");
    
        PacketEncoder encoder = new PacketEncoder();
        
        channel.writeInbound(buff(msg));
//        channel.writeOutbound(msg);
    }
    
    public static ByteBuf buff(AbstractPacket msg){
    
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        
        out.writeInt(msg.getPacketType()); //packet type
    
        String jsonStr = new Gson().toJson(msg);
    
        byte[] bytes = jsonStr.getBytes(Charset.forName("UTF-8"));
    
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    
        System.err.println("--------- writerIndex : "+out.writerIndex());
        return out;
    }
}
