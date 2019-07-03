package remote.procedure.call.test;

import remote.procedure.call.client.Client;
import remote.procedure.call.server.HelloService;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class RPCClientTest {
    /**
     * RPC原理
     * 1.客户端通过Socket请求客户端，并且通过字符串形式将需要请求的接口发送给服务端
     * 2.服务端将可以提供的接口注册到服务中心（通过map保存 key：接口的名字 value：接口的实现类）
     * 3.服务端收到客户端的请求后，通过请求中的接口名在服务中心的map中寻找对应的接口实现类
     * 4.找到后：解析刚才客户端发送来的接口名，方法名
     * 5.解析完成后，执行该方法，并将结果返回给客户端
     */
    public static void main(String[] args) {
        final HelloService service;
        try {
            service = Client.getRemoteProxyObj(
                            Class.forName("remote.procedure.call.server.HelloService"),
                            new InetSocketAddress("127.0.0.1", 9999));
            final CountDownLatch countDownLatch = new CountDownLatch(50);
            for (int i = 0; i < 50; i++) {
                new Thread(new Runnable() {
                    public void run() {
                        countDownLatch.countDown();
                        System.out.println(service.sayHello("" + Thread.currentThread().getName()));
                    }
                }).start();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
