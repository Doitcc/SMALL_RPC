package com.caox.utils;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class NacosUtil {
    //nacos服务的地址
    private static  final String SERVER_ADDR = "127.0.0.1:8848";

    private static NamingService namingservice = null;

    /**
     * 保存注册的服务
     */
    private static final Set<String> serviceinterfaceNames = new HashSet<>();

    //初始化
    static {
        try {
            namingservice = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            log.error("nacos连接失败:"+e);
            e.printStackTrace();
        }
    }

    /**
     * 注册服务到nacos
     * @param serviceinterfaceName 接口的全限定类名
     * @param inetSocketAddress 服务地址（host，port）
     */
    public static void registerServer(String serviceinterfaceName, InetSocketAddress inetSocketAddress) throws NacosException {
        namingservice.registerInstance(serviceinterfaceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        serviceinterfaceNames.add(serviceinterfaceName);
    }


    /**
     * 返回当前服务名的所有实例
     * @param serverName
     * @return
     * @throws NacosException
     */
    public static List<Instance> getAllInstance(String serverName) throws NacosException {
        List<Instance> allInstance = null;
        try {
            allInstance = namingservice.getAllInstances(serverName);
        } catch (NacosException e) {
            log.error("服务获取失败");
            e.printStackTrace();
        }
        return allInstance;
    }


    /**
     * 注销address上的服务
     * @param address 地址
     */
    public static void clearRegistry(InetSocketAddress address) {
        if(!serviceinterfaceNames.isEmpty()) {
            String host = address.getHostName();
            int port = address.getPort();
            for (String serviceName : serviceinterfaceNames) {
                try {
                    //注销服务
                    namingservice.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    log.error("清除服务失败"+e.getMessage());
                }
            }
        }
    }
}
