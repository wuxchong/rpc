package remote.procedure.call.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//服务中心的具体实现
public class ServerCenter implements Server {
    //map，服务端所有可供访问的接口都注册到该map中
    //key：接口名称，value：接口的具体实现
    private static HashMap<String, Class> serviceRegister = new HashMap<String, Class>();
    private static int port;
    //连接池，可以同时处理多个客户端请求
    private static ExecutorService executor = Executors.newFixedThreadPool(5);
    //服务未启动
    private static boolean isRunning = false;
    public ServerCenter(int port) {
        ServerCenter.port = port;
    }
    //通过Socket通信
    public void start() {
        ServerSocket server = null;
        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRunning = true;//启动服务
        while(true) {
            //等待客户端连接
            System.out.println("server start....");
            Socket socket = null;
            try {
                socket = server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            executor.execute(new ServiceTask(socket));
        }
    }

    //关闭服务
    public void stop() {
        isRunning = false;
        executor.shutdown();
    }

    public void register(Class service, Class serviceImpl) {
        serviceRegister.put(service.getName(), serviceImpl);
    }

    private static class ServiceTask implements Runnable {
        private Socket socket;
        public ServiceTask() {
        }
        public ServiceTask(Socket socket) {
            this.socket = socket;
        }
        public void run() {
            ObjectInputStream input = null;
            ObjectOutputStream output = null;
            try {
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
    }
}
