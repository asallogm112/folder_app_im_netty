package com.chen.logic;


import com.chen.enums.NodeMachine;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    
    static AttributeKey<String> key = AttributeKey.valueOf("user_id");
    
    private static ConcurrentHashMap<String, Channel> single_chat = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, HashSet<Channel>> group_chat = new ConcurrentHashMap<>();
    
    private static ConcurrentHashMap<String, HashSet<NodeMachine>> other_machines = new ConcurrentHashMap<>();
    
    public static void linkChannel(String user_id, Channel channel) {
        channel.attr(key).setIfAbsent(user_id);
        single_chat.put(user_id, channel);
    }
    
    public static Channel getChannel(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        Channel channel = single_chat.get(userId);
        return channel;
    }
    
    public static void unlinkChannel(String userId) {
        Channel channel = single_chat.get(userId);
        if (channel != null) {
            channel.close();
            single_chat.remove(userId);
        }
    }
    
    public static void joinGroup(String group_id, Channel channel) {
        HashSet set = group_chat.computeIfAbsent(group_id, k -> new HashSet<Channel>());
        set.add(channel);
    }
    
    public static HashSet leaveGroup(String group_id, Channel channel) {
        HashSet set = group_chat.computeIfAbsent(group_id, null);
        if (set != null) {
            set.remove(channel);
        }
        return set;
    }
    
    public static void destroyGroup(String group_id) {
        group_chat.remove(group_id);
    }
    
    public static HashSet<Channel> getGroup(String group_id) {
        HashSet<Channel> channels = group_chat.get(group_id);
        return channels;
    }
    
    public static HashSet<NodeMachine> getOtherMachines(String group_id) {
        HashSet<NodeMachine> channels = other_machines.get(group_id);
        return channels;
    }
    
    public static HashSet<NodeMachine> addOtherMachine(String group_id, NodeMachine nodeMachine) {
        HashSet<NodeMachine> machines = other_machines.computeIfAbsent(group_id, k -> new HashSet<>());
        machines.add(nodeMachine);
        return machines;
    }
    
    public static HashSet<NodeMachine> clearOtherMachines(String group_id) {
        HashSet<NodeMachine> channels = other_machines.remove(group_id);
        return channels;
    }
}
