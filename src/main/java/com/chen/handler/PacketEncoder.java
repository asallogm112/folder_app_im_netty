package com.chen.handler;

import com.chen.enums.PacketType;
import com.chen.packet.AbstractPacket;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class PacketEncoder extends MessageToByteEncoder<AbstractPacket> {
    
    Gson gson = new Gson();
    
    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractPacket pkt, ByteBuf outBuf) throws Exception {
        
        outBuf.writeByte(pkt.getPacket_type());
        
        String json = gson.toJson(pkt);
        
        if (pkt.getPacket_type() == PacketType.PacketType_Chat.ordinal() ||
                pkt.getPacket_type() == PacketType.PacketType_Live.ordinal() ||
                pkt.getPacket_type() == PacketType.PacketType_Forward.ordinal()) {
            
            outBuf.writeShort(json.getBytes(StandardCharsets.UTF_8).length + 1); //+1: msg_type
            outBuf.writeByte(pkt.getMsg_type());
        } else {
            outBuf.writeShort(json.getBytes(StandardCharsets.UTF_8).length);
        }
        
        outBuf.writeBytes(json.getBytes(StandardCharsets.UTF_8));
        
    }
}
