import lombok.Getter;
import lombok.Setter;

import java.net.Socket;

@Getter
@Setter
public class SocketInfo {
    private Socket socket;
    private boolean isFree;
    private Integer socketID;
    private boolean isClosed;
}
