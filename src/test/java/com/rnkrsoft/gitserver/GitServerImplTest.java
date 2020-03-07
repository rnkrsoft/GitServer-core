package com.rnkrsoft.gitserver;

import com.rnkrsoft.gitserver.exception.RepositoryCreateFailureException;
import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;
import com.rnkrsoft.gitserver.http.loader.DefaultFileLoader;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.junit.Test;

import java.io.IOException;

public class GitServerImplTest {
    @Test
    public void start() throws IOException, InterruptedException, UninitializedGitServerException {
        GitServer gitServer = GitServerFactory.getInstance();
        gitServer.init(GitServerSetting.builder().repositoriesHome("./target").sshPort(8022).httpPort(8080).fileLoader(new DefaultFileLoader()).build()).startup();
        Thread.sleep(600 * 1000);
    }
}