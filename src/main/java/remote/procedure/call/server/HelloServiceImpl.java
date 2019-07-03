package remote.procedure.call.server;

public class HelloServiceImpl implements HelloService {
    public void sayHello(String name) {
        System.out.println("Hello " + name);
    }
}
