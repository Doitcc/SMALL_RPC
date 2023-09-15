package com.caox.server;

import com.caox.annotation.ServiceImplementation;
import com.caox.handler.RequestMessageHandler;
import com.caox.handler.ServerReadHeartHandler;
import com.caox.protocol.MyProtocol;
import com.caox.protocol.ProcotolFrameDecoder;
import com.caox.register.NacosRegisterAndDiscovery;
import com.caox.register.ServiceProvider;
import com.caox.register.impl.NacosRegisterAndDiscoveryImpl;
import com.caox.register.impl.ServiceProviderImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.net.InetSocketAddress;
import java.util.Set;

@Slf4j
public class RpcServer {
    //主机号
    private final String host;
    //端口号
    private final int port;
    //服务注册
    private final NacosRegisterAndDiscovery nacosServerRegistry;
    //本地注册
    private final ServiceProvider serviceProvider;


    public RpcServer(String host, int port) {
        this.host = host;
        this.port = port;
        nacosServerRegistry = new NacosRegisterAndDiscoveryImpl();
        serviceProvider = new ServiceProviderImpl();
    }
    
    public void start() {
        //boss 处理连接
        NioEventLoopGroup boss = new NioEventLoopGroup();
        //worker 处理读写
        NioEventLoopGroup worker = new NioEventLoopGroup();
        //客户端空闲检测处理器
        ServerReadHeartHandler READ_HEART_HANDLER=new ServerReadHeartHandler();
        //日志处理器
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        //自定义协议编解码器
        MyProtocol MY_PROTOCOL = new MyProtocol();
        //请求处理器
        RequestMessageHandler REQUEST_HANDLER = new RequestMessageHandler();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .group(boss, worker)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProcotolFrameDecoder());
                            ch.pipeline().addLast(new IdleStateHandler(30,0,0));
                            ch.pipeline().addLast(LOGGING_HANDLER);
                            ch.pipeline().addLast(MY_PROTOCOL);
                            ch.pipeline().addLast(READ_HEART_HANDLER);
                            ch.pipeline().addLast(REQUEST_HANDLER);
                        }
                    });
            
            //阻塞直到建立连接
            ChannelFuture future = serverBootstrap.bind(port).sync();
            //回调方法
            future.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> {
/*                ServerShutDownHook.getServerShutDownHook().addClearAllHook(new InetSocketAddress(host,port));*/
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            log.error("服务端启动异常", e);
        }
    }

    /**
     * 自动扫描方法，将其注册到nacos及本地
     */
    private void autoRegisterService() {
        //利用Reflections 框架获取所有被@RpcService注解修饰的类（即被调用方法所在的实现类）
        //因为所调用的方法都在com.caox.service路径下，所以我这里直接写死了
        Reflections reflections = new Reflections("com.caox.service");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(ServiceImplementation.class);

        for (Class<?> clazz : classes) {
            //获取实现类的全限定类名
            String serviceName = clazz.getCanonicalName();
            //获取接口类
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> interfaceOne : interfaces) {
                //获取接口类的全限定类名
                String serviceInterfaceName = interfaceOne.getCanonicalName();
                //注册到nacos
                nacosServerRegistry.register(serviceInterfaceName, new InetSocketAddress(host, port));

                //本地注册
                serviceProvider.addService(serviceName, serviceInterfaceName);
            }
        }
    }


    public <T> void publishService() {
        //自动注册服务
        autoRegisterService();
        //测试使用
        start();
    }

}
