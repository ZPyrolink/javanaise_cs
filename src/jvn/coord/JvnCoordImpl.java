/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn.coord;

import jvn.utils.JvnException;
import jvn.object.JvnObject;
import jvn.server.JvnRemoteServer;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final String COORD_NAME = "coordinator";
    public static final int COORD_PORT = 1099;
    public static final String COORD_HOST = "127.0.0.1";



    /**
     * Default constructor
     *
     * @throws JvnException
     **/
    private JvnCoordImpl() throws Exception {
        // to be completed
        Registry registry = LocateRegistry.createRegistry(COORD_PORT);
        registry.bind(COORD_NAME, this);
    }

    /**
     * Allocate a NEW JVN object id (usually allocated to a
     * newly created JVN object)
     *
     * @throws java.rmi.RemoteException,JvnException
     **/
    public int jvnGetObjectId() throws java.rmi.RemoteException, JvnException {
        // to be completed
        return 0;
    }

    /**
     * Associate a symbolic name with a JVN object
     *
     * @param jon : the JVN object name
     * @param jo  : the JVN object
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        // to be completed
        // TODO assign server to map
    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     *
     * @param jon : the JVN object name
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        // to be completed
        return null;
    }

    /**
     * Get a Read lock on a JVN object managed by a given JVN server
     *
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     **/
    public Serializable jvnLockRead(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        // to be completed
        // TODO Invalidation du verrou d'écriture
        // TODO Récuperer l'objet mis à jour via javaObject.jvnGetSharedObject(serializable) avec la version à jour envoyée par l'écrivain.
        // TODO Le verrou de lecture est accordé une fois que l'objet est à jour.
        // TODO return l'objet à jour au serveur qui à demandé le lock
        return null;
    }

    /**
     * Get a Write lock on a JVN object managed by a given JVN server
     *
     * @param joi : the JVN object identification
     * @param js  : the remote reference of the server
     * @return the current JVN object state
     * @throws java.rmi.RemoteException, JvnException
     **/
    public Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        // to be completed
        return null;
    }

    /**
     * A JVN server terminates
     *
     * @param js : the remote reference of the server
     * @throws java.rmi.RemoteException, JvnException
     **/
    public void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        // to be completed
    }
}

 
