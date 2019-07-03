package remote.procedure.call.server;

public class HelloServiceImpl implements HelloService {
    public String sayHello(String name) {
       return "Hello " + name;
    }
}
