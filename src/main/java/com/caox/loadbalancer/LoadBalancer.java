package com.caox.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 负载均衡接口
 */
public interface LoadBalancer {
    Instance getOneInstance(List<Instance> instances);
}
