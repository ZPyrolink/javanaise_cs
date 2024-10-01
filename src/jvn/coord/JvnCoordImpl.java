/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn.coord;

import jvn.LockState;
import jvn.object.JvnObject;
import jvn.server.JvnRemoteServer;
import jvn.utils.JvnException;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {
    public static class ObjectState {
        // Unique identifier of the object
        private String name;

        // (Value) of the object
        private JvnObject value;

        // Lock detain by the different servers
        private Map<JvnRemoteServer, LockState> lockStateByServer;

        public JvnObject getValue() {
            return value;
        }

        public void setValue(JvnObject value) {
            this.value = value;
        }

        public void putLockStateByServer(JvnRemoteServer jrs, LockState state) {
            lockStateByServer.put(jrs, state);
        }

        public LockState getLockState(JvnRemoteServer server) {
            return lockStateByServer.get(server);
        }

        public LockState removeLockStateByServer(JvnRemoteServer server) {
            return lockStateByServer.remove(server);
        }

        public ObjectState(String name, JvnObject value, JvnRemoteServer server) {
            this.name = name;
            this.value = value;
            this.lockStateByServer = new HashMap<>();
            lockStateByServer.put(server, LockState.NONE);
        }

        public boolean canReadLock() {
            return lockStateByServer.values().stream().noneMatch(obj -> obj == LockState.WRITING);
        }

        public boolean canWriteLock() {
            return lockStateByServer.values().stream().noneMatch(obj -> obj == LockState.READING);
        }

        public String getName() {
            return name;
        }
    }

    private static final long serialVersionUID = 1L;
    public static final String COORD_NAME = "coordinator";
    public static final int COORD_PORT = 1099;
    public static final String COORD_HOST = "127.0.0.1";

    public HashMap<Integer, ObjectState> states;

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
        return states.isEmpty() ? 0 : states.keySet().stream().max(Comparator.naturalOrder()).get() + 1;
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
        states.put(jvnGetObjectId(), new ObjectState(jon, jo, js));
    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     *
     * @param jon : the JVN object name
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        ObjectState objState = null;

        for (ObjectState obj : states.values()) {
            if (obj.name.equals(jon)) {
                objState = obj;
                break;
            }
        }

        if (objState == null)
            throw new JvnException("The '" + jon + "' JVN object doesn't exists");

        LockState lockState = objState.getLockState(js);

//        while (!lockState.canRead()) {
//            // ToDo: wait
//            // ToDo: notify ?
//        }

        return objState.getValue();
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
        ObjectState state = states.get(joi);

        if (state == null)
            throw new JvnException();

        JvnObject object = state.getValue();

        Serializable serializable = object.jvnGetSharedObject();

        // ToDo
        object.jvnInvalidateWriterForReader();

//        while (!state.canReadLock()) {
//            // ToDo: wait
//            // ToDo: notify ?
//        }
        state.putLockStateByServer(js, LockState.READ);

        return serializable;
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
        ObjectState state = states.get(joi);

        if (state == null)
            throw new JvnException();

        JvnObject object = state.getValue();

        Serializable serializable = object.jvnGetSharedObject();

        // ToDo
        object.jvnInvalidateWriter();
        object.jvnInvalidateReader();

        state.putLockStateByServer(js, LockState.READ);


//        while (!state.canReadLock()) {
//            // ToDo: wait
//            // ToDo: notify ?
//        }

        return serializable;
    }

    /**
     * A JVN server terminates
     *
     * @param js : the remote reference of the server
     * @throws java.rmi.RemoteException, JvnException
     **/
    public void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        states.values().forEach(state -> state.removeLockStateByServer(js));
    }
}

 
