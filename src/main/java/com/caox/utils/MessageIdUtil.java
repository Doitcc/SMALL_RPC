package com.caox.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息Id生成工具
 */
public class MessageIdUtil {
    private static final AtomicInteger messageId=new AtomicInteger(0);
    
    public static int increase(){
        return messageId.getAndIncrement();
    }
}
