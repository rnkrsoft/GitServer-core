package com.rnkrsoft.gitserver.http.mgr;

import com.rnkrsoft.gitserver.http.loader.FileLoader;
import com.rnkrsoft.gitserver.http.nanohttpd.protocols.http.IHTTPSession;
import com.rnkrsoft.gitserver.http.nanohttpd.protocols.http.NanoHTTPD;
import com.rnkrsoft.gitserver.http.nanohttpd.protocols.http.response.Response;
import com.rnkrsoft.gitserver.http.nanohttpd.protocols.http.response.Status;
import com.rnkrsoft.gitserver.log.Logger;
import com.rnkrsoft.gitserver.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by XDA on 2019/4/2.
 */
public class FileServer extends NanoHTTPD {
    Logger logger = LoggerFactory.getInstance();
    //根目录
    private static final String REQUEST_ROOT = "/";
    FileLoader fileLoader;

    public FileServer(int serverPort, FileLoader fileLoader) {
        super(serverPort);
        this.fileLoader = fileLoader;
    }

    //当接受到连接时会调用此方法
    public Response serve(IHTTPSession session) {
        return responseFile(session);
    }

    //对于请求文件的，返回下载的文件
    public Response responseFile(IHTTPSession session) {
        String uri = session.getUri();
        logger.debug("access:{}", uri);
        String filename = uri.substring(1);

        if (uri.equals(REQUEST_ROOT)) {
            filename = "index.html";
        }
        boolean is_ascii = true;
        String mimetype;
        if (filename.contains(".html") || filename.contains(".htm")) {
            mimetype = "text/html";
        } else if (filename.contains(".js")) {
            mimetype = "text/javascript";
        } else if (filename.contains(".css")) {
            mimetype = "text/css";
        } else if (filename.contains(".gif")) {
            mimetype = "text/gif";
            is_ascii = false;
        } else if (filename.contains(".jpeg") || filename.contains(".jpg")) {
            mimetype = "text/jpeg";
            is_ascii = false;
        } else if (filename.contains(".png")) {
            mimetype = "image/png";
            is_ascii = false;
        } else if (filename.contains(".woff2")) {
            mimetype = "application / font-woff";
            is_ascii = false;
        } else {
            filename = "index.html";
            mimetype = "text/html";
        }

        if (is_ascii) {
            String response = "";
            String line = "";
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(fileLoader.load(filename)));

                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                reader.close();
            } catch (IOException e) {
                return response404(session, uri);
            }
            return Response.newFixedLengthResponse(Status.OK, mimetype, response);
        } else {
            try {
                InputStream isr = fileLoader.load(filename);
                return Response.newFixedLengthResponse(Status.OK, mimetype, isr, isr.available());
            } catch (IOException e) {
                return response404(session, uri);
            }
        }
    }

    //页面不存在，或者文件不存在时
    public Response response404(IHTTPSession session, String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html>body>");
        builder.append("Sorry,Can't Found" + url + " !");
        builder.append("</body></html>\n");
        return Response.newFixedLengthResponse(builder.toString());
    }
}