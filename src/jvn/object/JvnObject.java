/***
 * JAVANAISE API
 * Contact: 
 *
 * Authors: 
 */

package jvn.object;

import jvn.server.JvnServerImpl;
import jvn.utils.JvnException;

import java.io.Serializable;

/**
 * Interface of a JVN object.
 * A JVN object is used to acquire read/write locks to access a given shared object
 */

public interface JvnObject extends Serializable {
	/* A JvnObject should be serializable in order to be able to transfer 
       a reference to a JVN object remotely */

    /**
     * Get a Read lock on the shared object
     *
     * @throws JvnException
     **/
    public void jvnLockRead() throws JvnException;

    /**
     * Get a Write lock on the object
     *
     * @throws JvnException
     **/
    public void jvnLockWrite() throws JvnException;

    /**
     * Unlock  the object
     *
     * @throws JvnException
     **/
    public void jvnUnLock() throws JvnException;


    /**
     * Get the object identification
     *
     * @throws JvnException
     **/
    public int jvnGetObjectId() throws JvnException;

    /**
     * Get the shared object associated to this JvnObject
     *
     * @throws JvnException
     **/
    public Serializable jvnGetSharedObject() throws JvnException;
    /**
     * Set the shared object associated to this JvnObject
     *
     * @throws JvnException
     **/
    public void jvnSetSharedObject(Serializable serializable) throws JvnException;;

    /**
     * Invalidate the Read lock of the JVN object
     *
     * @throws JvnException
     **/
    public void jvnInvalidateReader()
            throws JvnException;

    /**
     * Invalidate the Write lock of the JVN object
     *
     * @return the current JVN object state
     * @throws JvnException
     **/
    public Serializable jvnInvalidateWriter() throws JvnException;

    /**
     * Reduce the Write lock of the JVN object
     *
     * @return the current JVN object state
     * @throws JvnException
     **/
    public Serializable jvnInvalidateWriterForReader() throws JvnException;


    void jvnSetServer(JvnServerImpl jvnServer);

    void resetState();
}
