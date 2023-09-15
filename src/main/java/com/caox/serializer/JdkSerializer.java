package com.caox.serializer;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class JdkSerializer implements Serializer {
    @Override
    public <T> T deserialize(byte[] bytes,Class<T> clazz) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("反序列化错误"+e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> byte[] serialize(T object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (IOException e) {
            log.error("序列化错误"+e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
