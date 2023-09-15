package com.caox.loadbalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.caox.loadbalancer.LoadBalancer;

import java.util.List;

/**
 * 轮询
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    private int index = 0;

    @Override
    public Instance getOneInstance(List<Instance> instances) {
        if (index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }
}
