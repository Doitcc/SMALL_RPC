package com.caox.config;

import com.caox.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class SerializerConfig {

    private static String algorithm;

    private static String serializerClassName;

    private static final String PATH = "com.caox.serializer.";

    /**
     * 初始化序列化方法列表
     * 读取配置文件
     */ 
    static{
        try {
            InputStream in = SerializerConfig.class.getResourceAsStream("/application.properties");
            Properties properties = new Properties();
            properties.load(in);
            algorithm = properties.getProperty("serializer.algorithm");
            serializerClassName = PATH + algorithm + "Serializer";
        } catch (IOException e) {
            log.error("读取序列化配置文件失败" + e.getMessage());
        }
    }

    /**
     * 利用反射创建实例
     *
     * @return
     */
    public static Serializer  getSerializer() {
        try {
/*            System.out.println(serializerClassName);*/
            Class<?> clazz = Class.forName(serializerClassName);
            return (Serializer) clazz.newInstance();
        } catch (Exception e) {
            log.error("创建序列化实例失败"+e.getMessage());
        }
        return null;
    }

    /**
     * 返回序列化类型。jdk序列化为0 Json序列化为1 protostuff序列化为2 默认为jdk序列化
     *
     * @return
     */
    public static int getSerializerType() {
        if ("Json".equals(algorithm)) {
            return 1;
        } else if ("protostuff".equals(algorithm)) {
            return 2;
        }
        return 0;
    }

}
