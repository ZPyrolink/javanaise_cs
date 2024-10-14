package jvn.object;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
        System.out.println("Interception de la méthode : " + method.getName());

        // Exemple de gestion du cache avant d'appeler la méthode réelle
        // if (method.getName().equals("someCachedMethod")) { ... }

        // Appel de la méthode réelle sur l'objet
        Object result = method.invoke(jvnObject, args);

        // Logique post-traitement si nécessaire
        System.out.println("Appel de la méthode terminé : " + method.getName());

        return result;
    }
}
