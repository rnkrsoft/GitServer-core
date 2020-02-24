package com.rnkrsoft.gitserver.http;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

/**
 * NanoHTTPD see https://github.com/NanoHttpd/nanohttpd
 */
public abstract class NanoHTTPD {

    public static final String CONTENT_DISPOSITION_REGEX = "([ |\t]*Content-Disposition[ |\t]*:)(.*)";

    public static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile(CONTENT_DISPOSITION_REGEX, Pattern.CASE_INSENSITIVE);

    public static final String CONTENT_TYPE_REGEX = "([ |\t]*content-type[ |\t]*:)(.*)";

    public static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile(CONTENT_TYPE_REGEX, Pattern.CASE_INSENSITIVE);

    public static final String CONTENT_DISPOSITION_ATTRIBUTE_REGEX = "[ |\t]*([a-zA-Z]*)[ |\t]*=[ |\t]*['|\"]([^\"^']*)['|\"]";

    public static final Pattern CONTENT_DISPOSITION_ATTRIBUTE_PATTERN = Pattern.compile(CONTENT_DISPOSITION_ATTRIBUTE_REGEX);

    public static final class ResponseException extends Exception {

        private static final long serialVersionUID = 6569838532917408380L;

        private final Status status;

        public ResponseException(Status status, String message) {
            super(message);
            this.status = status;
        }

        public ResponseException(Status status, String message, Exception e) {
            super(message, e);
            this.status = status;
        }

        public Status getStatus() {
            return this.status;
        }
    }

    /**
     * Maximum time to wait on Socket.getInputStream().read() (in milliseconds)
     * This is required as the Keep-Alive HTTP connections would otherwise block
     * the socket reading thread forever (or as long the browser is open).
     */
    public static final int SOCKET_READ_TIMEOUT = 5000;

    /**
     * Common MIME type for dynamic content: plain text
     */
    public static final String MIME_PLAINTEXT = "text/plain";

    /**
     * Common MIME type for dynamic content: html
     */
    public static final String MIME_HTML = "text/html";

    /**
     * Pseudo-Parameter to use to store the actual query string in the
     * parameters map for later re-processing.
     */
    private static final String QUERY_STRING_PARAMETER = "NanoHttpd.QUERY_STRING";

    /**
     * logger to log to.
     */
    public static final Logger LOG = Logger.getLogger(NanoHTTPD.class.getName());

    /**
     * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
     */
    protected static Map<String, String> MIME_TYPES = new HashMap<String, String>();

    static {
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("gif", "text/gif");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("icon", "image/x-icon");
        MIME_TYPES.put("woff2", "application/x-font-woff2");
        MIME_TYPES.put("woff", "application/x-font-woff");
        MIME_TYPES.put("ttf", "application/x-font-truetype");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("otf", "application/x-font-opentype");
        MIME_TYPES.put("eot", "application/vnd.ms-fontobject");
        MIME_TYPES.put("js", "text/javascript");
        MIME_TYPES.put("css", "text/css");
    }

    public static Map<String, String> mimeTypes() {
        return MIME_TYPES;
    }

    @SuppressWarnings({
            "unchecked",
            "rawtypes"
    })
    private static void loadMimeTypes(Map<String, String> result, String resourceName) {
        try {
            Enumeration<URL> resources = NanoHTTPD.class.getClassLoader().getResources(resourceName);
            while (resources.hasMoreElements()) {
                URL url = (URL) resources.nextElement();
                Properties properties = new Properties();
                InputStream stream = null;
                try {
                    stream = url.openStream();
                    properties.load(stream);
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "could not load mimetypes from " + url, e);
                } finally {
                    safeClose(stream);
                }
                result.putAll((Map) properties);
            }
        } catch (IOException e) {
            LOG.log(Level.INFO, "no mime types available at " + resourceName);
        }
    }

    ;

    /**
     * Creates an SSLSocketFactory for HTTPS. Pass a loaded KeyStore and an
     * array of loaded KeyManagers. These objects must properly
     * loaded/initialized by the caller.
     */
    public static SSLServerSocketFactory makeSSLSocketFactory(KeyStore loadedKeyStore, KeyManager[] keyManagers) throws IOException {
        SSLServerSocketFactory res = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(loadedKeyStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(keyManagers, trustManagerFactory.getTrustManagers(), null);
            res = ctx.getServerSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return res;
    }

    /**
     * Creates an SSLSocketFactory for HTTPS. Pass a loaded KeyStore and a
     * loaded KeyManagerFactory. These objects must properly loaded/initialized
     * by the caller.
     */
    public static SSLServerSocketFactory makeSSLSocketFactory(KeyStore loadedKeyStore, KeyManagerFactory loadedKeyFactory) throws IOException {
        try {
            return makeSSLSocketFactory(loadedKeyStore, loadedKeyFactory.getKeyManagers());
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Creates an SSLSocketFactory for HTTPS. Pass a KeyStore resource with your
     * certificate and passphrase
     */
    public static SSLServerSocketFactory makeSSLSocketFactory(String keyAndTrustStoreClasspathPath, char[] passphrase) throws IOException {
        try {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream keystoreStream = NanoHTTPD.class.getResourceAsStream(keyAndTrustStoreClasspathPath);

            if (keystoreStream == null) {
                throw new IOException("Unable to load keystore from classpath: " + keyAndTrustStoreClasspathPath);
            }

            keystore.load(keystoreStream, passphrase);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, passphrase);
            return makeSSLSocketFactory(keystore, keyManagerFactory);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Get MIME type from file name extension, if possible
     *
     * @param uri the string representing a file
     * @return the connected mime/type
     */
    public static String getMimeTypeForFile(String uri) {
        int dot = uri.lastIndexOf('.');
        String mime = null;
        if (dot >= 0) {
            mime = mimeTypes().get(uri.substring(dot + 1).toLowerCase());
        }
        return mime == null ? "application/octet-stream" : mime;
    }

    public static final void safeClose(Object closeable) {
        try {
            if (closeable != null) {
                if (closeable instanceof Closeable) {
                    ((Closeable) closeable).close();
                } else if (closeable instanceof Socket) {
                    ((Socket) closeable).close();
                } else if (closeable instanceof ServerSocket) {
                    ((ServerSocket) closeable).close();
                } else {
                    throw new IllegalArgumentException("Unknown object to close");
                }
            }
        } catch (IOException e) {
            NanoHTTPD.LOG.log(Level.SEVERE, "Could not close", e);
        }
    }

    public final String hostname;

    public final int myPort;

    private volatile ServerSocket myServerSocket;

    public ServerSocket getMyServerSocket() {
        return myServerSocket;
    }

    private IFactoryThrowing<ServerSocket, IOException> serverSocketFactory = new DefaultServerSocketFactory();

    private Thread myThread;

    private IHandler<IHTTPSession, Response> httpHandler;

    protected List<IHandler<IHTTPSession, Response>> interceptors = new ArrayList<IHandler<IHTTPSession, Response>>(4);

    /**
     * Pluggable strategy for asynchronously executing requests.
     */
    protected IAsyncRunner asyncRunner;

    /**
     * Pluggable strategy for creating and cleaning up temporary files.
     */
    private IFactory<ITempFileManager> tempFileManagerFactory;

    /**
     * Constructs an HTTP server on given port.
     */
    public NanoHTTPD(int port) {
        this(null, port);
    }

    // -------------------------------------------------------------------------------
    // //
    //
    // Threading Strategy.
    //
    // -------------------------------------------------------------------------------
    // //

    /**
     * Constructs an HTTP server on given hostname and port.
     */
    public NanoHTTPD(String hostname, int port) {
        this.hostname = hostname;
        this.myPort = port;
        setTempFileManagerFactory(new DefaultTempFileManagerFactory());
        setAsyncRunner(new DefaultAsyncRunner());

        // creates a default handler that redirects to deprecated serve();
        this.httpHandler = new IHandler<IHTTPSession, Response>() {

            @Override
            public Response handle(IHTTPSession input) {
                return NanoHTTPD.this.serve(input);
            }
        };
    }

    public void setHTTPHandler(IHandler<IHTTPSession, Response> handler) {
        this.httpHandler = handler;
    }

    public void addHTTPInterceptor(IHandler<IHTTPSession, Response> interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * Forcibly closes all connections that are open.
     */
    public synchronized void closeAllConnections() {
        stop();
    }

    /**
     * create a instance of the client handler, subclasses can return a subclass
     * of the ClientHandler.
     *
     * @param finalAccept the socket the cleint is connected to
     * @param inputStream the input stream
     * @return the client handler
     */
    protected ClientHandler createClientHandler(final Socket finalAccept, final InputStream inputStream) {
        return new ClientHandler(this, inputStream, finalAccept);
    }

    /**
     * Instantiate the server runnable, can be overwritten by subclasses to
     * provide a subclass of the ServerRunnable.
     *
     * @param timeout the socet timeout to use.
     * @return the server runnable.
     */
    protected ServerRunnable createServerRunnable(final int timeout) {
        return new ServerRunnable(this, timeout);
    }

    /**
     * Decode parameters from a URL, handing the case where a single parameter
     * name might have been supplied several times, by return lists of values.
     * In general these lists will contain a single element.
     *
     * @param parms original <b>NanoHTTPD</b> parameters values, as passed to the
     *              <code>serve()</code> method.
     * @return a map of <code>String</code> (parameter name) to
     * <code>List&lt;String&gt;</code> (a list of the values supplied).
     */
    protected static Map<String, List<String>> decodeParameters(Map<String, String> parms) {
        return decodeParameters(parms.get(NanoHTTPD.QUERY_STRING_PARAMETER));
    }

    // -------------------------------------------------------------------------------
    // //

    /**
     * Decode parameters from a URL, handing the case where a single parameter
     * name might have been supplied several times, by return lists of values.
     * In general these lists will contain a single element.
     *
     * @param queryString a query string pulled from the URL.
     * @return a map of <code>String</code> (parameter name) to
     * <code>List&lt;String&gt;</code> (a list of the values supplied).
     */
    protected static Map<String, List<String>> decodeParameters(String queryString) {
        Map<String, List<String>> parms = new HashMap<String, List<String>>();
        if (queryString != null) {
            StringTokenizer st = new StringTokenizer(queryString, "&");
            while (st.hasMoreTokens()) {
                String e = st.nextToken();
                int sep = e.indexOf('=');
                String propertyName = sep >= 0 ? decodePercent(e.substring(0, sep)).trim() : decodePercent(e).trim();
                if (!parms.containsKey(propertyName)) {
                    parms.put(propertyName, new ArrayList<String>());
                }
                String propertyValue = sep >= 0 ? decodePercent(e.substring(sep + 1)) : null;
                if (propertyValue != null) {
                    parms.get(propertyName).add(propertyValue);
                }
            }
        }
        return parms;
    }

    /**
     * Decode percent encoded <code>String</code> values.
     *
     * @param str the percent encoded <code>String</code>
     * @return expanded form of the input, for example "foo%20bar" becomes
     * "foo bar"
     */
    public static String decodePercent(String str) {
        String decoded = null;
        try {
            decoded = URLDecoder.decode(str, "UTF8");
        } catch (UnsupportedEncodingException ignored) {
            NanoHTTPD.LOG.log(Level.WARNING, "Encoding not supported, ignored", ignored);
        }
        return decoded;
    }

    public final int getListeningPort() {
        return this.myServerSocket == null ? -1 : this.myServerSocket.getLocalPort();
    }

    public final boolean isAlive() {
        return wasStarted() && !this.myServerSocket.isClosed() && this.myThread.isAlive();
    }

    public IFactoryThrowing<ServerSocket, IOException> getServerSocketFactory() {
        return serverSocketFactory;
    }

    public void setServerSocketFactory(IFactoryThrowing<ServerSocket, IOException> serverSocketFactory) {
        this.serverSocketFactory = serverSocketFactory;
    }

    public String getHostname() {
        return hostname;
    }

    public IFactory<ITempFileManager> getTempFileManagerFactory() {
        return tempFileManagerFactory;
    }

    /**
     * Call before start() to serve over HTTPS instead of HTTP
     */
    public void makeSecure(SSLServerSocketFactory sslServerSocketFactory, String[] sslProtocols) {
        this.serverSocketFactory = new SecureServerSocketFactory(sslServerSocketFactory, sslProtocols);
    }

    /**
     * This is the "master" method that delegates requests to handlers and makes
     * sure there is a response to every request. You are not supposed to call
     * or override this method in any circumstances. But no one will stop you if
     * you do. I'm a Javadoc, not Code Police.
     *
     * @param session the incoming session
     * @return a response to the incoming session
     */
    public Response handle(IHTTPSession session) {
        for (IHandler<IHTTPSession, Response> interceptor : interceptors) {
            Response response = interceptor.handle(session);
            if (response != null)
                return response;
        }
        return httpHandler.handle(session);
    }

    /**
     * Override this to customize the server.
     * <p/>
     * <p/>
     * (By default, this returns a 404 "Not Found" plain text error response.)
     *
     * @param session The HTTP session
     * @return HTTP response, see class Response for details
     */
    protected abstract Response serve(IHTTPSession session);

    /**
     * Pluggable strategy for asynchronously executing requests.
     *
     * @param asyncRunner new strategy for handling threads.
     */
    public void setAsyncRunner(IAsyncRunner asyncRunner) {
        this.asyncRunner = asyncRunner;
    }

    /**
     * Pluggable strategy for creating and cleaning up temporary files.
     *
     * @param tempFileManagerFactory new strategy for handling temp files.
     */
    public void setTempFileManagerFactory(IFactory<ITempFileManager> tempFileManagerFactory) {
        this.tempFileManagerFactory = tempFileManagerFactory;
    }

    /**
     * Start the server.
     *
     * @throws IOException if the socket is in use.
     */
    public void start() throws IOException {
        start(NanoHTTPD.SOCKET_READ_TIMEOUT);
    }

    /**
     * Starts the server (in setDaemon(true) mode).
     */
    public void start(final int timeout) throws IOException {
        start(timeout, true);
    }

    /**
     * Start the server.
     *
     * @param timeout timeout to use for socket connections.
     * @param daemon  start the thread daemon or not.
     * @throws IOException if the socket is in use.
     */
    public void start(final int timeout, boolean daemon) throws IOException {
        this.myServerSocket = this.getServerSocketFactory().create();
        this.myServerSocket.setReuseAddress(true);

        ServerRunnable serverRunnable = createServerRunnable(timeout);
        this.myThread = new Thread(serverRunnable);
        this.myThread.setDaemon(daemon);
        this.myThread.setName("NanoHttpd Main Listener");
        this.myThread.start();
        while (!serverRunnable.hasBinded() && serverRunnable.getBindException() == null) {
            try {
                Thread.sleep(10L);
            } catch (Throwable e) {
                // on android this may not be allowed, that's why we
                // catch throwable the wait should be very short because we are
                // just waiting for the bind of the socket
            }
        }
        if (serverRunnable.getBindException() != null) {
            throw serverRunnable.getBindException();
        }
    }

    /**
     * Stop the server.
     */
    public void stop() {
        try {
            safeClose(this.myServerSocket);
            this.asyncRunner.closeAll();
            if (this.myThread != null) {
                this.myThread.join();
            }
        } catch (Exception e) {
            NanoHTTPD.LOG.log(Level.SEVERE, "Could not stop all connections", e);
        }
    }

    public final boolean wasStarted() {
        return this.myServerSocket != null && this.myThread != null;
    }

    public static class ContentType {

        private static final String ASCII_ENCODING = "US-ASCII";

        private static final String MULTIPART_FORM_DATA_HEADER = "multipart/form-data";

        private static final String CONTENT_REGEX = "[ |\t]*([^/^ ^;^,]+/[^ ^;^,]+)";

        private static final Pattern MIME_PATTERN = Pattern.compile(CONTENT_REGEX, Pattern.CASE_INSENSITIVE);

        private static final String CHARSET_REGEX = "[ |\t]*(charset)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;^,]*)['|\"]?";

        private static final Pattern CHARSET_PATTERN = Pattern.compile(CHARSET_REGEX, Pattern.CASE_INSENSITIVE);

        private static final String BOUNDARY_REGEX = "[ |\t]*(boundary)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;^,]*)['|\"]?";

        private static final Pattern BOUNDARY_PATTERN = Pattern.compile(BOUNDARY_REGEX, Pattern.CASE_INSENSITIVE);

        private final String contentTypeHeader;

        private final String contentType;

        private final String encoding;

        private final String boundary;

        public ContentType(String contentTypeHeader) {
            this.contentTypeHeader = contentTypeHeader;
            if (contentTypeHeader != null) {
                contentType = getDetailFromContentHeader(contentTypeHeader, MIME_PATTERN, "", 1);
                encoding = getDetailFromContentHeader(contentTypeHeader, CHARSET_PATTERN, null, 2);
            } else {
                contentType = "";
                encoding = "UTF-8";
            }
            if (MULTIPART_FORM_DATA_HEADER.equalsIgnoreCase(contentType)) {
                boundary = getDetailFromContentHeader(contentTypeHeader, BOUNDARY_PATTERN, null, 2);
            } else {
                boundary = null;
            }
        }

        private String getDetailFromContentHeader(String contentTypeHeader, Pattern pattern, String defaultValue, int group) {
            Matcher matcher = pattern.matcher(contentTypeHeader);
            return matcher.find() ? matcher.group(group) : defaultValue;
        }

        public String getContentTypeHeader() {
            return contentTypeHeader;
        }

        public String getContentType() {
            return contentType;
        }

        public String getEncoding() {
            return encoding == null ? ASCII_ENCODING : encoding;
        }

        public String getBoundary() {
            return boundary;
        }

        public boolean isMultipart() {
            return MULTIPART_FORM_DATA_HEADER.equalsIgnoreCase(contentType);
        }

        public ContentType tryUTF8() {
            if (encoding == null) {
                return new ContentType(this.contentTypeHeader + "; charset=UTF-8");
            }
            return this;
        }
    }

    /**
     * A simple cookie representation. This is old code and is flawed in many ways.
     *
     * @author LordFokas
     */
    public static class Cookie {

        public static String getHTTPTime(int days) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendar.add(Calendar.DAY_OF_MONTH, days);
            return dateFormat.format(calendar.getTime());
        }

        private final String n, v, e;

        public Cookie(String name, String value) {
            this(name, value, 30);
        }

        public Cookie(String name, String value, int numDays) {
            this.n = name;
            this.v = value;
            this.e = getHTTPTime(numDays);
        }

        public Cookie(String name, String value, String expires) {
            this.n = name;
            this.v = value;
            this.e = expires;
        }

        public String getHTTPHeader() {
            String fmt = "%s=%s; expires=%s";
            return String.format(fmt, this.n, this.v, this.e);
        }
    }

    /**
     * Provides rudimentary support for cookies. Doesn't support 'path', 'secure'
     * nor 'httpOnly'. Feel free to improve it and/or add unsupported features. This
     * is old code and it's flawed in many ways.
     *
     * @author LordFokas
     */
    public static class CookieHandler implements Iterable<String> {

        private final HashMap<String, String> cookies = new HashMap<String, String>();

        private final ArrayList<Cookie> queue = new ArrayList<Cookie>();

        public CookieHandler(Map<String, String> httpHeaders) {
            String raw = httpHeaders.get("cookie");
            if (raw != null) {
                String[] tokens = raw.split(";");
                for (String token : tokens) {
                    String[] data = token.trim().split("=");
                    if (data.length == 2) {
                        this.cookies.put(data[0], data[1]);
                    }
                }
            }
        }

        /**
         * Set a cookie with an expiration date from a month ago, effectively
         * deleting it on the client side.
         *
         * @param name The cookie name.
         */
        public void delete(String name) {
            set(name, "-delete-", -30);
        }

        @Override
        public Iterator<String> iterator() {
            return this.cookies.keySet().iterator();
        }

        /**
         * Read a cookie from the HTTP Headers.
         *
         * @param name The cookie's name.
         * @return The cookie's value if it exists, null otherwise.
         */
        public String read(String name) {
            return this.cookies.get(name);
        }

        public void set(Cookie cookie) {
            this.queue.add(cookie);
        }

        /**
         * Sets a cookie.
         *
         * @param name    The cookie's name.
         * @param value   The cookie's value.
         * @param expires How many days until the cookie expires.
         */
        public void set(String name, String value, int expires) {
            this.queue.add(new Cookie(name, value, Cookie.getHTTPTime(expires)));
        }

        /**
         * Internally used by the webserver to add all queued cookies into the
         * Response's HTTP Headers.
         *
         * @param response The Response object to which headers the queued cookies will
         *                 be added.
         */
        public void unloadQueue(Response response) {
            for (Cookie cookie : this.queue) {
                response.addCookieHeader(cookie.getHTTPHeader());
            }
        }
    }

    /**
     * HTTP Request methods, with the ability to decode a <code>String</code> back
     * to its enum value.
     */
    public static enum Method {
        GET,
        PUT,
        POST,
        DELETE,
        HEAD,
        OPTIONS,
        TRACE,
        CONNECT,
        PATCH,
        PROPFIND,
        PROPPATCH,
        MKCOL,
        MOVE,
        COPY,
        LOCK,
        UNLOCK;

        public static Method lookup(String method) {
            if (method == null)
                return null;

            try {
                return valueOf(method);
            } catch (IllegalArgumentException e) {
                // TODO: Log it?
                return null;
            }
        }
    }

    /**
     * Output stream that will automatically send every write to the wrapped
     * OutputStream according to chunked transfer:
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.6.1
     */
    public static class ChunkedOutputStream extends FilterOutputStream {

        public ChunkedOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(int b) throws IOException {
            byte[] data = {
                    (byte) b
            };
            write(data, 0, 1);
        }

        @Override
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (len == 0)
                return;
            out.write(String.format("%x\r\n", len).getBytes());
            out.write(b, off, len);
            out.write("\r\n".getBytes());
        }

        public void finish() throws IOException {
            out.write("0\r\n\r\n".getBytes());
        }
    }

    public static interface IStatus {

        String getDescription();

        int getRequestStatus();
    }

    /**
     * Some HTTP response status codes
     */
    public static enum Status implements IStatus {
        SWITCH_PROTOCOL(101, "Switching Protocols"),

        OK(200, "OK"),
        CREATED(201, "Created"),
        ACCEPTED(202, "Accepted"),
        NO_CONTENT(204, "No Content"),
        PARTIAL_CONTENT(206, "Partial Content"),
        MULTI_STATUS(207, "Multi-Status"),

        REDIRECT(301, "Moved Permanently"),
        /**
         * Many user agents mishandle 302 in ways that violate the RFC1945 spec
         * (i.e., redirect a POST to a GET). 303 and 307 were added in RFC2616 to
         * address this. You should prefer 303 and 307 unless the calling user agent
         * does not support 303 and 307 functionality
         */
        @Deprecated
        FOUND(302, "Found"),
        REDIRECT_SEE_OTHER(303, "See Other"),
        NOT_MODIFIED(304, "Not Modified"),
        TEMPORARY_REDIRECT(307, "Temporary Redirect"),

        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
        NOT_ACCEPTABLE(406, "Not Acceptable"),
        REQUEST_TIMEOUT(408, "Request Timeout"),
        CONFLICT(409, "Conflict"),
        GONE(410, "Gone"),
        LENGTH_REQUIRED(411, "Length Required"),
        PRECONDITION_FAILED(412, "Precondition Failed"),
        PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
        RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
        EXPECTATION_FAILED(417, "Expectation Failed"),
        TOO_MANY_REQUESTS(429, "Too Many Requests"),

        INTERNAL_ERROR(500, "Internal Server Error"),
        NOT_IMPLEMENTED(501, "Not Implemented"),
        SERVICE_UNAVAILABLE(503, "Service Unavailable"),
        UNSUPPORTED_HTTP_VERSION(505, "HTTP Version Not Supported");

        private final int requestStatus;

        private final String description;

        Status(int requestStatus, String description) {
            this.requestStatus = requestStatus;
            this.description = description;
        }

        public static Status lookup(int requestStatus) {
            for (Status status : Status.values()) {
                if (status.getRequestStatus() == requestStatus) {
                    return status;
                }
            }
            return null;
        }

        @Override
        public String getDescription() {
            return "" + this.requestStatus + " " + this.description;
        }

        @Override
        public int getRequestStatus() {
            return this.requestStatus;
        }
    }

    /**
     * HTTP response. Return one of these from serve().
     */
    public static class Response implements Closeable {

        /**
         * HTTP status code after processing, e.g. "200 OK", Status.OK
         */
        private IStatus status;

        /**
         * MIME type of content, e.g. "text/html"
         */
        private String mimeType;

        /**
         * Data of the response, may be null.
         */
        private InputStream data;

        private long contentLength;

        /**
         * Headers for the HTTP response. Use addHeader() to add lines. the
         * lowercase map is automatically kept up to date.
         */
        @SuppressWarnings("serial")
        private final Map<String, String> header = new HashMap<String, String>() {

            public String put(String key, String value) {
                lowerCaseHeader.put(key == null ? key : key.toLowerCase(), value);
                return super.put(key, value);
            }

            ;
        };

        /**
         * copy of the header map with all the keys lowercase for faster searching.
         */
        private final Map<String, String> lowerCaseHeader = new HashMap<String, String>();

        /**
         * The request method that spawned this response.
         */
        private Method requestMethod;

        /**
         * Use chunkedTransfer
         */
        private boolean chunkedTransfer;

        private boolean encodeAsGzip;

        private boolean keepAlive;

        private List<String> cookieHeaders;

        private Response.GzipUsage gzipUsage = Response.GzipUsage.DEFAULT;

        enum GzipUsage {
            DEFAULT,
            ALWAYS,
            NEVER;
        }

        /**
         * Creates a fixed length response if totalBytes>=0, otherwise chunked.
         */
        @SuppressWarnings({
                "rawtypes",
                "unchecked"
        })
        protected Response(IStatus status, String mimeType, InputStream data, long totalBytes) {
            this.status = status;
            this.mimeType = mimeType;
            if (data == null) {
                this.data = new ByteArrayInputStream(new byte[0]);
                this.contentLength = 0L;
            } else {
                this.data = data;
                this.contentLength = totalBytes;
            }
            this.chunkedTransfer = this.contentLength < 0;
            this.keepAlive = true;
            this.cookieHeaders = new ArrayList(10);
        }

        @Override
        public void close() throws IOException {
            if (this.data != null) {
                this.data.close();
            }
        }

        /**
         * Adds a cookie header to the list. Should not be called manually, this is
         * an internal utility.
         */
        public void addCookieHeader(String cookie) {
            cookieHeaders.add(cookie);
        }

        /**
         * Should not be called manually. This is an internally utility for JUnit
         * test purposes.
         *
         * @return All unloaded cookie headers.
         */
        public List<String> getCookieHeaders() {
            return cookieHeaders;
        }

        /**
         * Adds given line to the header.
         */
        public void addHeader(String name, String value) {
            this.header.put(name, value);
        }

        /**
         * Indicate to close the connection after the Response has been sent.
         *
         * @param close {@code true} to hint connection closing, {@code false} to let
         *              connection be closed by client.
         */
        public void closeConnection(boolean close) {
            if (close)
                this.header.put("connection", "close");
            else
                this.header.remove("connection");
        }

        /**
         * @return {@code true} if connection is to be closed after this Response
         * has been sent.
         */
        public boolean isCloseConnection() {
            return "close".equals(getHeader("connection"));
        }

        public InputStream getData() {
            return this.data;
        }

        public String getHeader(String name) {
            return this.lowerCaseHeader.get(name.toLowerCase());
        }

        public String getMimeType() {
            return this.mimeType;
        }

        public Method getRequestMethod() {
            return this.requestMethod;
        }

        public IStatus getStatus() {
            return this.status;
        }

        public void setGzipEncoding(boolean encodeAsGzip) {
            this.encodeAsGzip = encodeAsGzip;
        }

        public void setKeepAlive(boolean useKeepAlive) {
            this.keepAlive = useKeepAlive;
        }

        /**
         * Sends given response to the socket.
         */
        public void send(OutputStream outputStream) {
            SimpleDateFormat gmtFrmt = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));

            try {
                if (this.status == null) {
                    throw new Error("sendResponse(): Status can't be null.");
                }
                PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, new ContentType(this.mimeType).getEncoding())), false);
                pw.append("HTTP/1.1 ").append(this.status.getDescription()).append(" \r\n");
                if (this.mimeType != null) {
                    printHeader(pw, "Content-Type", this.mimeType);
                }
                if (getHeader("date") == null) {
                    printHeader(pw, "Date", gmtFrmt.format(new Date()));
                }
                for (Map.Entry<String, String> entry : this.header.entrySet()) {
                    printHeader(pw, entry.getKey(), entry.getValue());
                }
                for (String cookieHeader : this.cookieHeaders) {
                    printHeader(pw, "Set-Cookie", cookieHeader);
                }
                if (getHeader("connection") == null) {
                    printHeader(pw, "Connection", (this.keepAlive ? "keep-alive" : "close"));
                }
                if (getHeader("content-length") != null) {
                    encodeAsGzip = false;
                }
                if (encodeAsGzip) {
                    printHeader(pw, "Content-Encoding", "gzip");
                    setChunkedTransfer(true);
                }
                long pending = this.data != null ? this.contentLength : 0;
                if (this.requestMethod != Method.HEAD && this.chunkedTransfer) {
                    printHeader(pw, "Transfer-Encoding", "chunked");
                } else if (!encodeAsGzip) {
                    pending = sendContentLengthHeaderIfNotAlreadyPresent(pw, pending);
                }
                pw.append("\r\n");
                pw.flush();
                sendBodyWithCorrectTransferAndEncoding(outputStream, pending);
                outputStream.flush();
                NanoHTTPD.safeClose(this.data);
            } catch (IOException ioe) {
                NanoHTTPD.LOG.log(Level.SEVERE, "Could not send response to the client", ioe);
            }
        }

        @SuppressWarnings("static-method")
        protected void printHeader(PrintWriter pw, String key, String value) {
            pw.append(key).append(": ").append(value).append("\r\n");
        }

        protected long sendContentLengthHeaderIfNotAlreadyPresent(PrintWriter pw, long defaultSize) {
            String contentLengthString = getHeader("content-length");
            long size = defaultSize;
            if (contentLengthString != null) {
                try {
                    size = Long.parseLong(contentLengthString);
                } catch (NumberFormatException ex) {
                    NanoHTTPD.LOG.severe("content-length was no number " + contentLengthString);
                }
            }
            pw.print("Content-Length: " + size + "\r\n");
            return size;
        }

        private void sendBodyWithCorrectTransferAndEncoding(OutputStream outputStream, long pending) throws IOException {
            if (this.requestMethod != Method.HEAD && this.chunkedTransfer) {
                ChunkedOutputStream chunkedOutputStream = new ChunkedOutputStream(outputStream);
                sendBodyWithCorrectEncoding(chunkedOutputStream, -1);
                chunkedOutputStream.finish();
            } else {
                sendBodyWithCorrectEncoding(outputStream, pending);
            }
        }

        private void sendBodyWithCorrectEncoding(OutputStream outputStream, long pending) throws IOException {
            if (encodeAsGzip) {
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
                sendBody(gzipOutputStream, -1);
                gzipOutputStream.finish();
            } else {
                sendBody(outputStream, pending);
            }
        }

        /**
         * Sends the body to the specified OutputStream. The pending parameter
         * limits the maximum amounts of bytes sent unless it is -1, in which case
         * everything is sent.
         *
         * @param outputStream the OutputStream to send data to
         * @param pending      -1 to send everything, otherwise sets a max limit to the
         *                     number of bytes sent
         * @throws IOException if something goes wrong while sending the data.
         */
        private void sendBody(OutputStream outputStream, long pending) throws IOException {
            long BUFFER_SIZE = 16 * 1024;
            byte[] buff = new byte[(int) BUFFER_SIZE];
            boolean sendEverything = pending == -1;
            while (pending > 0 || sendEverything) {
                long bytesToRead = sendEverything ? BUFFER_SIZE : Math.min(pending, BUFFER_SIZE);
                int read = this.data.read(buff, 0, (int) bytesToRead);
                if (read <= 0) {
                    break;
                }
                try {
                    outputStream.write(buff, 0, read);
                } catch (Exception e) {
                    if (this.data != null) {
                        this.data.close();
                    }
                }
                if (!sendEverything) {
                    pending -= read;
                }
            }
        }

        public void setChunkedTransfer(boolean chunkedTransfer) {
            this.chunkedTransfer = chunkedTransfer;
        }

        public void setData(InputStream data) {
            this.data = data;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public void setRequestMethod(Method requestMethod) {
            this.requestMethod = requestMethod;
        }

        public void setStatus(IStatus status) {
            this.status = status;
        }

        /**
         * Create a response with unknown length (using HTTP 1.1 chunking).
         */
        public static Response newChunkedResponse(IStatus status, String mimeType, InputStream data) {
            return new Response(status, mimeType, data, -1);
        }

        public static Response newFixedLengthResponse(IStatus status, String mimeType, byte[] data) {
            return newFixedLengthResponse(status, mimeType, new ByteArrayInputStream(data), data.length);
        }

        /**
         * Create a response with known length.
         */
        public static Response newFixedLengthResponse(IStatus status, String mimeType, InputStream data, long totalBytes) {
            return new Response(status, mimeType, data, totalBytes);
        }

        /**
         * Create a text response with known length.
         */
        public static Response newFixedLengthResponse(IStatus status, String mimeType, String txt) {
            ContentType contentType = new ContentType(mimeType);
            if (txt == null) {
                return newFixedLengthResponse(status, mimeType, new ByteArrayInputStream(new byte[0]), 0);
            } else {
                byte[] bytes;
                try {
                    CharsetEncoder newEncoder = Charset.forName(contentType.getEncoding()).newEncoder();
                    if (!newEncoder.canEncode(txt)) {
                        contentType = contentType.tryUTF8();
                    }
                    bytes = txt.getBytes(contentType.getEncoding());
                } catch (UnsupportedEncodingException e) {
                    NanoHTTPD.LOG.log(Level.SEVERE, "encoding problem, responding nothing", e);
                    bytes = new byte[0];
                }
                return newFixedLengthResponse(status, contentType.getContentTypeHeader(), new ByteArrayInputStream(bytes), bytes.length);
            }
        }

        /**
         * Create a text response with known length.
         */
        public static Response newFixedLengthResponse(String msg) {
            return newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_HTML, msg);
        }

        public Response setUseGzip(boolean useGzip) {
            gzipUsage = useGzip ? Response.GzipUsage.ALWAYS : Response.GzipUsage.NEVER;
            return this;
        }

        // If a Gzip usage has been enforced, use it.
        // Else decide whether or not to use Gzip.
        public boolean useGzipWhenAccepted() {
            if (gzipUsage == Response.GzipUsage.DEFAULT)
                return getMimeType() != null && (getMimeType().toLowerCase().contains("text/") || getMimeType().toLowerCase().contains("/json"));
            else
                return gzipUsage == Response.GzipUsage.ALWAYS;
        }
    }

    /**
     * Creates a normal ServerSocket for TCP connections
     */
    public static class DefaultServerSocketFactory implements IFactoryThrowing<ServerSocket, IOException> {

        @Override
        public ServerSocket create() throws IOException {
            return new ServerSocket();
        }

    }

    /**
     * Creates a new SSLServerSocket
     */
    public static class SecureServerSocketFactory implements IFactoryThrowing<ServerSocket, IOException> {

        private SSLServerSocketFactory sslServerSocketFactory;

        private String[] sslProtocols;

        public SecureServerSocketFactory(SSLServerSocketFactory sslServerSocketFactory, String[] sslProtocols) {
            this.sslServerSocketFactory = sslServerSocketFactory;
            this.sslProtocols = sslProtocols;
        }

        @Override
        public ServerSocket create() throws IOException {
            SSLServerSocket ss = null;
            ss = (SSLServerSocket) this.sslServerSocketFactory.createServerSocket();
            if (this.sslProtocols != null) {
                ss.setEnabledProtocols(this.sslProtocols);
            } else {
                ss.setEnabledProtocols(ss.getSupportedProtocols());
            }
            ss.setUseClientMode(false);
            ss.setWantClientAuth(false);
            ss.setNeedClientAuth(false);
            return ss;
        }

    }

    /**
     * Default strategy for creating and cleaning up temporary files.
     * <p/>
     * <p>
     * By default, files are created by <code>File.createTempFile()</code> in the
     * directory specified.
     * </p>
     */
    public static class DefaultTempFile implements ITempFile {

        private final File file;

        private final OutputStream fstream;

        public DefaultTempFile(File tempdir) throws IOException {
            this.file = File.createTempFile("NanoHTTPD-", "", tempdir);
            this.fstream = new FileOutputStream(this.file);
        }

        @Override
        public void delete() throws Exception {
            NanoHTTPD.safeClose(this.fstream);
            if (!this.file.delete()) {
                throw new Exception("could not delete temporary file: " + this.file.getAbsolutePath());
            }
        }

        @Override
        public String getName() {
            return this.file.getAbsolutePath();
        }

        @Override
        public OutputStream open() throws Exception {
            return this.fstream;
        }
    }

    /**
     * Default strategy for creating and cleaning up temporary files.
     * <p/>
     * <p>
     * This class stores its files in the standard location (that is, wherever
     * <code>java.io.tmpdir</code> points to). Files are added to an internal list,
     * and deleted when no longer needed (that is, when <code>clear()</code> is
     * invoked at the end of processing a request).
     * </p>
     */
    public static class DefaultTempFileManager implements ITempFileManager {

        private final File tmpdir;

        private final List<ITempFile> tempFiles;

        public DefaultTempFileManager() {
            this.tmpdir = new File(System.getProperty("java.io.tmpdir"));
            if (!tmpdir.exists()) {
                tmpdir.mkdirs();
            }
            this.tempFiles = new ArrayList<ITempFile>();
        }

        @Override
        public void clear() {
            for (ITempFile file : this.tempFiles) {
                try {
                    file.delete();
                } catch (Exception ignored) {
                    NanoHTTPD.LOG.log(Level.WARNING, "could not delete file ", ignored);
                }
            }
            this.tempFiles.clear();
        }

        @Override
        public ITempFile createTempFile(String filename_hint) throws Exception {
            DefaultTempFile tempFile = new DefaultTempFile(this.tmpdir);
            this.tempFiles.add(tempFile);
            return tempFile;
        }
    }

    /**
     * Default strategy for creating and cleaning up temporary files.
     */
    public static class DefaultTempFileManagerFactory implements IFactory<ITempFileManager> {

        @Override
        public ITempFileManager create() {
            return new DefaultTempFileManager();
        }
    }

    /**
     * A temp file.
     * <p/>
     * <p>
     * Temp files are responsible for managing the actual temporary storage and
     * cleaning themselves up when no longer needed.
     * </p>
     */
    public static interface ITempFile {

        public void delete() throws Exception;

        public String getName();

        public OutputStream open() throws Exception;
    }

    /**
     * Temp file manager.
     * <p/>
     * <p>
     * Temp file managers are created 1-to-1 with incoming requests, to create and
     * cleanup temporary files created as a result of handling the request.
     * </p>
     */
    public static interface ITempFileManager {

        void clear();

        ITempFile createTempFile(String filename_hint) throws Exception;
    }

    /**
     * Pluggable strategy for asynchronously executing requests.
     */
    static interface IAsyncRunner {

        void closeAll();

        void closed(ClientHandler clientHandler);

        void exec(ClientHandler code);
    }

    /**
     * Default threading strategy for NanoHTTPD.
     * <p/>
     * <p>
     * By default, the server spawns a new Thread for every incoming request. These
     * are set to <i>daemon</i> status, and named according to the request number.
     * The name is useful when profiling the application.
     * </p>
     */
    public static class DefaultAsyncRunner implements IAsyncRunner {

        protected long requestCount;

        private final List<ClientHandler> running = Collections.synchronizedList(new ArrayList<ClientHandler>());

        /**
         * @return a list with currently running clients.
         */
        public List<ClientHandler> getRunning() {
            return running;
        }

        @Override
        public void closeAll() {
            // copy of the list for concurrency
            for (ClientHandler clientHandler : new ArrayList<ClientHandler>(this.running)) {
                clientHandler.close();
            }
        }

        @Override
        public void closed(ClientHandler clientHandler) {
            this.running.remove(clientHandler);
        }

        @Override
        public void exec(ClientHandler clientHandler) {
            ++this.requestCount;
            this.running.add(clientHandler);
            createThread(clientHandler).start();
        }

        protected Thread createThread(ClientHandler clientHandler) {
            Thread t = new Thread(clientHandler);
            t.setDaemon(true);
            t.setName("NanoHttpd Request Processor (#" + this.requestCount + ")");
            return t;
        }
    }

    /**
     * The runnable that will be used for every new client connection.
     */
    public static class ClientHandler implements Runnable {

        private final NanoHTTPD httpd;

        private final InputStream inputStream;

        private final Socket acceptSocket;

        public ClientHandler(NanoHTTPD httpd, InputStream inputStream, Socket acceptSocket) {
            this.httpd = httpd;
            this.inputStream = inputStream;
            this.acceptSocket = acceptSocket;
        }

        public void close() {
            NanoHTTPD.safeClose(this.inputStream);
            NanoHTTPD.safeClose(this.acceptSocket);
        }

        @Override
        public void run() {
            OutputStream outputStream = null;
            try {
                outputStream = this.acceptSocket.getOutputStream();
                ITempFileManager tempFileManager = httpd.getTempFileManagerFactory().create();
                HTTPSession session = new HTTPSession(httpd, tempFileManager, this.inputStream, outputStream, this.acceptSocket.getInetAddress());
                while (!this.acceptSocket.isClosed()) {
                    session.execute();
                }
            } catch (Exception e) {
                // When the socket is closed by the client,
                // we throw our own SocketException
                // to break the "keep alive" loop above. If
                // the exception was anything other
                // than the expected SocketException OR a
                // SocketTimeoutException, print the
                // stacktrace
                if (!(e instanceof SocketException && "NanoHttpd Shutdown".equals(e.getMessage())) && !(e instanceof SocketTimeoutException)) {
                    NanoHTTPD.LOG.log(Level.SEVERE, "Communication with the client broken, or an bug in the handler code", e);
                }
            } finally {
                NanoHTTPD.safeClose(outputStream);
                NanoHTTPD.safeClose(this.inputStream);
                NanoHTTPD.safeClose(this.acceptSocket);
                httpd.asyncRunner.closed(this);
            }
        }
    }

    /**
     * Handles one session, i.e. parses the HTTP request and returns the response.
     */
    public interface IHTTPSession {

        void execute() throws IOException;

        CookieHandler getCookies();

        Map<String, String> getHeaders();

        InputStream getInputStream();

        Method getMethod();

        /**
         * This method will only return the first value for a given parameter. You
         * will want to use getParameters if you expect multiple values for a given
         * key.
         *
         * @deprecated use {@link #getParameters()} instead.
         */
        @Deprecated
        Map<String, String> getParms();

        Map<String, List<String>> getParameters();

        String getQueryParameterString();

        /**
         * @return the path part of the URL.
         */
        String getUri();

        /**
         * Adds the files in the request body to the files map.
         *
         * @param files map to modify
         */
        void parseBody(Map<String, String> files) throws IOException, NanoHTTPD.ResponseException;

        /**
         * Get the remote ip address of the requester.
         *
         * @return the IP address.
         */
        String getRemoteIpAddress();

        /**
         * Get the remote hostname of the requester.
         *
         * @return the hostname.
         */
        String getRemoteHostName();
    }

    public static class HTTPSession implements IHTTPSession {

        public static final String POST_DATA = "postData";

        private static final int REQUEST_BUFFER_LEN = 512;

        private static final int MEMORY_STORE_LIMIT = 1024;

        public static final int BUFSIZE = 8192;

        public static final int MAX_HEADER_SIZE = 1024;

        private final NanoHTTPD httpd;

        private final ITempFileManager tempFileManager;

        private final OutputStream outputStream;

        private final BufferedInputStream inputStream;

        private int splitbyte;

        private int rlen;

        private String uri;

        private Method method;

        private Map<String, List<String>> parms;

        private Map<String, String> headers;

        private CookieHandler cookies;

        private String queryParameterString;

        private String remoteIp;

        private String remoteHostname;

        private String protocolVersion;

        public HTTPSession(NanoHTTPD httpd, ITempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream) {
            this.httpd = httpd;
            this.tempFileManager = tempFileManager;
            this.inputStream = new BufferedInputStream(inputStream, HTTPSession.BUFSIZE);
            this.outputStream = outputStream;
        }

        public HTTPSession(NanoHTTPD httpd, ITempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream, InetAddress inetAddress) {
            this.httpd = httpd;
            this.tempFileManager = tempFileManager;
            this.inputStream = new BufferedInputStream(inputStream, HTTPSession.BUFSIZE);
            this.outputStream = outputStream;
            this.remoteIp = inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress() ? "127.0.0.1" : inetAddress.getHostAddress().toString();
            this.remoteHostname = inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress() ? "localhost" : inetAddress.getHostName().toString();
            this.headers = new HashMap<String, String>();
        }

        /**
         * Decodes the sent headers and loads the data into Key/value pairs
         */
        private void decodeHeader(BufferedReader in, Map<String, String> pre, Map<String, List<String>> parms, Map<String, String> headers) throws NanoHTTPD.ResponseException {
            try {
                // Read the request line
                String inLine = in.readLine();
                if (inLine == null) {
                    return;
                }

                StringTokenizer st = new StringTokenizer(inLine);
                if (!st.hasMoreTokens()) {
                    throw new NanoHTTPD.ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
                }

                pre.put("method", st.nextToken());

                if (!st.hasMoreTokens()) {
                    throw new NanoHTTPD.ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
                }

                String uri = st.nextToken();

                // Decode parameters from the URI
                int qmi = uri.indexOf('?');
                if (qmi >= 0) {
                    decodeParms(uri.substring(qmi + 1), parms);
                    uri = NanoHTTPD.decodePercent(uri.substring(0, qmi));
                } else {
                    uri = NanoHTTPD.decodePercent(uri);
                }

                // If there's another token, its protocol version,
                // followed by HTTP headers.
                // NOTE: this now forces header names lower case since they are
                // case insensitive and vary by client.
                if (st.hasMoreTokens()) {
                    protocolVersion = st.nextToken();
                } else {
                    protocolVersion = "HTTP/1.1";
                    NanoHTTPD.LOG.log(Level.FINE, "no protocol version specified, strange. Assuming HTTP/1.1.");
                }
                String line = in.readLine();
                while (line != null && !line.trim().isEmpty()) {
                    int p = line.indexOf(':');
                    if (p >= 0) {
                        headers.put(line.substring(0, p).trim().toLowerCase(Locale.US), line.substring(p + 1).trim());
                    }
                    line = in.readLine();
                }

                pre.put("uri", uri);
            } catch (IOException ioe) {
                throw new NanoHTTPD.ResponseException(Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage(), ioe);
            }
        }

        /**
         * Decodes the Multipart Body data and put it into Key/Value pairs.
         */
        private void decodeMultipartFormData(ContentType contentType, ByteBuffer fbuf, Map<String, List<String>> parms, Map<String, String> files) throws NanoHTTPD.ResponseException {
            int pcount = 0;
            try {
                int[] boundaryIdxs = getBoundaryPositions(fbuf, contentType.getBoundary().getBytes());
                if (boundaryIdxs.length < 2) {
                    throw new NanoHTTPD.ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but contains less than two boundary strings.");
                }

                byte[] partHeaderBuff = new byte[MAX_HEADER_SIZE];
                for (int boundaryIdx = 0; boundaryIdx < boundaryIdxs.length - 1; boundaryIdx++) {
                    fbuf.position(boundaryIdxs[boundaryIdx]);
                    int len = (fbuf.remaining() < MAX_HEADER_SIZE) ? fbuf.remaining() : MAX_HEADER_SIZE;
                    fbuf.get(partHeaderBuff, 0, len);
                    BufferedReader in =
                            new BufferedReader(new InputStreamReader(new ByteArrayInputStream(partHeaderBuff, 0, len), Charset.forName(contentType.getEncoding())), len);

                    int headerLines = 0;
                    // First line is boundary string
                    String mpline = in.readLine();
                    headerLines++;
                    if (mpline == null || !mpline.contains(contentType.getBoundary())) {
                        throw new NanoHTTPD.ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but chunk does not start with boundary.");
                    }

                    String partName = null, fileName = null, partContentType = null;
                    // Parse the reset of the header lines
                    mpline = in.readLine();
                    headerLines++;
                    while (mpline != null && mpline.trim().length() > 0) {
                        Matcher matcher = NanoHTTPD.CONTENT_DISPOSITION_PATTERN.matcher(mpline);
                        if (matcher.matches()) {
                            String attributeString = matcher.group(2);
                            matcher = NanoHTTPD.CONTENT_DISPOSITION_ATTRIBUTE_PATTERN.matcher(attributeString);
                            while (matcher.find()) {
                                String key = matcher.group(1);
                                if ("name".equalsIgnoreCase(key)) {
                                    partName = matcher.group(2);
                                } else if ("filename".equalsIgnoreCase(key)) {
                                    fileName = matcher.group(2);
                                    // add these two line to support multiple
                                    // files uploaded using the same field Id
                                    if (!fileName.isEmpty()) {
                                        if (pcount > 0)
                                            partName = partName + String.valueOf(pcount++);
                                        else
                                            pcount++;
                                    }
                                }
                            }
                        }
                        matcher = NanoHTTPD.CONTENT_TYPE_PATTERN.matcher(mpline);
                        if (matcher.matches()) {
                            partContentType = matcher.group(2).trim();
                        }
                        mpline = in.readLine();
                        headerLines++;
                    }
                    int partHeaderLength = 0;
                    while (headerLines-- > 0) {
                        partHeaderLength = scipOverNewLine(partHeaderBuff, partHeaderLength);
                    }
                    // Read the part data
                    if (partHeaderLength >= len - 4) {
                        throw new NanoHTTPD.ResponseException(Status.INTERNAL_ERROR, "Multipart header size exceeds MAX_HEADER_SIZE.");
                    }
                    int partDataStart = boundaryIdxs[boundaryIdx] + partHeaderLength;
                    int partDataEnd = boundaryIdxs[boundaryIdx + 1] - 4;

                    fbuf.position(partDataStart);

                    List<String> values = parms.get(partName);
                    if (values == null) {
                        values = new ArrayList<String>();
                        parms.put(partName, values);
                    }

                    if (partContentType == null) {
                        // Read the part into a string
                        byte[] data_bytes = new byte[partDataEnd - partDataStart];
                        fbuf.get(data_bytes);

                        values.add(new String(data_bytes, contentType.getEncoding()));
                    } else {
                        // Read it into a file
                        String path = saveTmpFile(fbuf, partDataStart, partDataEnd - partDataStart, fileName);
                        if (!files.containsKey(partName)) {
                            files.put(partName, path);
                        } else {
                            int count = 2;
                            while (files.containsKey(partName + count)) {
                                count++;
                            }
                            files.put(partName + count, path);
                        }
                        values.add(fileName);
                    }
                }
            } catch (NanoHTTPD.ResponseException re) {
                throw re;
            } catch (Exception e) {
                throw new NanoHTTPD.ResponseException(Status.INTERNAL_ERROR, e.toString());
            }
        }

        private int scipOverNewLine(byte[] partHeaderBuff, int index) {
            while (partHeaderBuff[index] != '\n') {
                index++;
            }
            return ++index;
        }

        /**
         * Decodes parameters in percent-encoded URI-format ( e.g.
         * "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them to given Map.
         */
        private void decodeParms(String parms, Map<String, List<String>> p) {
            if (parms == null) {
                this.queryParameterString = "";
                return;
            }

            this.queryParameterString = parms;
            StringTokenizer st = new StringTokenizer(parms, "&");
            while (st.hasMoreTokens()) {
                String e = st.nextToken();
                int sep = e.indexOf('=');
                String key = null;
                String value = null;

                if (sep >= 0) {
                    key = NanoHTTPD.decodePercent(e.substring(0, sep)).trim();
                    value = NanoHTTPD.decodePercent(e.substring(sep + 1));
                } else {
                    key = NanoHTTPD.decodePercent(e).trim();
                    value = "";
                }

                List<String> values = p.get(key);
                if (values == null) {
                    values = new ArrayList<String>();
                    p.put(key, values);
                }

                values.add(value);
            }
        }

        @Override
        public void execute() throws IOException {
            Response r = null;
            try {
                // Read the first 8192 bytes.
                // The full header should fit in here.
                // Apache's default header limit is 8KB.
                // Do NOT assume that a single read will get the entire header
                // at once!
                byte[] buf = new byte[HTTPSession.BUFSIZE];
                this.splitbyte = 0;
                this.rlen = 0;

                int read = -1;
                this.inputStream.mark(HTTPSession.BUFSIZE);
                try {
                    read = this.inputStream.read(buf, 0, HTTPSession.BUFSIZE);
                } catch (SSLException e) {
                    throw e;
                } catch (IOException e) {
                    NanoHTTPD.safeClose(this.inputStream);
                    NanoHTTPD.safeClose(this.outputStream);
                    throw new SocketException("NanoHttpd Shutdown");
                }
                if (read == -1) {
                    // socket was been closed
                    NanoHTTPD.safeClose(this.inputStream);
                    NanoHTTPD.safeClose(this.outputStream);
                    throw new SocketException("NanoHttpd Shutdown");
                }
                while (read > 0) {
                    this.rlen += read;
                    this.splitbyte = findHeaderEnd(buf, this.rlen);
                    if (this.splitbyte > 0) {
                        break;
                    }
                    read = this.inputStream.read(buf, this.rlen, HTTPSession.BUFSIZE - this.rlen);
                }

                if (this.splitbyte < this.rlen) {
                    this.inputStream.reset();
                    this.inputStream.skip(this.splitbyte);
                }

                this.parms = new HashMap<String, List<String>>();
                if (null == this.headers) {
                    this.headers = new HashMap<String, String>();
                } else {
                    this.headers.clear();
                }

                // Create a BufferedReader for parsing the header.
                BufferedReader hin = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf, 0, this.rlen)));

                // Decode the header into parms and header java properties
                Map<String, String> pre = new HashMap<String, String>();
                decodeHeader(hin, pre, this.parms, this.headers);

                if (null != this.remoteIp) {
                    this.headers.put("remote-addr", this.remoteIp);
                    this.headers.put("http-client-ip", this.remoteIp);
                }

                this.method = Method.lookup(pre.get("method"));
                if (this.method == null) {
                    throw new NanoHTTPD.ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Syntax error. HTTP verb " + pre.get("method") + " unhandled.");
                }

                this.uri = pre.get("uri");

                this.cookies = new CookieHandler(this.headers);

                String connection = this.headers.get("connection");
                boolean keepAlive = "HTTP/1.1".equals(protocolVersion) && (connection == null || !connection.matches("(?i).*close.*"));

                // Ok, now do the serve()

                // TODO: long body_size = getBodySize();
                // TODO: long pos_before_serve = this.inputStream.totalRead()
                // (requires implementation for totalRead())
                r = httpd.handle(this);
                // TODO: this.inputStream.skip(body_size -
                // (this.inputStream.totalRead() - pos_before_serve))

                if (r == null) {
                    throw new NanoHTTPD.ResponseException(Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: Serv" +
                            "e() returned a null response.");
                } else {
                    String acceptEncoding = this.headers.get("accept-encoding");
                    this.cookies.unloadQueue(r);
                    r.setRequestMethod(this.method);
                    r.setGzipEncoding(r.useGzipWhenAccepted() && acceptEncoding != null && acceptEncoding.contains("gzip"));
                    r.setKeepAlive(keepAlive);
                    r.send(this.outputStream);
                }
                if (!keepAlive || r.isCloseConnection()) {
                    throw new SocketException("NanoHttpd Shutdown");
                }
            } catch (SocketException e) {
                // throw it out to close socket object (finalAccept)
                throw e;
            } catch (SocketTimeoutException ste) {
                // treat socket timeouts the same way we treat socket exceptions
                // i.e. close the stream & finalAccept object by throwing the
                // exception up the call stack.
                throw ste;
            } catch (SSLException ssle) {
                Response resp = Response.newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "SSL PROTOCOL FAILURE: " + ssle.getMessage());
                resp.send(this.outputStream);
                NanoHTTPD.safeClose(this.outputStream);
            } catch (IOException ioe) {
                Response resp = Response.newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
                resp.send(this.outputStream);
                NanoHTTPD.safeClose(this.outputStream);
            } catch (NanoHTTPD.ResponseException re) {
                Response resp = Response.newFixedLengthResponse(re.getStatus(), NanoHTTPD.MIME_PLAINTEXT, re.getMessage());
                resp.send(this.outputStream);
                NanoHTTPD.safeClose(this.outputStream);
            } finally {
                NanoHTTPD.safeClose(r);
                this.tempFileManager.clear();
            }
        }

        /**
         * Find byte index separating header from body. It must be the last byte of
         * the first two sequential new lines.
         */
        private int findHeaderEnd(final byte[] buf, int rlen) {
            int splitbyte = 0;
            while (splitbyte + 1 < rlen) {

                // RFC2616
                if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n' && splitbyte + 3 < rlen && buf[splitbyte + 2] == '\r' && buf[splitbyte + 3] == '\n') {
                    return splitbyte + 4;
                }

                // tolerance
                if (buf[splitbyte] == '\n' && buf[splitbyte + 1] == '\n') {
                    return splitbyte + 2;
                }
                splitbyte++;
            }
            return 0;
        }

        /**
         * Find the byte positions where multipart boundaries start. This reads a
         * large block at a time and uses a temporary buffer to optimize (memory
         * mapped) file access.
         */
        private int[] getBoundaryPositions(ByteBuffer b, byte[] boundary) {
            int[] res = new int[0];
            if (b.remaining() < boundary.length) {
                return res;
            }

            int search_window_pos = 0;
            byte[] search_window = new byte[4 * 1024 + boundary.length];

            int first_fill = (b.remaining() < search_window.length) ? b.remaining() : search_window.length;
            b.get(search_window, 0, first_fill);
            int new_bytes = first_fill - boundary.length;

            do {
                // Search the search_window
                for (int j = 0; j < new_bytes; j++) {
                    for (int i = 0; i < boundary.length; i++) {
                        if (search_window[j + i] != boundary[i])
                            break;
                        if (i == boundary.length - 1) {
                            // Match found, add it to results
                            int[] new_res = new int[res.length + 1];
                            System.arraycopy(res, 0, new_res, 0, res.length);
                            new_res[res.length] = search_window_pos + j;
                            res = new_res;
                        }
                    }
                }
                search_window_pos += new_bytes;

                // Copy the end of the buffer to the start
                System.arraycopy(search_window, search_window.length - boundary.length, search_window, 0, boundary.length);

                // Refill search_window
                new_bytes = search_window.length - boundary.length;
                new_bytes = (b.remaining() < new_bytes) ? b.remaining() : new_bytes;
                b.get(search_window, boundary.length, new_bytes);
            } while (new_bytes > 0);
            return res;
        }

        @Override
        public CookieHandler getCookies() {
            return this.cookies;
        }

        @Override
        public final Map<String, String> getHeaders() {
            return this.headers;
        }

        @Override
        public final InputStream getInputStream() {
            return this.inputStream;
        }

        @Override
        public final Method getMethod() {
            return this.method;
        }

        /**
         * @deprecated use {@link #getParameters()} instead.
         */
        @Override
        @Deprecated
        public final Map<String, String> getParms() {
            Map<String, String> result = new HashMap<String, String>();
            for (String key : this.parms.keySet()) {
                result.put(key, this.parms.get(key).get(0));
            }

            return result;
        }

        @Override
        public final Map<String, List<String>> getParameters() {
            return this.parms;
        }

        @Override
        public String getQueryParameterString() {
            return this.queryParameterString;
        }

        private RandomAccessFile getTmpBucket() {
            try {
                ITempFile tempFile = this.tempFileManager.createTempFile(null);
                return new RandomAccessFile(tempFile.getName(), "rw");
            } catch (Exception e) {
                throw new Error(e); // we won't recover, so throw an error
            }
        }

        @Override
        public final String getUri() {
            return this.uri;
        }

        /**
         * Deduce body length in bytes. Either from "content-length" header or read
         * bytes.
         */
        public long getBodySize() {
            if (this.headers.containsKey("content-length")) {
                return Long.parseLong(this.headers.get("content-length"));
            } else if (this.splitbyte < this.rlen) {
                return this.rlen - this.splitbyte;
            }
            return 0;
        }

        @Override
        public void parseBody(Map<String, String> files) throws IOException, NanoHTTPD.ResponseException {
            RandomAccessFile randomAccessFile = null;
            try {
                long size = getBodySize();
                ByteArrayOutputStream baos = null;
                DataOutput requestDataOutput = null;

                // Store the request in memory or a file, depending on size
                if (size < MEMORY_STORE_LIMIT) {
                    baos = new ByteArrayOutputStream();
                    requestDataOutput = new DataOutputStream(baos);
                } else {
                    randomAccessFile = getTmpBucket();
                    requestDataOutput = randomAccessFile;
                }

                // Read all the body and write it to request_data_output
                byte[] buf = new byte[REQUEST_BUFFER_LEN];
                while (this.rlen >= 0 && size > 0) {
                    this.rlen = this.inputStream.read(buf, 0, (int) Math.min(size, REQUEST_BUFFER_LEN));
                    size -= this.rlen;
                    if (this.rlen > 0) {
                        requestDataOutput.write(buf, 0, this.rlen);
                    }
                }

                ByteBuffer fbuf = null;
                if (baos != null) {
                    fbuf = ByteBuffer.wrap(baos.toByteArray(), 0, baos.size());
                } else {
                    fbuf = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, randomAccessFile.length());
                    randomAccessFile.seek(0);
                }

                // If the method is POST, there may be parameters
                // in data section, too, read it:
                if (Method.POST.equals(this.method)) {
                    ContentType contentType = new ContentType(this.headers.get("content-type"));
                    if (contentType.isMultipart()) {
                        String boundary = contentType.getBoundary();
                        if (boundary == null) {
                            throw new NanoHTTPD.ResponseException(Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html");
                        }
                        decodeMultipartFormData(contentType, fbuf, this.parms, files);
                    } else {
                        byte[] postBytes = new byte[fbuf.remaining()];
                        fbuf.get(postBytes);
                        String postLine = new String(postBytes, contentType.getEncoding()).trim();
                        // Handle application/x-www-form-urlencoded
                        if ("application/x-www-form-urlencoded".equalsIgnoreCase(contentType.getContentType())) {
                            decodeParms(postLine, this.parms);
                        } else if (postLine.length() != 0) {
                            // Special case for raw POST data => create a
                            // special files entry "postData" with raw content
                            // data
                            files.put(POST_DATA, postLine);
                        }
                    }
                } else if (Method.PUT.equals(this.method)) {
                    files.put("content", saveTmpFile(fbuf, 0, fbuf.limit(), null));
                }
            } finally {
                NanoHTTPD.safeClose(randomAccessFile);
            }
        }

        /**
         * Retrieves the content of a sent file and saves it to a temporary file.
         * The full path to the saved file is returned.
         */
        private String saveTmpFile(ByteBuffer b, int offset, int len, String filename_hint) {
            String path = "";
            if (len > 0) {
                FileOutputStream fileOutputStream = null;
                try {
                    ITempFile tempFile = this.tempFileManager.createTempFile(filename_hint);
                    ByteBuffer src = b.duplicate();
                    fileOutputStream = new FileOutputStream(tempFile.getName());
                    FileChannel dest = fileOutputStream.getChannel();
                    src.position(offset).limit(offset + len);
                    dest.write(src.slice());
                    path = tempFile.getName();
                } catch (Exception e) { // Catch exception if any
                    throw new Error(e); // we won't recover, so throw an error
                } finally {
                    NanoHTTPD.safeClose(fileOutputStream);
                }
            }
            return path;
        }

        @Override
        public String getRemoteIpAddress() {
            return this.remoteIp;
        }

        @Override
        public String getRemoteHostName() {
            return this.remoteHostname;
        }
    }

    /**
     * The runnable that will be used for the main listening thread.
     */
    public static class ServerRunnable implements Runnable {

        private NanoHTTPD httpd;

        private final int timeout;

        private IOException bindException;

        private boolean hasBinded = false;

        public ServerRunnable(NanoHTTPD httpd, int timeout) {
            this.httpd = httpd;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            try {
                httpd.getMyServerSocket().bind(httpd.hostname != null ? new InetSocketAddress(httpd.hostname, httpd.myPort) : new InetSocketAddress(httpd.myPort));
                hasBinded = true;
            } catch (IOException e) {
                this.bindException = e;
                return;
            }
            do {
                try {
                    final Socket finalAccept = httpd.getMyServerSocket().accept();
                    if (this.timeout > 0) {
                        finalAccept.setSoTimeout(this.timeout);
                    }
                    final InputStream inputStream = finalAccept.getInputStream();
                    httpd.asyncRunner.exec(httpd.createClientHandler(finalAccept, inputStream));
                } catch (IOException e) {
                    NanoHTTPD.LOG.log(Level.FINE, "Communication with the client broken", e);
                }
            } while (!httpd.getMyServerSocket().isClosed());
        }

        public IOException getBindException() {
            return bindException;
        }

        public boolean hasBinded() {
            return hasBinded;
        }
    }

    /**
     * Represents a simple factory
     *
     * @param <T> The Type of object to create
     * @author LordFokas
     */
    public static interface IFactory<T> {

        T create();
    }

    /**
     * Represents a factory that can throw an exception instead of actually creating
     * an object
     *
     * @param <T> The Type of object to create
     * @param <E> The base Type of exceptions that can be thrown
     * @author LordFokas
     */
    public static interface IFactoryThrowing<T, E extends Throwable> {

        T create() throws E;
    }

    /**
     * Defines a generic handler that returns an object of type O when given an
     * object of type I.
     *
     * @param <I> The input type.
     * @param <O> The output type.
     * @author LordFokas
     */
    public static interface IHandler<I, O> {

        O handle(I input);
    }

    public static class ServerRunner {

        /**
         * logger to log to.
         */
        private static final Logger LOG = Logger.getLogger(ServerRunner.class.getName());

        public static void executeInstance(NanoHTTPD server) {
            try {
                server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            } catch (IOException ioe) {
                System.err.println("Couldn't start server:\n" + ioe);
                System.exit(-1);
            }

            System.out.println("Server started, Hit Enter to stop.\n");

            try {
                System.in.read();
            } catch (Throwable ignored) {
            }

            server.stop();
            System.out.println("Server stopped.\n");
        }

        public static <T extends NanoHTTPD> void run(Class<T> serverClass) {
            try {
                executeInstance(serverClass.newInstance());
            } catch (Exception e) {
                ServerRunner.LOG.log(Level.SEVERE, "Could not create server", e);
            }
        }
    }
}
