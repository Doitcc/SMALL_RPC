import com.caox.pojo.Student;
import com.caox.service.ServiceOne;
import com.caox.service.ServiceTwo;
import com.caox.utils.ProxyUtil;

public class TestClient {
    public static void main(String[] args) {
        ProxyUtil proxyUtil = new ProxyUtil();
        
        //被调用方法的接口
        ServiceOne serviceOne = proxyUtil.getProxy(ServiceOne.class);
        
        /*测试一个客户端发送多次请求*/
        String result1 = serviceOne.hello("11111111111111");
        System.out.println(result1);
        String result2 = serviceOne.hello("2222222222222");
        System.out.println(result2);
        String result3 = serviceOne.hello("33333333333333");
        System.out.println(result3);
        
    }
}