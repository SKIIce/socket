import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class MyServer {
    private final static Logger logger = Logger.getLogger(MyServer.class.getName());

    public static void main(String[] args){
        Selector selector = null;
        ServerSocketChannel serverSocketChannel = null;
        try{
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.socket().bind(new InetSocketAddress(5534));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while(selector.select() > 0){
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while(it.hasNext()){
                    SelectionKey readtKey = it.next();
                    it.remove();
                    execute((ServerSocketChannel)readtKey.channel());
                }
            }
        }catch(Exception e){

        }finally {
            try{
                selector.close();
            }catch(Exception ex){ }
        }
    }

    private static void execute(ServerSocketChannel serverSocketChannel) throws IOException{
        SocketChannel socketChannel = null;
        try{
            socketChannel = serverSocketChannel.accept();
            MyRequestObject myRequestObject = receiveData(socketChannel);
            logger.log(Level.INFO,myRequestObject.toString());
            MyResponseObject myResponseObject = new MyResponseObject(
                    "response for " + myRequestObject.getName(),
                    "response for " + myRequestObject.getValue());
            sendData(socketChannel, myResponseObject);
            logger.log(Level.INFO, myResponseObject.toString());
        }catch(Exception e){ }
    }

    /**
     * 接收客户端的请求
     * @param socketChannel
     * @return
     * @throws IOException
     */
    private static MyRequestObject receiveData(SocketChannel socketChannel) throws IOException{
        MyRequestObject myRequestObject = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try{
            byte[] bytes;
            int size = 0;
            while((size = socketChannel.read(buffer)) >= 0){
                buffer.flip();
                bytes = new byte[size];
                buffer.get(bytes);
                baos.write(bytes);
                buffer.clear();
            }
            bytes = baos.toByteArray();
            Object obj = SerializableUtil.toObject(bytes);
            myRequestObject = (MyRequestObject)obj;
        }catch(Exception e){

        }finally {
            try{
                baos.close();
            }catch(Exception ex){}
        }
        return myRequestObject;
    }

    /**
     * 发送给客户端的响应
     * @param socketChannel
     * @param myResponseObject
     * @throws IOException
     */
    private static void sendData(SocketChannel socketChannel, MyResponseObject myResponseObject) throws IOException{
        byte[] bytes = SerializableUtil.toBytes(myResponseObject);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        socketChannel.write(buffer);
    }
}
