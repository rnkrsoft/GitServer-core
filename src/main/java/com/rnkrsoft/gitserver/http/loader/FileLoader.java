package com.rnkrsoft.gitserver.http.loader;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件加载器，用于将文件路径指定的文件加载读取成输入流
 */
public interface FileLoader {
    String WEB_ROOT = "webroot";

    /**
     * 通过带有路径的文件名加载在webroot目录下的资源
     * @param fileName 带有路径的文件名
     * @return 输入流
     * @throws IOException 如果文件不存在则抛出异常
     */
    InputStream load(String fileName)throws IOException;
}
