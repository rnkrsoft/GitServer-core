package com.rnkrsoft.gitserver;

public final class GitServerFactory {
    private static GitServer SERVER;

    /**
     * 获取Git服务
     * @return Git服务
     */
    public static GitServer getInstance(){
        if (SERVER != null){
            return SERVER;
        }

        synchronized (GitServerFactory.class){
            if (SERVER != null){
                return SERVER;
            }
            SERVER = new GitServerImpl();
            return SERVER;
        }
    }
}
