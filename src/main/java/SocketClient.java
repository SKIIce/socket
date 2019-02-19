import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketClient {
    public static void main(String[] args) throws Exception{
        String host = "127.0.0.1";
        int port = 5533;
        while(true) {
            for (int i = 0; i < 3; i++) {
                SocketInfo socketInfo = SocketPool.getInstance().getSocketInfo();
                Socket socket = socketInfo.getSocket();//new Socket(host, port);
                OutputStream outputStream = socket.getOutputStream();
                String message = "Hello world!";
                System.out.println("发送的message = " + message);
                byte[] sendBytes = message.getBytes("UTF-8");
                outputStream.write(sendBytes);
                outputStream.write("#end#".getBytes("UTF-8"));
                outputStream.flush();

                InputStream inputStream = socket.getInputStream();
                byte[] bytes = new byte[1024];
                int len;
                StringBuilder sb = new StringBuilder();
                while ((len = inputStream.read(bytes)) != -1) {
                    String str = new String(bytes,0,len,"UTF-8");
                    if(str.endsWith("#end#")){
                        break;
                    }
                    sb.append(str);
                }
                String msg = sb.toString();
                System.out.println("客户端接收到message = " + msg);

                // 使用完后释放
                SocketPool.getInstance().destorySocket(socketInfo);
            }

            try{
                Thread.sleep(10000);
            }catch (Exception e){

            }
        }
    }
}
