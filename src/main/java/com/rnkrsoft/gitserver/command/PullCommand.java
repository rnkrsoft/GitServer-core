package com.rnkrsoft.gitserver.command;

import java.io.IOException;


import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.command.AbstractGitCommand;
import com.rnkrsoft.gitserver.command.CommandConstants;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.transport.UploadPack;

public final class PullCommand extends AbstractGitCommand {
	private final static String MSG_REPOSITORY_PERMISSIONS = "Don't have permissions to PULL from this repository.\r\n";
	
	public PullCommand(String name, GitServer gitServer) {
		super(name, gitServer);
	}
	
	@Override
	protected void run() throws IOException {
		if(!gitServer.hasPermission(name, session.getUsername(), "pull")) {
			err.write(MSG_REPOSITORY_PERMISSIONS.getBytes());
			err.flush();
			onExit(CommandConstants.CODE_OK, MSG_REPOSITORY_PERMISSIONS);
			return;
		}

		Config config = new Config();
		int timeout = 30;
		PackConfig packConfig = new PackConfig();
		packConfig.setDeltaCompress(false);
		packConfig.setThreads(2);
		packConfig.fromConfig(config);

		final UploadPack up = new UploadPack(repo);
		up.setPackConfig(packConfig);
		up.setTimeout(timeout);
		up.upload(in, out, err);
	}
}
