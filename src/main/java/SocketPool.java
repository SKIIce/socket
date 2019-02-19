

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketPool {
    public int defaultCount = 5;
    public static ConcurrentHashMap<Integer,SocketInfo> socketMap = new ConcurrentHashMap<>();
    private static SocketPool instance = new SocketPool();
    private SocketPool(){}
    public static SocketPool getInstance(){
        if(instance == null){
            synchronized(SocketPool.class){
                if(instance == null){
                    instance = new SocketPool();
                }
            }
        }
        return instance;
    }
    static{
        instance.initSocket(true);
    }

    public static boolean checkHeartBeats(SocketInfo socketInfo){
        boolean flag = false;
        OutputStream outputStream = null;
        try {
            Socket socket = socketInfo.getSocket();
            String msg = "HeartBeat";
            outputStream = socket.getOutputStream();
            byte[] sendBytes = msg.getBytes("UTF-8");
            outputStream.write(sendBytes);
            outputStream.flush();
            outputStream.write("#end#".getBytes("UTF-8"));
            outputStream.flush();

            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                String mes = new String(bytes, 0, len, "UTF-8");
                if(sb.toString().endsWith("#end#")){
                    break;
                }
                sb.append(mes);
            }
            msg = sb.toString();
            flag = msg.equals("true");

            SocketPool.getInstance().destorySocket(socketInfo);

        }catch (Exception ex){
            return false;
        }
        return flag;
    }

    /**
     * 初始化socket连接池
     * @param isAllReInit
     */
    public void initSocket(boolean isAllReInit) {
        for(int i = 0; i < defaultCount; i++){
            if(isAllReInit){
                socketMap.put(i,setSocketInfo(i,true,false));
            }else{
                if(socketMap.get(i) == null || socketMap.get(i).isClosed()){
                    socketMap.put(i,setSocketInfo(i,true,false));
                }
            }
        }
        new CheckSocketThread().start();
    }

    /**
     * 为连接池创建socket
     * @param key
     * @param isFree
     * @param isClosed
     * @return
     */
    private static SocketInfo setSocketInfo(Integer key, boolean isFree, boolean isClosed){
        SocketInfo socketInfo = new SocketInfo();
        Socket socket = createSocket();
        socketInfo.setFree(isFree);
        socketInfo.setSocket(socket);
        socketInfo.setSocketID(key);
        socketInfo.setClosed(isClosed);
        return socketInfo;
    }

    public SocketInfo getSocketInfo(){
        SocketInfo socketInfo = null;
        if(socketMap.size() < defaultCount){
            initSocket(false);
        }

        if(socketMap.size() > 0){
            for(Map.Entry<Integer,SocketInfo> entry : socketMap.entrySet()){
                socketInfo = entry.getValue();
                if(socketInfo.isFree() && !socketInfo.getSocket().isClosed()){
                    socketInfo.setFree(false);
                    return socketInfo;
                }
            }
        }else{
            return null;
        }

        socketInfo = setSocketInfo(-1,true,true);
        return socketInfo;
    }

    /**
     * 创建socket连接
     * @return
     */
    private static Socket createSocket() {
        String ip = "127.0.0.1";
        int port = 5533;
        Socket socket = null;
        try{
            socket = new Socket(ip, port);
        }catch (Exception e){
            System.out.println("第一次创建连接失败，将再次尝试连接。");
            try{
                socket = new Socket(ip, port);
            }catch(Exception ex){
                System.out.println("第二次创建连接失败。");
                return null;
            }
        }
        return socket;
    }

    public static void destorySocket(Integer socketId){
        SocketInfo socketInfo = socketMap.get(socketId);
        socketInfo.setFree(true);
    }

    public static void destorySocket(SocketInfo socketInfo){
        if(socketInfo == null)return;
        if(!socketInfo.isClosed()){
            destorySocket(socketInfo.getSocketID());
            return;
        }

        try {
            if(socketInfo.getSocket() != null){
                socketInfo.getSocket().close();
            }
        } catch (IOException e) {
        }
        socketInfo = null;
    }

    class CheckSocketThread extends Thread{
        @Override
        public void run(){
            while(true){
                if(socketMap.size() < defaultCount){
                    initSocket(false);
                }

                for(Map.Entry<Integer, SocketInfo> entry : socketMap.entrySet()){
                    SocketInfo socketInfo = entry.getValue();
                    if(socketInfo.getSocket() == null || socketInfo.isClosed()){
                        socketInfo.setSocket(createSocket());
                    }else{
                        if(socketInfo.isFree()){
                            // 发送心跳检测socket是否可用
                            boolean flag = SocketPool.getInstance().checkHeartBeats(socketInfo);
                            if(!flag){
                                socketInfo.setSocket(createSocket());
                                continue;
                            }
                        }
                    }
                }

                try{
                    sleep(1000);
                }catch(Exception e){

                }
            }
        }

    }
}
