package com.caox.loadbalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.caox.loadbalancer.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * 随机
 */
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public Instance getOneInstance(List<Instance> instances) {
        // nextInt():生成一个介于[0, instances.size())的int型随机值
        return instances.get(new Random().nextInt(instances.size()));
    }
}
