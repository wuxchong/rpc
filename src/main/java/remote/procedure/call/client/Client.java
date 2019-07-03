package remote.procedure.call.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    /**
     * 获取代表服务端接口的动态代理对象（HelloService）
     * @param serviceInterface 请求的接口名， 不用String的原因是不好获取到String的Classloader
     * @param address 地址
     */
    public static <T> T getRemoteProxyObj(final Class serviceInterface, final InetSocketAddress address) {

        InvocationHandler handler = new InvocationHandler() {
            //proxy:代理的对象 method:哪个方法 args:代理方法的参数
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //客户端向服务端发送请求；请求某一个具体的接口
                //发送OutputStream，接收InputStream
                Socket socket = new Socket();
                socket.connect(address);
                //序列化，发送
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                //接口名，方法名 writeUTF
                output.writeUTF(serviceInterface.getName());
                output.writeUTF(method.getName());
                //方法类型，参数
                output.writeObject(method.getParameterTypes());
                output.writeObject(args);
                //等待服务端处理

                //接收服务端处理后的返回值
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                return input.readObject();
            }
        };

        //newProxyInstance(a, b, c)
        //a:类加载器；需要代理哪个类，就使用它的类加载器
        //b:需要代理的对象，具备哪些方法 --接口
        //c:handler对象
        return (T)Proxy.newProxyInstance(serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface}, handler);
    }
}
