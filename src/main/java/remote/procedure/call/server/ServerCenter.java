package remote.procedure.call.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashMap;

//服务中心的具体实现
public class ServerCenter implements Server {
    //map，服务端所有可供访问的接口都注册到该map中
    //key：接口名称，value：接口的具体实现
    private static HashMap<String, Class> serviceRegister = new HashMap<String, Class>();
    private static int port;

    public ServerCenter(int port) {
        this.port = port;
    }
    //通过Socket通信
    public void start() throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(port));
        //等待客户端连接
        server.accept();
    }

    public void stop() {

    }

    public void register(Class service, Class serviceImpl) {
        serviceRegister.put(service.getName(), serviceImpl);
    }
}
