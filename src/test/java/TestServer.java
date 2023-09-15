import com.caox.server.RpcServer;
import com.caox.service.ServiceOne;
import io.protostuff.Rpc;

public class TestServer {
    public static void main(String[] args) {
        /*服务端开启多个服务端*/
        RpcServer rpcServer=new RpcServer("localhost",8080);
        rpcServer.publishService();

        RpcServer rpcServer2=new RpcServer("localhost",8081);
        rpcServer2.publishService();

        RpcServer rpcServer3=new RpcServer("localhost",8082);
        rpcServer3.publishService();

        RpcServer rpcServer4=new RpcServer("localhost",8083);
        rpcServer4.publishService();
        
    }
}
