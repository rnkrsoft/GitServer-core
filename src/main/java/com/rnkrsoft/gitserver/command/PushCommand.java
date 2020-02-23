package com.rnkrsoft.gitserver.command;

import java.io.IOException;


import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.command.AbstractGitCommand;
import com.rnkrsoft.gitserver.command.CommandConstants;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.transport.ReceivePack;

/**
 * Receives change upload over SSH using the Git receive-pack protocol.
 */
public final class PushCommand extends AbstractGitCommand {
	private final static String MSG_REPOSITORY_PERMISSIONS = "Don't have permissions to PUSH in this repository.\r\n";
	
	public PushCommand(String name, GitServer gitServer) {
		super(name, gitServer);
	}
	
	@Override
	protected void run() throws IOException {
		if(!gitServer.hasPermission(name, session.getUsername(), "push")) {
			err.write(MSG_REPOSITORY_PERMISSIONS.getBytes());
			err.flush();
			onExit(CommandConstants.CODE_ERROR, MSG_REPOSITORY_PERMISSIONS);
			return;
		}
		
		int timeout = 30;
		final ReceivePack rp = new ReceivePack(repo);
		rp.setTimeout(timeout);
		rp.receive(in, out, err);
	}
}
