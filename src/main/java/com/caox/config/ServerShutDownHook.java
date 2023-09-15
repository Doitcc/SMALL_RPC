package com.caox.config;

import ch.qos.logback.core.hook.ShutdownHook;
import com.caox.utils.NacosUtil;
import com.caox.utils.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

public class ServerShutDownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private final ExecutorService threadPool = ThreadPoolUtil.createDefaultThreadPool("shutdown-hook");
    private static final ServerShutDownHook serverShutDownHook = new ServerShutDownHook();

    public static ServerShutDownHook getServerShutDownHook() {
        return serverShutDownHook;
    }

    public void addClearAllHook(InetSocketAddress address) {
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry(address);
            threadPool.shutdown();
        }));
    }
}
