import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MyResponseObject implements Serializable{
    private static final long serialVersionUID = 1L;
    private String name;
    private String value;
    private String msg;
    private byte[] bytes;

    public MyResponseObject(String name, String value){
        this.name = name;
        this.value = value;
        this.bytes = new byte[1024];
        setMsg();
    }

    private void setMsg(){
        this.msg = "{\"data\":\"<?xml version=\\\"1.0\\\" encoding=\\\"GBK\\\"?>\\r\\n<CMBSDKPGK>\\r\\n    <INFO>\\r\\n        <FUNNAM>NTQRYSTY</FUNNAM>\\r\\n        <DATTYP>2</DATTYP>\\r\\n        <LGNNAM>银企直连专用账户9</LGNNAM>\\r\\n    </INFO>\\r\\n    <NTQRYSTYX1>\\r\\n        <BUSCOD>N02031</BUSCOD>\\r\\n        <BGNDAT>20181011</BGNDAT>\\r\\n        <ENDDAT>20181011</ENDDAT>\\r\\n        <YURREF>@KDoep40001</YURREF>\\r\\n    </NTQRYSTYX1>\\r\\n</CMBSDKPGK>\",\"httpHeaders\":\"{\\\"connection\\\":\\\"close\\\",\\\"Content-Type\\\":\\\"text/xml\\\"}\",\"httpMethod\":\"POST\",\"proxyType\":\"http\",\"remoteEncode\":\"GBK\",\"remoteURL\":\"http://172.20.69.61:80/\",\"signPlainText\":\"httphttp://172.20.69.61:80/{\\\"connection\\\":\\\"close\\\",\\\"Content-Type\\\":\\\"text/xml\\\"}POSTGBK<?xml version=\\\"1.0\\\" encoding=\\\"GBK\\\"?>\\r\\n<CMBSDKPGK>\\r\\n    <INFO>\\r\\n        <FUNNAM>NTQRYSTY</FUNNAM>\\r\\n        <DATTYP>2</DATTYP>\\r\\n        <LGNNAM>银企直连专用账户9</LGNNAM>\\r\\n    </INFO>\\r\\n    <NTQRYSTYX1>\\r\\n        <BUSCOD>N02031</BUSCOD>\\r\\n        <BGNDAT>20181011</BGNDAT>\\r\\n        <ENDDAT>20181011</ENDDAT>\\r\\n        <YURREF>@KDoep40001</YURREF>\\r\\n    </NTQRYSTYX1>\\r\\n</CMBSDKPGK>\",\"signedData\":\"MCwCFBrJT/b7Adp5gjfLaF0VusxZlmnNAhRgRustQikyF56D1uu3cg44qI2N8Q==\"}\n ";
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Response [name: " + name  + ", value: " + value + ", bytes: " + bytes.length + ", Msg = " + msg + "]");
        return sb.toString();
    }

}
