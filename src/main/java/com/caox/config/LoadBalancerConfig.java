package com.caox.config;

import com.caox.loadbalancer.LoadBalancer;
import com.caox.loadbalancer.impl.RandomLoadBalancer;
import com.caox.loadbalancer.impl.RoundRobinLoadBalancer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class LoadBalancerConfig {

    private static String ruler;

    /**
     * 类加载时进行初始化
     */
    static {
        try {
            InputStream in = SerializerConfig.class.getResourceAsStream("/application.properties");
            Properties properties = new Properties();
            properties.load(in);
            ruler = properties.getProperty("loadbalancer.ruler");
        } catch (IOException e) {
            log.error("读取负载均衡配置文件失败" + e.getMessage());
        }
    }

    /**
     * 读取负载均衡器
     *
     * @return
     */
    public static LoadBalancer getLoadBalance() {
        switch (ruler) {
            case "RoundBin":
                return new RoundRobinLoadBalancer();
            case "Random":
                return new RandomLoadBalancer();
        }
        //默认是轮询方式
        return new RoundRobinLoadBalancer();
    }
}
