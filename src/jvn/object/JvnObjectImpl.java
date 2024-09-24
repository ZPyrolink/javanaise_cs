package jvn.object;

import jvn.utils.JvnException;
import jvn.server.JvnServerImpl;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {
    public JvnObjectImpl(Serializable o, JvnServerImpl jvnServer) {

    }

    @Override
    public void jvnLockRead() throws JvnException {
    }

    @Override
    public void jvnLockWrite() throws JvnException {

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
        return null;
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
