package remote.procedure.call.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

//服务中心的具体实现
public class ServerCenter implements Server {
    //map，服务端所有可供访问的接口都注册到该map中
    //key：接口名称，value：接口的具体实现
    private static HashMap<String, Class> serviceRegister = new HashMap<String, Class>();
    private int port;

    public ServerCenter(int port) {
        this.port = port;
    }
    //通过Socket通信
    public void start() {
        ServerSocket server;
        ObjectInputStream input = null;
        ObjectOutputStream output = null;
        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress(port));
            //等待客户端连接
            Socket socket = server.accept();

            //接收到客户连接请求,处理该请求
            input = new ObjectInputStream(socket.getInputStream());
            //在序列化流中，对发送数据顺序严格要求
            String serviceName = input.readUTF();
            String methodName = input.readUTF();
            Class[] parameterTypes = (Class[])input.readObject();
            Object[] arguments = (Object[])input.readObject();
            //根据客户请求，在map中找到对呀的具体接口
            Class serviceClass = serviceRegister.get(serviceName);//HelloService
            Method method = serviceClass.getMethod(methodName,parameterTypes);
            //执行该方法
            Object result = method.invoke(serviceClass.newInstance(),arguments);

            //将方法结果传回客户端
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(result);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            }catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void stop() {

    }

    public void register(Class service, Class serviceImpl) {
        serviceRegister.put(service.getName(), serviceImpl);
    }
}
