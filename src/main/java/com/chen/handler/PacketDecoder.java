package com.chen.handler;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.chen.logic.AesUtil;
import com.chen.packet.AbstractPacket;
import com.chen.packet.AllEnums;
import com.chen.packet.PacketType;
import com.chen.packet.ReceiptPacket;
import com.google.gson.Gson;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {

		while (buf.readableBytes() > 0) {

			int packetType = buf.readInt(); 

//			System.err.println("packetType : " + packetType + "-- size : " + size);

			if (packetType == PacketType.PacketType_HeartBeat.getPacketType()) {
//				size = buf.readableBytes();
//				buf.skipBytes(buf.readableBytes());
//				avai = buf.readableBytes();
//				System.err.println("心跳包 size : " + size);
				return;
			}

			int readableBytes = buf.readableBytes();
			if (readableBytes < 4) {
				buf.resetReaderIndex();
				readableBytes = buf.readableBytes();
				return;
			}

			int bodyLength = buf.readInt();
			readableBytes = buf.readableBytes();
			if (readableBytes < bodyLength) {
				buf.resetReaderIndex();
				return;
			}
			byte[] bytes = new byte[bodyLength];
			buf.readBytes(bytes);

			String body = new String(bytes, Charset.defaultCharset());

			Class<? extends AbstractPacket> clazz = PacketType.getPacketClass(packetType);
			if (clazz == null) {
				return;
			}

			AbstractPacket packetClass = clazz.getConstructor().newInstance();

			packetClass = new Gson().fromJson(body, packetClass.getClass());
			String token = packetClass.getToken();

			if (AesUtil.verifyToken(token) == false) {
				System.err.println("token 验证失败 :" + token);

				ReceiptPacket failed = new ReceiptPacket();
				BeanUtils.copyProperties(packetClass, failed);
				failed.setMsg_status(AllEnums.MsgStatus_Failed);
				ctx.channel().writeAndFlush(failed);
			} else {
				ctx.fireChannelRead(packetClass);
			}
		}
	}

}
