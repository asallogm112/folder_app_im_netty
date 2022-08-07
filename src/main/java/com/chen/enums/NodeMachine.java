package com.chen.enums;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class NodeMachine{
    String host_colon_port;
    Channel channel;
}