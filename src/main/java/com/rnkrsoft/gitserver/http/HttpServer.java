package com.rnkrsoft.gitserver.http;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer extends NanoHTTPD {
    /**
     * 消息内容类型 映射表
     */
    final static Map<String, String> MIME_TYPES = new HashMap<String, String>();
    /**
     * 二进制流类型
     */
    final static String OCTET_STREAM_MIME_TYPE = "application/octet-stream";
    /**
     * 超文本类型
     */
    final static String HTML_MIME_TYPE = "text/html";

    static {
        MIME_TYPES.put(".txt", "text/plain");
        MIME_TYPES.put(".htm", "text/html");
        MIME_TYPES.put(".html", "text/html");
        MIME_TYPES.put(".gif", "text/gif");
        MIME_TYPES.put(".jpeg", "image/jpeg");
        MIME_TYPES.put(".jpg", "image/jpeg");
        MIME_TYPES.put(".png", "image/png");
        MIME_TYPES.put(".icon", "image/x-icon");
        MIME_TYPES.put(".woff2", "application/x-font-woff2");
        MIME_TYPES.put(".woff", "application/x-font-woff");
        MIME_TYPES.put(".ttf", "application/x-font-truetype");
        MIME_TYPES.put(".svg", "image/svg+xml");
        MIME_TYPES.put(".otf", "application/x-font-opentype");
        MIME_TYPES.put(".eot", "application/vnd.ms-fontobject");
        MIME_TYPES.put(".js", "text/javascript");
        MIME_TYPES.put(".css", "text/css");
    }

    Logger logger = LoggerFactory.getInstance();
    //根目录
    private static final String REQUEST_ROOT = "/";
    FileLoader fileLoader;

    public HttpServer(int serverPort, FileLoader fileLoader) {
        super(serverPort);
        this.fileLoader = fileLoader;
    }

    public Response serve(IHTTPSession session) {
        return responseFile(session);
    }

    /**
     * 处理访问的文件
     * @param session 会话对象
     * @return 应答对象
     */
    public Response responseFile(IHTTPSession session) {
        String uri = session.getUri();
        logger.debug("access:{}", uri);
        String filename = uri.substring(1);

        if (uri.equals(REQUEST_ROOT)) {
            filename = "index.html";
        }
        String suffix = filename.substring(filename.lastIndexOf("."));
        String mimeType = OCTET_STREAM_MIME_TYPE;
        if (suffix != null && !suffix.isEmpty()) {
            String mimeType0 = MIME_TYPES.get(suffix);
            if (mimeType0 != null && !mimeType.isEmpty()) {
                mimeType = mimeType0;
            }
        }

        try {
            InputStream is = fileLoader.load(filename);
            return Response.newFixedLengthResponse(Status.OK, mimeType, is, is.available());
        } catch (FileNotFoundException e) {
            return response500(session, uri);
        } catch (IOException e) {
            return response404(session, uri);
        }
    }
    /**
     * 发生内部错误
     * @param session
     * @param url
     * @return
     */
    public Response response500(IHTTPSession session, String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html>body>");
        builder.append("Sorry,Can't Found" + url + " !");
        builder.append("</body></html>\n");
        return Response.newFixedLengthResponse(Status.INTERNAL_ERROR, HTML_MIME_TYPE, builder.toString());
    }

    /**
     * 应答文件不存在
     * @param session
     * @param url
     * @return
     */
    public Response response404(IHTTPSession session, String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html>body>");
        builder.append("Sorry,Can't Found" + url + " !");
        builder.append("</body></html>\n");
        return Response.newFixedLengthResponse(Status.NOT_FOUND, HTML_MIME_TYPE, builder.toString());
    }
}