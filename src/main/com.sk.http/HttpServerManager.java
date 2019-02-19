import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServerManager {
    private final static HttpServerManager instance = new HttpServerManager();
    private HttpServer httpServer;
    private ExecutorService executor;

    private HttpServerManager(){

    }

    public final static HttpServerManager getInstance(){
        return instance;
    }

    public final static void init() throws IOException{
        HttpServerManager.getInstance().executor = Executors.newCachedThreadPool();
        final InetSocketAddress inetSocketAddress = new InetSocketAddress("0.0.0.0",8080);
        HttpServerManager.getInstance().httpServer = HttpServer.create(inetSocketAddress,0);
        HttpServerManager.getInstance().httpServer.setExecutor( HttpServerManager.getInstance().executor);
        HttpServerManager.getInstance().httpServer.createContext("/",new HttpServerHandler());
        HttpServerManager.getInstance().httpServer.start();
    }

    public final void exit(){
        HttpServerManager.getInstance().executor.shutdown();
        HttpServerManager.getInstance().httpServer.stop(0);
    }


    public static void main(String[] args) throws IOException{
        HttpServerManager.init();
    }


}
