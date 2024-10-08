/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:
 *
 * Authors:
 */

package jvn.coord;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jvn.object.JvnObject;
import jvn.server.JvnRemoteServer;
import jvn.utils.JvnException;
import java.io.Serializable;

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {

    private static final long serialVersionUID = 1L;


    private int id = 0;

    private Map<String, Integer> nameMap = new HashMap<>();

    private Map<Integer, JvnObject> objectMap = new HashMap<>();

    private transient Map<Integer, JvnRemoteServer> writerMap = new HashMap<>();


    private transient Map<Integer, ArrayList<JvnRemoteServer>> readerMap = new HashMap<>();

    // Registry for communication
    public static final String COORD_NAME = "coordinator";
    public static final int COORD_PORT = 1099;
    public static final String COORD_HOST = "127.0.0.1";
    public static final String PROPERTY = "java.rmi.server.hostname";

    /**
     * Default constructor
     *
     * @throws RemoteException
     * @throws AlreadyBoundException
     */
    private JvnCoordImpl() throws RemoteException, AlreadyBoundException {
        Registry registry = LocateRegistry.createRegistry(COORD_PORT);
        registry.bind(COORD_NAME, this);
    }

    public static void main(String[] args) {
        try {
            // Créer le serveur
            System.setProperty(PROPERTY, COORD_HOST);
            JvnCoordImpl coord = new JvnCoordImpl();
            if (coord.isReady()) {
                System.out.println("Server ready");
            } else {
                throw new JvnException(JvnCoordImpl.class.getName() + " is not ready");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isReady() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(COORD_PORT);
        return registry.lookup(COORD_NAME) != null;
    }

    @Override
    public int jvnGetObjectId() throws RemoteException, jvn.utils.JvnException {
        return ++this.id;
    }

    @Override
    public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws RemoteException, JvnException {
        int id = jo.jvnGetObjectId();

        this.nameMap.put(jon, id);
        this.objectMap.put(id, jo);
        this.writerMap.put(id, js);
        this.readerMap.put(id, new ArrayList<>());
    }

    @Override
    public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws RemoteException, JvnException {
        return this.objectMap.get(this.nameMap.get(jon));
    }

    @Override
    public Serializable jvnLockRead(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
        JvnObject jo = this.objectMap.get(joi);
        Serializable serializable = jo.jvnGetSharedObject();
        JvnRemoteServer writer = this.writerMap.get(joi);

        if (writer != null && !writer.equals(js)) {
            serializable = writer.jvnInvalidateWriterForReader(joi);
            this.writerMap.put(joi, null);
            this.readerMap.get(joi).add(writer);


            jo.jvnSetSharedObject(serializable);
        }

        this.readerMap.get(joi).add(js);
        return serializable;
    }

    @Override
    public Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
        JvnObject jo = this.objectMap.get(joi);
        Serializable serializable = jo.jvnGetSharedObject();
        JvnRemoteServer writer = this.writerMap.get(joi);


        if (writer != null && (!writer.equals(js))) {

            serializable = writer.jvnInvalidateWriter(joi);
            jo.jvnSetSharedObject(serializable);

        }

        for (JvnRemoteServer reader : this.readerMap.get(joi)) {
            if (!reader.equals(js))
                reader.jvnInvalidateReader(joi);
        }

        this.readerMap.get(joi).clear();
        this.writerMap.put(joi, js);
        return serializable;
    }

    @Override
    public void jvnTerminate(JvnRemoteServer js) throws RemoteException, JvnException {
        for (var entry : this.writerMap.entrySet()) {
            JvnRemoteServer writer = entry.getValue();
            if (writer != null && (writer.equals(js))) {
                int joi = entry.getKey();
                JvnObject jo = this.objectMap.get(joi);
                Serializable serializable = writer.jvnInvalidateWriter(joi);
                jo.jvnSetSharedObject(serializable);
                this.writerMap.put(joi, null);
            }
        }

        this.readerMap.forEach((id, readers) -> readers.remove(js));
    }
}