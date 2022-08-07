package com.chen;

import com.chen.handler.ChatHandler;
import com.chen.handler.PacketDecoder;
import com.chen.handler.PacketEncoder;
import com.chen.packet.ChatPacket;
import io.netty.channel.ChannelFuture;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StopWatch;

import java.net.InetSocketAddress;

@SpringBootTest
class AppTests {
    
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void test(){
    
    }

}
