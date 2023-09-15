import com.caox.annotation.ServiceImplementation;
import com.caox.pojo.Student;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class Test {
    public static void main(String[] args) throws Exception {
/*        Class<?> clazz = Class.forName("com.caox.service.Impl.ServiceOneImpl");
        Object service = clazz.newInstance();
        Method method = clazz.getMethod("hello", String.class);
*//*        Method method = service.getClass().getMethod("hello", String.class);*//*
        Object result = method.invoke(service, "你好");
        System.out.println(result);*/
        
/*        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 8080);
        System.out.println(inetSocketAddress.getHostString().getClass());
        System.out.println(inetSocketAddress.getHostName().getClass());*/
        /*        System.out.println(new InetSocketAddress("localhost",8080));*/

/*        Class<?> clazz = Class.forName("com.caox.service.Impl.ServiceTwoImpl");
        Object service = clazz.newInstance();
        Class<?> cla = Student.class;
        Method method = clazz.getMethod("write", cla);
        Student student = new Student(101, "caox");
        Object result = method.invoke(service, student);
        System.out.println(result);*/
        
        
    }
}
