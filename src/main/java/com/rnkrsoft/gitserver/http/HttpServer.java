package com.rnkrsoft.gitserver.http;

import com.rnkrsoft.gitserver.http.loader.FileLoader;
import com.rnkrsoft.log.Logger;
import com.rnkrsoft.log.LoggerFactory;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by woate on 2020/02/24.
 */
public class HttpServer extends NanoHTTPD {
    public static final String LOGIN = "/login.do";
    public static final String LOGOUT = "/logout.do";
    public static final String AJAX = "/ajax.do";
    /**
     * 二进制流类型
     */
    final static String OCTET_STREAM_MIME_TYPE = "application/octet-stream";
    /**
     * 超文本类型
     */
    final static String HTML_MIME_TYPE = "text/html";

    Logger logger = LoggerFactory.getInstance();
    //根目录
    private static final String REQUEST_ROOT = "/";
    FileLoader fileLoader;

    public HttpServer(int serverPort, FileLoader fileLoader) {
        super(serverPort);
        this.fileLoader = fileLoader;
    }

    protected Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (LOGIN.equals(uri)){
            return login(session);
        }else if (LOGOUT.equals(uri)){
            return logout(session);
        }else if (AJAX.equals(uri)){
            return ajax(session);
        }else{
            return staticResource(session, uri);
        }
    }

    public Response login(IHTTPSession session){

        try {
            Map parms = new HashMap();
            session.parseBody(parms);
            System.out.println(parms);

            Map<String, List<String>> parameters = session.getParameters();
            System.out.println(parameters);
            System.out.println(session.getParms());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }
        return Response.newFixedLengthResponse("login success!");
    }

    public Response logout(IHTTPSession session){
        return Response.newFixedLengthResponse("logout success!");
    }

    public Response ajax(IHTTPSession session){
        try {
           String str =  IOUtils.toString(session.getInputStream(), "UTF-8");
            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.newFixedLengthResponse("ajax success!");
    }
    public Response staticResource(IHTTPSession session, String uri){
        logger.debug("access:{}", uri);
        String filename = uri.substring(1);

        if (uri.equals(REQUEST_ROOT)) {
            filename = "index.html";
        }
        String suffix = filename.substring(filename.lastIndexOf("." ) + 1);
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