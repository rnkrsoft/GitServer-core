package com.rnkrsoft.gitserver.http.loader;

import java.io.IOException;
import java.io.InputStream;

public class DefaultFileLoader implements FileLoader {
    @Override
    public InputStream load(String fileName) throws IOException {
        char[] chars = fileName.toCharArray();
        return FileLoader.class.getClassLoader().getResourceAsStream(WEB_ROOT + (chars[0] == '/' ? "" : "/") + fileName);
    }
}
