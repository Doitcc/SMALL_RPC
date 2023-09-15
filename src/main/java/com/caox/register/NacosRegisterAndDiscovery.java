package com.caox.register;

import java.net.InetSocketAddress;

/**
 * nacos服务注册接口
 */
public interface NacosRegisterAndDiscovery {
    
    void register(String serviceinterfaceName, InetSocketAddress inetSocketAddress);

    InetSocketAddress  lookupService(String serviceName);

}
