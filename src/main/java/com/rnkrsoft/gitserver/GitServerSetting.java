package com.rnkrsoft.gitserver;

import com.rnkrsoft.gitserver.http.loader.FileLoader;

public final class GitServerSetting {
    private String repositoriesHome;
    private int sshPort;
    private int httpPort;
    private FileLoader fileLoader;

    public String getRepositoriesHome() {
        return repositoriesHome;
    }

    public int getSshPort() {
        return sshPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public FileLoader getFileLoader() {
        return fileLoader;
    }

    public static GitServerSettingBuilder builder(){
        return new GitServerSettingBuilder();
    }
    public static class GitServerSettingBuilder{
        private String repositoriesHome;
        private int sshPort;
        private int httpPort;
        private FileLoader fileLoader;
        private GitServerSettingBuilder() {
        }

        public GitServerSettingBuilder repositoriesHome(String repositoriesHome) {
            this.repositoriesHome = repositoriesHome;
            return this;
        }

        public GitServerSettingBuilder sshPort(int sshPort) {
            this.sshPort = sshPort;
            return this;
        }

        public GitServerSettingBuilder httpPort(int httpPort) {
            this.httpPort = httpPort;
            return this;
        }

        public GitServerSettingBuilder fileLoader(FileLoader fileLoader) {
            this.fileLoader = fileLoader;
            return this;
        }

        public GitServerSetting build(){
            GitServerSetting setting = new GitServerSetting();
            setting.repositoriesHome = repositoriesHome;
            setting.sshPort = sshPort;
            setting.httpPort = httpPort;
            setting.fileLoader = fileLoader;
            return setting;
        }
    }
}
