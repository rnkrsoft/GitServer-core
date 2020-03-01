package com.rnkrsoft.gitserver.command;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.SessionAware;
import org.apache.sshd.server.session.ServerSession;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by woate on 2020/02/24.
 */
public abstract class BaseCommand implements Command, SessionAware {


    protected InputStream in;
    protected OutputStream out;
    protected OutputStream err;
    protected ServerSession session;

    private ExitCallback exit;

    public void setInputStream(final InputStream in) {
        this.in = in;
    }

    public void setOutputStream(final OutputStream out) {
        this.out = out;
    }

    public void setErrorStream(final OutputStream err) {
        this.err = err;
    }

    public void setExitCallback(final ExitCallback callback) {
        this.exit = callback;
    }

    public void destroy() {
        exit.onExit(CommandConstants.CODE_OK);
    }

    protected void startThread(final Runnable thunk) {
        new Thread(thunk, "SSH Worker").start();
    }

    /**
     * Terminate this command and return a result code to the remote client.
     * <p>
     * Commands should invoke this at most once. Once invoked, the command may
     * lose access to request based resources as any callbacks previously
     * registered with RequestCleanup will fire.
     *
     * @param requestCleanup exit code for the remote client.
     */
    protected void onExit(final int requestCleanup) {
        exit.onExit(requestCleanup);
    }

    protected void onExit(final int requestCleanup, final String message) {
        exit.onExit(requestCleanup, message);
    }

    @Override
    public void setSession(ServerSession session) {
        this.session = session;
    }
}
