package com.caox.register.impl;

import com.caox.register.ServiceProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册服务到本地（接口全限定类名---实现类全限定类名），相比于nacos，这里主要是通过接口来找实现类，
 * 然后通过反射（因为反射需要实现类）去调用方法
 */
public class ServiceProviderImpl implements ServiceProvider {
    private static final Map<String,String> serviceMap=new ConcurrentHashMap<>();
    
    @Override
    public void addService(String serviceimplName,String serviceinterfaceName) {
        serviceMap.put(serviceinterfaceName,serviceimplName);
    }

    @Override
    public String getService(String serviceinterfaceName) {
        
        return serviceMap.get(serviceinterfaceName);
    }
}
