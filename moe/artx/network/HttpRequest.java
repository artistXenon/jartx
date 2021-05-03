package moe.artx.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class HttpRequest {
    public static String DefaultUserAgent = null;

    final private Map<String, String> requestHeaders = new HashMap<>();
    private boolean secureConnection = false;
    private int timeout = 5000;
    private String characterSet = "UTF-8";
    private URL destination = null;
    private Method method = Method.GET;
    private HttpRequestBody body = null;
    private HttpURLConnection connection;

    public HttpRequest() { }

    public HttpRequest(Method method, URL url) {
        this.method = method;
        this.setURL(url);
    }

    public HttpRequest setURL(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        }
        catch (MalformedURLException e) { url = null; }

        return this.setURL(url);
    }

    public HttpRequest setURL(URL url) {
        if (url != null && secureConnection && url.getProtocol().equals("http")) {
            String secureUrlString = url.toString().replaceFirst("^http", "https");
            setURL(secureUrlString);
        }
        this.destination = url;
        return this;
    }

    public HttpRequest setSecureConnection(boolean secureConnection) {
        this.secureConnection = secureConnection;
        return this;
    }

    public HttpRequest setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
        return this;
    }

    public HttpRequest setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public HttpRequest setBody(HttpRequestBody body) {
        this.body = body;
        return this;
    }

    public HttpRequest setMethod(Method method) {
        this.method = method;
        return this;
    }

    public HttpRequest setHeader(String key, String value) {
        this.requestHeaders.put(key, value);
        return this;
    }

    //  GETTERS

    public boolean isReady() {
        return
                destination != null &&
                method != null;
    }

    public boolean isSecureConnection() {
        return secureConnection;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    public Method getMethod() {
        return method;
    }

    public int getTimeout() {
        return timeout;
    }

    //methods

    public HttpURLConnection fire() throws Exception {
        if (!isReady()) throw new IllegalStateException();
        generateConnection();
        if (this.method.sendsBody) {
            writeConnection();
        }
        else {
            this.connection.addRequestProperty("Content-Type", null);
            this.connection.addRequestProperty("Content-Length", null);
        }
        return this.connection;
    }

    public void fire(HttpResponse httpResponse) {
        if (httpResponse == null) throw new NullPointerException();
        try {
            httpResponse.onResponse(fire());
        }
        catch (Exception e) {
            httpResponse.onException(e);
        }
    }

    protected HttpRequest generateConnection() throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) this.destination.openConnection();
            connection.setRequestMethod(this.method.name);
            connection.setDoOutput(this.method.sendsBody);
            connection.setDoInput(this.method.receivesBody);
            if (this.timeout > 0)
                connection.setConnectTimeout(this.timeout);
            if (DefaultUserAgent != null)
                connection.addRequestProperty("User-Agent", DefaultUserAgent);
            connection.addRequestProperty("Accept-Charset", this.characterSet);
            if (this.method.sendsBody && this.body != null) {
                connection.addRequestProperty("Content-Type", this.body.contentType);
                if (this.body.contentLength > 0)
                    connection.addRequestProperty("Content-Length", this.body.contentLength + "");
            }
            for (Map.Entry<String, String> entry: requestHeaders.entrySet())
                connection.addRequestProperty(entry.getKey(), entry.getValue());
        } catch (ProtocolException ignore) { }
        this.connection = connection;
        return this;
    }

    protected HttpRequest writeConnection() throws IOException {
        try (OutputStream output = connection.getOutputStream()) {
            if (this.body == null) this.body = HttpRequestBody.EMPTY_BODY;
            InputStream input = this.body;
            if (input == null) return this;
            byte[] buf = new byte[8192];
            int length;
            while ((length = input.read(buf)) > 0)
                output.write(buf, 0, length);
            return this;
        }
    }

    protected static void addRequestMethod(String... requestingMethods) {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);
            methodsField.setAccessible(true);
            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(requestingMethods));
            String[] newMethods = methodsSet.toArray(new String[0]);

            methodsField.set(null/*static field*/, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public static class Method {
        final private static List<Method> methods = new ArrayList<>();
        final public static Method
                GET = new Method("GET", false, true),
                POST = new Method("POST", true, true),
                HEAD = new Method("HEAD", false, false),
                PUT = new Method("PUT", true, false),
                DELETE = new Method("DELETE", true, true),
                PATCH;

        static {
            Method patchMethod;
            patchMethod = Method.customMethod("PATCH");
            PATCH = patchMethod;
            methods.addAll(Arrays.asList(GET, POST, HEAD, PUT, DELETE));
        }

        final public String name;
        final public boolean sendsBody, receivesBody;

        private Method(String name, boolean sendsBody, boolean receivesBody) {
            this.name = name;
            this.sendsBody = sendsBody;
            this.receivesBody = receivesBody;
        }

        public static Method getMethod(String name) {
            for (Method method: methods)
                if (method.name.equals(name)) return method;
            return null;
        }

        public static Method customMethod(String name) {
            Method customMethod = getMethod(name);
            if (customMethod != null) return customMethod;
            if (!Pattern.compile("^[A-Z-]+$").matcher(name).matches()) return null;
            customMethod =  new Method(name, true, true);
            addRequestMethod(name);
            methods.add(customMethod);
            return customMethod;
        }

//        protected abstract HttpURLConnection onConnection(HttpURLConnection connection, RequestBody requestBody);
    }
}
