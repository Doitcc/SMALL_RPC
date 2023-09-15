package com.caox.register.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.caox.config.LoadBalancerConfig;
import com.caox.loadbalancer.LoadBalancer;
import com.caox.register.NacosRegisterAndDiscovery;
import com.caox.utils.NacosUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 服务的注册和发现，主要是注册到nacos上，有多个服务端时，让每一个客户端可以负载均衡的选择一个服务端
 */
@Slf4j
public class NacosRegisterAndDiscoveryImpl implements NacosRegisterAndDiscovery {

    /**
     * 服务注册
     *
     * @param serviceinterfaceName 服务名称
     * @param inetSocketAddress    服务地址
     */
    @Override
    public void register(String serviceinterfaceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerServer(serviceinterfaceName, inetSocketAddress);
        } catch (NacosException e) {
            log.error("服务注册失败");
            e.printStackTrace();
        }
    }
    
    //new LoabBalance()时必须放到静态代码块让其只执行一次，因为如果不这样做，
    // 负载均衡器里面的index每new一次就会重置为0，相当于每调用一次服务，都只用到了第一个服务端
    private static LoadBalancer loadBalancer;
    static{
        loadBalancer=LoadBalancerConfig.getLoadBalance();
    }

    /**
     * 根据接口名找到nacos里包含该接口的服务列表，并用负载均衡器选择其中一个
     *
     * @param serviceinterfaceName 调用方法接口的全限定类名
     * @return 返回其中的一个服务
     */
    @Override
    public InetSocketAddress lookupService(String serviceinterfaceName) {
        InetSocketAddress address = null;
        try {
            List<Instance> allInstance = NacosUtil.getAllInstance(serviceinterfaceName);
            if (allInstance.isEmpty()) {
                log.error("服务不存在: [{}]", serviceinterfaceName);
            } else {
                //用负载均衡器选择其中的一个
                Instance oneService = loadBalancer.getOneInstance(allInstance);
                address = new InetSocketAddress(oneService.getIp(), oneService.getPort());
            }
        } catch (NacosException e) {
            log.error("查找服务异常" + e.getMessage());
        }
        return address;
    }
}
