package jvn.object;

import jvn.LockState;
import jvn.server.JvnRemoteServer;
import jvn.utils.JvnException;
import jvn.server.JvnServerImpl;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {
    private int id;
    private Serializable cachedValue;
    private JvnServerImpl server;
    private LockState lockState;

    public JvnObjectImpl(Serializable o, JvnServerImpl jvnServer) {
        cachedValue = o;
        server = jvnServer;
        lockState = LockState.NONE;
    }

    @Override
    public void jvnLockRead() throws JvnException {
        cachedValue = server.jvnLockRead(id);
    }

    @Override
    public void jvnLockWrite() throws JvnException {
        cachedValue = server.jvnLockWrite(id);
    }

    @Override
    public void jvnUnLock() throws JvnException {

    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return 0;
    }

    @Override
    public Serializable jvnGetSharedObject() throws JvnException {
        return cachedValue;
    }

    @Override
    public void jvnSetSharedObject(Serializable serializable) throws JvnException {
        cachedValue = serializable;
    }

    @Override
    public void jvnInvalidateReader() throws JvnException {

    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        return null;
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        return null;
    }
}
