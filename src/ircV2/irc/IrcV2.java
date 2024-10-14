/***
 * Irc class : simple implementation of a chat using JAVANAISE 
 * Contact: 
 *
 * Authors: 
 */

package ircV2.irc;

import jvn.server.JvnServerImpl;
import proxy.JvnObjectInvocationHandler;
import proxy.ReadWrite;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class IrcV2 {
    public TextArea text;
    public TextField data;
    Frame frame;
    ReadWrite sentence;


    /**
     * main method
     * create a JVN object nammed IRC for representing the Chat application
     **/
    public static void main(String[] argv) {
        try {
            // initialize JVN
            JvnServerImpl js = JvnServerImpl.jvnGetServer();

            // look up the IRC object in the JVN server
            // if not found, create it, and register it in the JVN server
            ReadWrite jo = JvnObjectInvocationHandler.lookup(js, "IRC");

            if (jo == null) {
                jo = JvnObjectInvocationHandler.register(js, new SentenceV2(), "IRC");
            }
            // create the graphical part of the Chat application
            new IrcV2(jo);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("IRC problem : " + e.getMessage());
        }
    }

    /**
     * IRC Constructor
     *
     * @param jo the JVN object representing the Chat
     **/
    public IrcV2(ReadWrite jo) {
        sentence = jo;
        frame = new Frame();
        frame.setLayout(new GridLayout(1, 1));
        text = new TextArea(10, 60);
        text.setEditable(false);
        text.setForeground(Color.red);
        frame.add(text);
        data = new TextField(40);
        frame.add(data);
        Button read_button = new Button("read");
        read_button.addActionListener(new readListenerV2(this));
        frame.add(read_button);
        Button write_button = new Button("write");
        write_button.addActionListener(new writeListenerV2(this));
        frame.add(write_button);
        frame.setSize(545, 201);
        text.setBackground(Color.black);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });
    }
}


/**
 * Internal class to manage user events (read) on the CHAT application
 **/
class readListenerV2 implements ActionListener {
    IrcV2 irc;

    public readListenerV2(IrcV2 i) {
        irc = i;
    }

    /**
     * Management of user events
     **/
    public void actionPerformed(ActionEvent e) {
        // invoke the method
        String s = irc.sentence.read();

        // display the read value
        irc.data.setText(s);
        irc.text.append(s + "\n");
    }
}

/**
 * Internal class to manage user events (write) on the CHAT application
 **/
class writeListenerV2 implements ActionListener {
    IrcV2 irc;

    public writeListenerV2(IrcV2 i) {
        irc = i;
    }

    /**
     * Management of user events
     **/
    public void actionPerformed(ActionEvent e) {
        // get the value to be written from the buffer
        String s = irc.data.getText();

        // invoke the method
        irc.sentence.write(s);
    }
}



