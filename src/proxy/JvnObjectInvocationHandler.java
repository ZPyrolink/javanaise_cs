package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import jvn.object.JvnObject;
import jvn.utils.JvnException;

public class JvnObjectInvocationHandler implements InvocationHandler {
    private JvnObject jvnObject;

    public JvnObjectInvocationHandler(JvnObject jvnObject) {
        this.jvnObject = jvnObject;
    }

    public static Object newInstance(JvnObject obj) throws JvnException {
        try {
            Object shared = obj.jvnGetSharedObject();
            return Proxy.newProxyInstance(
                    shared.getClass().getClassLoader(),
                    shared.getClass().getInterfaces(),
                    new JvnObjectInvocationHandler(obj));
        } catch (Exception e) {
            throw new JvnException("Erreur lors de la création du proxy");
        }
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

        Object result = method.invoke(jvnObject, args);

        jvnObject.jvnUnLock();

        return result;
    }
}
