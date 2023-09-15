package com.caox.serializer;

/**
 * 序列化和反序列化的接口类
 */
public interface Serializer {
    // 反序列化方法
    <T> T deserialize(byte[] bytes,Class<T> clazz);

    // 序列化方法
    <T> byte[] serialize(T object);
}
