package remote.procedure.call.server;

//服务中心
public interface Server {
    //启动
    void start();
    //停止
    void stop();
    //注册中心
    void register(Class service, Class serviceImpl);

}
