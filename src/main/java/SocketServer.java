import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    public static void main(String[] args) throws Exception{
        int port = 5533;
        ServerSocket server = new ServerSocket(port);
        server.setReuseAddress(true);

        // 使用线程池用于管理多线程
        ExecutorService threadPool = Executors.newFixedThreadPool(100);

        while(true){

            Socket socket = server.accept();
            socket.setReuseAddress(true);
           // Runnable runnable = ()->{
              try{
                  InputStream inputStream = socket.getInputStream();
                  byte[] bytes = new byte[1024];
                  int len;
                  StringBuilder sb = new StringBuilder();
                  while((len = inputStream.read(bytes)) != -1){
                      String str = new String(bytes,0,len,"UTF-8");
                      if(str.endsWith("#end#")){
                          break;
                      }
                      sb.append(str);
                  }
                  String msg = sb.toString();
                  System.out.println("获取到message = " + msg);

                  OutputStream outputStream = socket.getOutputStream();
                  String message = "服务端 收到消息";
                  if(msg.equals("HeartBeat")) {
                      message = "true";
                  }
                  byte[] sendBytes = message.getBytes("UTF-8");
                  outputStream.write(sendBytes);
                  outputStream.write("#end#".getBytes("UTF-8"));
                  outputStream.flush();

//                  outputStream.close();
//                  inputStream.close();
//                  socket.close();
              }catch(Exception ex){
                  System.out.println("获取消息出现异常：" + ex.getMessage());
              }
            //};

           // threadPool.submit(runnable);
        }
    }
}
