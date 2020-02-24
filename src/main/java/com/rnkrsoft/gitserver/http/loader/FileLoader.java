package com.rnkrsoft.gitserver.http.loader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rnkrsoft on 2020/02/24.
 * 文件加载器，用于将文件路径指定的文件加载读取成输入流
 * 此类的作用通过接口方式进行反转文件的访问，以对同一套代码用于Java平台和安卓平台
 */
public interface FileLoader {
    /**
     * 存放的web资源根目录
     */
    String WEB_ROOT = "webroot";

    /**
     * 通过带有路径的文件名加载在webroot目录下的资源
     * @param fileName 带有路径的文件名
     * @return 输入流
     * @throws IOException 如果文件不存在则抛出异常
     */
    InputStream load(String fileName)throws IOException;
}
