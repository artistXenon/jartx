package moe.artx.network;

import moe.artx.utility.ConcatenatedInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestBody extends ConcatenatedInputStream {
    public static HttpRequestBody EMPTY_BODY = new HttpRequestBody(null);

    public final String contentType;
    public long contentLength = -1;

    public HttpRequestBody(String contentType) {
        this.contentType = contentType;
    }
}