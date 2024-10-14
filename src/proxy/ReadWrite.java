package proxy;

import java.io.Serializable;

public interface ReadWrite extends Serializable {
    @LockRequester(requestType = RequestType.WRITE)
    void write(String text);

    @LockRequester(requestType = RequestType.READ)
    String read();
}
