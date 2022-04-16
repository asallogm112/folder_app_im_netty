package com.chen.handler;

import com.chen.logic.AesUtil;
import com.chen.packet.AbstractPacket;
import com.chen.packet.AllEnums;
import com.chen.packet.PacketType;
import com.chen.packet.ReceiptPacket;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultEventLoop;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.springframework.beans.BeanUtils;

import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {
    
    private Gson gson = new Gson();
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf inBuf, List<Object> out) throws Exception {
        
        int packetType = inBuf.readInt();
        int bodyLength = inBuf.readInt();
        
        byte[] bytes = new byte[bodyLength];
        
        inBuf.readBytes(bytes);
        
        String jsonBody = new String(bytes, Charset.defaultCharset());
        
        Class<? extends AbstractPacket> clazz = PacketType.getPacketClass(packetType);
        if (clazz == null) {
            return;
        }
        
        AbstractPacket packetClass = clazz.getConstructor().newInstance();
    
        packetClass = gson.fromJson(jsonBody, packetClass.getClass());
        String token = packetClass.getToken();
        
        if (AesUtil.verifyToken(token) == false) {
            System.err.println("token 验证失败 :" + token);
            
//            ReceiptPacket failed_pkt = new ReceiptPacket();
//            BeanUtils.copyProperties(packetClass, failed_pkt);
//            failed_pkt.setMsg_status(AllEnums.MsgStatus_Failed);
//            ctx.channel().writeAndFlush(failed_pkt);
    
            out.add(packetClass); //todo remove
        } else {
            out.add(packetClass);
        }
        System.err.println("PacketDecoder : " + out);
    }
    
}
