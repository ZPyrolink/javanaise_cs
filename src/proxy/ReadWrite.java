package proxy;

import java.io.Serializable;

public interface ReadWrite<E> extends Serializable {
    @LockRequester(requestType = RequestType.WRITE)
    void write(E value);

    @LockRequester(requestType = RequestType.READ)
    E read();
}
