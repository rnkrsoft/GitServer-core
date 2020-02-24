package com.rnkrsoft.gitserver.http.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rnkrsoft on 2020/02/24.
 * 默认文件加载器
 */
public class DefaultFileLoader implements FileLoader {
    @Override
    public InputStream load(String fileName) throws IOException {
        char[] chars = fileName.toCharArray();
        InputStream is =  FileLoader.class.getClassLoader().getResourceAsStream(WEB_ROOT + (chars[0] == '/' ? "" : "/") + fileName);
        if (is == null){
            throw new FileNotFoundException(fileName + " is not found!");
        }
        return is;
    }
}
