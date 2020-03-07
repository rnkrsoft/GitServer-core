package com.rnkrsoft.gitserver.command;

import com.rnkrsoft.gitserver.GitServer;
import org.eclipse.jgit.transport.ReceivePack;

import java.io.IOException;

/**
 * Created by woate on 2020/02/24.
 * Receives change upload over SSH using the Git receive-pack protocol.
 */
public final class PushCommand extends AbstractGitCommand {
    private final static String MSG_REPOSITORY_PERMISSIONS = "message: Don't have permissions to PUSH in this repository.please make sure registered!\r\n";

    public PushCommand(String name, GitServer gitServer) {
        super(name, gitServer);
    }

    @Override
    protected void run() throws IOException {
//        if (!gitPermissionService.hasPermission(name, session.getUsername(), "push")) {
//            err.write(MSG_REPOSITORY_PERMISSIONS.getBytes());
//            err.flush();
//            onExit(CommandConstants.CODE_ERROR, MSG_REPOSITORY_PERMISSIONS);
//            return;
//        }

        int timeout = 30;
        final ReceivePack rp = new ReceivePack(repo);
        rp.setTimeout(timeout);
        rp.receive(in, out, err);
    }
}
