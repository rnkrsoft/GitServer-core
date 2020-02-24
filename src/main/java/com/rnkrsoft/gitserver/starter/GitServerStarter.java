package com.rnkrsoft.gitserver.starter;

import com.rnkrsoft.gitserver.GitServer;
import com.rnkrsoft.gitserver.GitServerFactory;
import com.rnkrsoft.gitserver.GitServerSetting;
import com.rnkrsoft.gitserver.exception.UninitializedGitServerException;
import com.rnkrsoft.gitserver.http.loader.DefaultFileLoader;

public class GitServerStarter {
    public static void main(String[] args) throws UninitializedGitServerException, InterruptedException {
        GitServer gitServer = GitServerFactory.getInstance();
        gitServer.init(GitServerSetting.builder()
                .sshPort(8022)
                .httpPort(8080)
                .repositoriesHome("./repositories")
                .fileLoader(new DefaultFileLoader())
                .build())
                .startup()
                .await();
    }
}
