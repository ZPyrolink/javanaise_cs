package proxy;

import jvn.object.JvnObject;
import jvn.server.JvnLocalServer;
import jvn.utils.JvnException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JvnObjectInvocationHandler implements InvocationHandler {
    private JvnObject jvnObject;

    public JvnObjectInvocationHandler(JvnObject jvnObject) {
        this.jvnObject = jvnObject;
    }

    public static Object newInstance(JvnObject obj) throws JvnException {
        try {
            return Proxy.newProxyInstance(
                    obj.jvnGetSharedObject().getClass().getClassLoader(),
                    obj.jvnGetSharedObject().getClass().getInterfaces(),
                    new JvnObjectInvocationHandler(obj));
        } catch (Exception e) {
            e.printStackTrace();
            throw new JvnException("Erreur lors de la création du proxy");
        }
    }

    public static ReadWrite lookup(JvnLocalServer server, String jon) throws JvnException {
        JvnObject result = server.jvnLookupObject(jon);
        return result == null ? null : (ReadWrite) newInstance(result);
    }

    public static ReadWrite register(JvnLocalServer js, ReadWrite jos, String jon) throws JvnException {
        JvnObject jo = js.jvnCreateObject(jos);
        // after creation, I have a write lock on the object
        jo.jvnUnLock();
        js.jvnRegisterObject(jon, jo);
        return (ReadWrite) newInstance(jo);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LockRequester requester = method.getAnnotation(LockRequester.class);

        if (requester == null)
            return method.invoke(proxy, args);

        switch (requester.requestType()) {
            case READ -> jvnObject.jvnLockRead();
            case WRITE -> jvnObject.jvnLockWrite();
        }

        Object result = method.invoke(jvnObject.jvnGetSharedObject(), args);

        jvnObject.jvnUnLock();

        return result;
    }
}
