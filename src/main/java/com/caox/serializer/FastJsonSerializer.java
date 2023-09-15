package com.caox.serializer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FastJsonSerializer implements Serializer {


    @Override
    public <T> byte[] serialize(T object) {
        try {
            byte[] bytes = JSON.toJSONBytes(object);
            return bytes;
        } catch (Exception e) {
            log.error("序列化错误"+e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            T t = (T) JSON.parseObject(bytes, clazz);
            return t;
        } catch (Exception e) {
            log.error("反序列化错误"+e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
