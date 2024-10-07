/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn.coord;

import jvn.object.JvnObject;
import jvn.server.JvnRemoteServer;
import jvn.utils.JvnException;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

class ObjectState {
    // (Value) of the object
    @Getter
    private JvnObject value;

    // Lock detain by the different servers
//        private Map<JvnRemoteServer, LockState> lockStateByServer;

    private List<JvnRemoteServer> readers;

    public void addReader(JvnRemoteServer r) {
        readers.add(r);
    }

    public boolean removeReader(JvnRemoteServer r) {
        return readers.remove(r);
    }

    public void forEachReaders(Consumer<JvnRemoteServer> action) {
        readers.forEach(action);
    }

    @Getter
    @Setter
    private JvnRemoteServer writer;

//        public void putLockStateByServer(JvnRemoteServer jrs, LockState state) {
//            lockStateByServer.put(jrs, state);
//        }
//
//        public LockState getLockState(JvnRemoteServer server) {
//            return lockStateByServer.get(server);
//        }
//
//        public LockState removeLockStateByServer(JvnRemoteServer server) {
//            return lockStateByServer.remove(server);
//        }

    public ObjectState(JvnObject value, JvnRemoteServer server) {
        this.value = value;
//            this.lockStateByServer = new HashMap<>();
//            lockStateByServer.put(server, LockState.NONE);
        readers = new ArrayList<>();
        writer = null;
    }

//        public boolean canReadLock() {
//            return lockStateByServer.values().stream().noneMatch(obj -> obj == LockState.WRITING);
//        }
//
//        public boolean canWriteLock() {
//            return lockStateByServer.values().stream().noneMatch(obj -> obj == LockState.READING);
//        }

    public void terminate(JvnRemoteServer jr) {
        removeReader(jr);
        if (writer == jr)
            writer = null;
    }
}

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {
    private static final long serialVersionUID = 1L;
    public static final String COORD_NAME = "coordinator";
    public static final int COORD_PORT = 1099;
    public static final String COORD_HOST = "127.0.0.1";

    private static int JVN_OBJECT_ID = 0;

    private Map<String, ObjectState> states;

    /**
     * Default constructor
     *
     * @throws JvnException
     **/
    private JvnCoordImpl() throws Exception {
        // to be completed
        Registry registry = LocateRegistry.createRegistry(COORD_PORT);
        registry.bind(COORD_NAME, this);

        states = new HashMap<>();
    }

    public static void main(String[] args) {
        try {
            new JvnCoordImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Allocate a NEW JVN object id (usually allocated to a
     * newly created JVN object)
     *
     * @throws java.rmi.RemoteException,JvnException
     **/
    public int jvnGetObjectId() throws java.rmi.RemoteException, JvnException {
        return JVN_OBJECT_ID++;
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
        if (!states.containsKey(jon))
            states.put(jon, new ObjectState(jo, js));
    }

    /**
     * Get the reference of a JVN object managed by a given JVN server
     *
     * @param jon : the JVN object name
     * @param js  : the remote reference of the JVNServer
     * @throws java.rmi.RemoteException,JvnException
     **/
    public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        return Optional.ofNullable(states.get(jon))
                .orElseThrow(() -> new JvnException("The '" + jon + "' JVN object doesn't exists"))
                .getValue();
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
        Serializable result = object.jvnGetSharedObject();

        if (state.getWriter() != null) {
            result = object.jvnInvalidateWriterForReader();
            object.jvnSetSharedObject(result);
            state.setWriter(null);
        }

        state.addReader(js);
        return result;
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
        Serializable result = object.jvnGetSharedObject();

        if (state.getWriter() != null) {
            result = object.jvnInvalidateWriter();
            object.jvnSetSharedObject(result);
            state.setWriter(null);
        }

        state.forEachReaders(server -> {
            if (server != js) {
                try {
                    server.jvnInvalidateReader(joi);
                } catch (RemoteException | JvnException e) {
                    throw new RuntimeException(e);
                }
                state.removeReader(js);
            }
        });

        state.setWriter(js);
        return result;
    }

    /**
     * A JVN server terminates
     *
     * @param js : the remote reference of the server
     * @throws java.rmi.RemoteException, JvnException
     **/
    public void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
        states.values().forEach(state -> state.terminate(js));
    }
}

 
