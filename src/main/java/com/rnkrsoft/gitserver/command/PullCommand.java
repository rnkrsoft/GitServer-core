package com.rnkrsoft.gitserver.command;

import com.rnkrsoft.gitserver.GitServer;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.transport.UploadPack;

import java.io.IOException;

/**
 * Created by woate on 2020/02/24.
 */
public final class PullCommand extends AbstractGitCommand {
    private final static String MSG_REPOSITORY_PERMISSIONS = "message: Don't have permissions to PULL from this repository.please make sure registered!\r\n";

    public PullCommand(String name, GitServer gitServer) {
        super(name, gitServer);
    }

    @Override
    protected void run() throws IOException {
//        if (!gitPermissionService.hasPermission(name, session.getUsername(), "pull")) {
//            err.write(MSG_REPOSITORY_PERMISSIONS.getBytes());
//            err.flush();
//            onExit(CommandConstants.CODE_ERROR, MSG_REPOSITORY_PERMISSIONS);
//            return;
//        }

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
