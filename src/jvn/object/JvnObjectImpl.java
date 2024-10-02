package jvn.object;

import jvn.LockState;
import jvn.server.JvnServerImpl;
import jvn.utils.JvnException;

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
        switch (lockState) {
            case NONE -> {
                cachedValue = server.jvnLockRead(id);
                lockState = LockState.READING;
            }
            case READ_CACHED -> lockState = LockState.READING;
            case WRITE_CACHED -> lockState = LockState.READ_WRITE_CACHED;
        }
    }

    @Override
    public void jvnLockWrite() throws JvnException {
        switch (lockState) {
            case NONE -> {
                cachedValue = server.jvnLockWrite(id);
                lockState = LockState.WRITING;
            }
            case WRITE_CACHED -> lockState = LockState.WRITING;
            case READ_CACHED -> {
                cachedValue = server.jvnLockWrite(id);
                lockState = LockState.READ_WRITE_CACHED;
            }
        }
    }

    @Override
    public void jvnUnLock() throws JvnException {
        lockState = switch (lockState) {
            case NONE, READ_WRITE_CACHED -> lockState;
            case READ_CACHED, READING -> LockState.READ_CACHED;
            case WRITE_CACHED, WRITING -> LockState.WRITE_CACHED;
        };

        notifyAll();
    }

    @Override
    public int jvnGetObjectId() throws JvnException {
        return id;
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
        while (lockState == LockState.READING) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new JvnException();
            }
        }

        lockState = LockState.NONE;
    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        while (lockState == LockState.WRITING) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new JvnException();
            }
        }

        lockState = LockState.NONE;
        return cachedValue;
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        switch (lockState) {
            case WRITING -> {
                while (lockState == LockState.WRITING) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                lockState = LockState.READ_CACHED;
            }
            case WRITE_CACHED, READ_WRITE_CACHED -> lockState = LockState.READ_CACHED;
        }
        return cachedValue;
    }
}
