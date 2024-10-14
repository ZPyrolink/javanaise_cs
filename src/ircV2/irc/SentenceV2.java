/***
 * Sentence class : used for keeping the text exchanged between users
 * during a chat application
 * Contact: 
 *
 * Authors: 
 */

package ircV2.irc;

import proxy.LockRequester;
import proxy.ReadWrite;
import proxy.RequestType;

public class SentenceV2 implements ReadWrite {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    String data;

    public SentenceV2() {
        data = new String("");
    }

    @Override
    @LockRequester(requestType = RequestType.WRITE)
    public void write(String text) {
        data = text;
    }

    @Override
    @LockRequester(requestType = RequestType.READ)
    public String read() {
        return data;
    }

}