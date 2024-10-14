package test;

import jvn.server.JvnServerImpl;
import jvn.utils.JvnException;
import proxy.JvnObjectInvocationHandler;
import proxy.ReadWrite;

import java.util.Random;

public class Test {
    public static void main(String[] args) {
        JvnServerImpl server = JvnServerImpl.jvnGetServer();

        try {
            ReadWrite<Integer> data = JvnObjectInvocationHandler.lookup(
                    server,
                    "test"
            );

            if (data == null)
                data = JvnObjectInvocationHandler.register(
                        server,
                        new Counter(),
                        "test"
                );

            Random r = new Random();

            for (int i = 0; i < Integer.MAX_VALUE / 8; i++) {
                System.out.println("Writing " + i);
                data.write(i);

                Thread.sleep(r.nextLong(1, 1_000));

                int res = data.read();
                System.out.println("Reading " + res);
            }
        } catch (JvnException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}