/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact:
 *
 * Authors:
 */

package jvn.server;

import jvn.coord.JvnCoordImpl;
import jvn.coord.JvnRemoteCoord;
import jvn.object.JvnObject;
import jvn.object.JvnObjectImpl;
import jvn.utils.JvnException;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;


public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * A JVN server is managed as a singleton
     */
    private static JvnServerImpl js = null;
    private JvnRemoteCoord coordinator = null;
    private Registry registery = null;

    private Integer id ;
    //Hashmap de nos objets intercepté pour notre cache
    private Map<Integer, JvnObject> objects = new HashMap<>();

    /**
     * Server name
     */
    private String name = "";

    /**
     * Default constructor
     *
     * @throws JvnException
     **/
    private JvnServerImpl() throws Exception {
        super();
        // to be completed
        this.registery = LocateRegistry.getRegistry(JvnCoordImpl.COORD_PORT);
        this.coordinator = (JvnRemoteCoord) registery.lookup(JvnCoordImpl.COORD_NAME);
        this.id = this.coordinator.jvnGetObjectId();
        this.name = "server_"+this.id;
        registery.bind(name, this);
    }

    /**
     * Static method allowing an application to get a reference to
     * a JVN server instance
     *
     * @throws JvnException
     **/
    public static JvnServerImpl jvnGetServer() {
        if (js == null) {
            try {
                js = new JvnServerImpl();
            } catch (Exception e) {
                return null;
            }
        }
        return js;
    }

    /**
     * The JVN service is not used anymore
     *
     * @throws JvnException
     **/
    public void jvnTerminate() throws JvnException {
        try {
            coordinator.jvnTerminate(this);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        this.objects.clear();
    }

    /**
     * creation of a JVN object
     *
     * @param o : the JVN object state
     * @throws JvnException
     **/
    public JvnObject jvnCreateObject(Serializable o) throws JvnException {
        try {
            int joi = this.coordinator.jvnGetObjectId();
            JvnObjectImpl jo = new JvnObjectImpl(joi,o, this);
            this.objects.put(joi, jo);
            return jo;
        } catch (RemoteException e) {
            throw new JvnException();
        }
    }

    /**
     * Associate a symbolic name with a JVN object
     *
     * @param jon : the JVN object name
     * @param jo  : the JVN object
     * @throws JvnException
     **/
    public void jvnRegisterObject(String jon, JvnObject jo) throws JvnException {
        // to be completed
        try {
            this.coordinator.jvnRegisterObject(jon, jo, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Provide the reference of a JVN object beeing given its symbolic name
     *
     * @param jon : the JVN object name
     * @return the JVN object
     * @throws JvnException
     **/
    public JvnObject jvnLookupObject(String jon) throws JvnException {
        try {
            JvnObject object = this.coordinator.jvnLookupObject(jon, this);
            if (object != null) {

                object.jvnSetServer(this);
                object.resetState();
                this.objects.put(object.jvnGetObjectId(), object);
            }
            return object;
        } catch (RemoteException e) {
            throw new JvnException();
        }
    }
    /**
     * Get a Read lock on a JVN object
     *
     * @param joi : the JVN object identification
     * @return the current JVN object state
     * @throws JvnException
     **/
    public Serializable jvnLockRead(int joi) throws JvnException {
        // to be completed
        try {
            Serializable updatedState = coordinator.jvnLockRead(joi, this);

            // Mettre à jour l'objet dans notre cache (HashMap `objects`)
            JvnObject obj = objects.get(joi);
            if (obj != null) {
                obj.jvnSetSharedObject(updatedState);  // Mettre à jour l'état de l'objet
            }
            // Retourner l'état mis à jour de l'objet
            return updatedState;
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get a Write lock on a JVN object
     *
     * @param joi : the JVN object identification
     * @return the current JVN object state
     * @throws JvnException
     **/
    public Serializable jvnLockWrite(int joi) throws JvnException {
        try {
            Serializable updatedState = coordinator.jvnLockWrite(joi, this);

            // Mettre à jour l'objet dans notre cache (HashMap `objects`)
            JvnObject obj = objects.get(joi);
            // Retourner l'état mis à jour de l'objet
            return updatedState;
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Invalidate the Read lock of the JVN object identified by id
     * called by the JvnCoord
     *
     * @param joi : the JVN object id
     * @return void
     * @throws java.rmi.RemoteException,JvnException
     **/
    public void jvnInvalidateReader(int joi) throws java.rmi.RemoteException, JvnException {
        this.objects.get(joi).jvnInvalidateReader();
    }

    /**
     * Invalidate the Write lock of the JVN object identified by id
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     * @throws java.rmi.RemoteException,JvnException
     **/
    public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException, JvnException {
        return this.objects.get(joi).jvnInvalidateWriter();
    }

    /**
     * Reduce the Write lock of the JVN object identified by id
     *
     * @param joi : the JVN object id
     * @return the current JVN object state
     * @throws java.rmi.RemoteException,JvnException
     **/
    public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException, JvnException {
        // to be completed
        return this.objects.get(joi).jvnInvalidateWriterForReader();
    }

}

 
