/***
 * JAVANAISE API
 * JvnRemoteServer interface
 * Defines the remote interface provided by a JVN server 
 * This interface is intended to be invoked by the Javanaise coordinator 
 * Contact: 
 *
 * Authors: 
 */

package jvn.server;

import jvn.utils.JvnException;

import java.io.Serializable;
import java.rmi.Remote;


/**
 * Remote interface of a JVN server (used by a remote JvnCoord)
 */

public interface JvnRemoteServer extends Remote {

    /**
     * Invalidate the Read lock of a JVN object
     *
     * @param joi : the JVN object id
     * @throws java.rmi.RemoteException,JvnException
     **/
    public void jvnInvalidateReader(int joi) throws java.rmi.RemoteException, JvnException;

    /**
     * Invalidate the Write lock of a JVN object
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     * @throws java.rmi.RemoteException,JvnException
     **/
    public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException, JvnException;

    /**
     * Reduce the Write lock of a JVN object
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     * @throws java.rmi.RemoteException,JvnException
     **/
    public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException, JvnException;

}

 
