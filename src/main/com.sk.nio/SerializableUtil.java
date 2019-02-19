import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializableUtil {
    /**
     * 将对象进行序列化操作
     * @param object
     * @return
     */
    public static byte[] toBytes(Object object){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try{
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        }catch(Exception e){
            return null;
        }finally {
            try {
                oos.close();
            }catch(Exception ex){

            }
        }
    }

    /**
     * 反序列化对象
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes){
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try{
            ois = new ObjectInputStream(bais);
            Object object = ois.readObject();
            return object;
        }catch(Exception e){
            return null;
        }finally {
            try{
                ois.close();
            }catch(Exception ex){}
        }
    }
}
