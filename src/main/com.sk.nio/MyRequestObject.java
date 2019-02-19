import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MyRequestObject implements Serializable{
    private static final long serialVersionUID = 1L;
    private String name;
    private String value;
    private String msg;
    private byte[] bytes;

    public MyRequestObject(String name, String value){
        this.name = name;
        this.value = value;
        this.bytes = new byte[1024];
        setMsg();
    }

    private void setMsg(){
        this.msg = "{\"data\":\"<?xml version=\\\"1.0\\\" encoding=\\\"gbk\\\"?>\\n<CMBSDKPGK>\\n    <INFO>\\n        <DATTYP>2</DATTYP>\\n        <ERRMSG></ERRMSG>\\n        <FUNNAM>NTQRYSTY</FUNNAM>\\n        <LGNNAM>银企直连专用账户9</LGNNAM>\\n        <RETCOD>0</RETCOD>\\n    </INFO>\\n</CMBSDKPGK>\",\"exception\":false,\"signature\":\"MCwCFDjoafN2zgkEvQZseP0LLvPZ7pK5AhRQq1+X4eng1EdzXig7MjLn/dYtuw==\"}\n";
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("Request [name = " + name +", value = " + value + ", bytes length = " + bytes.length + ", Msg = " + msg + "]");
        return sb.toString();
    }

}
