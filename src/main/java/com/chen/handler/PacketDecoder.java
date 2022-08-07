package com.chen.handler;

import com.chen.enums.MsgType;
import com.chen.enums.PacketType;
import com.chen.packet.*;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ChannelHandler.Sharable
public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {
    
    static Map<MsgType, Class<? extends ChatPacket>> msg_types = new HashMap<>();
    static Map<PacketType, Class<? extends AbstractPacket>> pkt_types = new HashMap<>();
    
    static {
        msg_types.put(MsgType.MsgType_Text, ChatPacket_Text.class);
        msg_types.put(MsgType.MsgType_Image, ChatPacket_Image.class);
        msg_types.put(MsgType.MsgType_Audio, ChatPacket_Audio.class);
        msg_types.put(MsgType.MsgType_Video, ChatPacket_Video.class);
        msg_types.put(MsgType.MsgType_Location, ChatPacket_Location.class);
        msg_types.put(MsgType.MsgType_Goods, ChatPacket_Goods.class);
        
        pkt_types.put(PacketType.PacketType_HeartBeat, null);
        pkt_types.put(PacketType.PacketType_Login, LoginPacket.class);
        pkt_types.put(PacketType.PacketType_Receipt, ReceiptPacket.class);
        pkt_types.put(PacketType.PacketType_Group, GroupPacket.class);
        pkt_types.put(PacketType.PacketType_Logout, LoginPacket.class);
    }
    
    Gson gson = new Gson();
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf inBuf, List<Object> out) throws Exception {
        
        int idx = inBuf.readByte();
        PacketType packetType = PacketType.values()[idx];
        
        short length = inBuf.readShort(); //length
        
        if (packetType == PacketType.PacketType_Chat
                || packetType == PacketType.PacketType_Forward
                || packetType == PacketType.PacketType_Live) {
            
            int msg_type = inBuf.readByte();
            MsgType type = MsgType.values()[msg_type];
            
            Class<? extends ChatPacket> clazz = msg_types.get(type);
            
            byte[] arr = new byte[length - 1]; //msg type
            inBuf.readBytes(arr);
            
            String json = new String(arr, Charset.defaultCharset());
            
            ChatPacket pkt = gson.fromJson(json, clazz);
            out.add(pkt);
            
        }
        // TODO: 2022/6/29  heartbeat pkt
        else {
            Class<? extends AbstractPacket> clazz = pkt_types.get(packetType);
            if (clazz == null) {
                inBuf.skipBytes(length); //heartbeat
                return;
            }
            
            byte[] arr = new byte[length];
            inBuf.readBytes(arr);
            
            String json = new String(arr, Charset.defaultCharset());
            
            AbstractPacket pkt = gson.fromJson(json, clazz);
            pkt.setPacket_type(packetType.ordinal());
            out.add(pkt);
        }

//        if (AesUtil.verifyToken(token) == false) {
//        }else{
//        }
    }
    
}
