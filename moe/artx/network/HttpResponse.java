package moe.artx.network;

import java.net.HttpURLConnection;

public interface HttpResponse {
    void onResponse(HttpURLConnection connection) throws Exception;

    void onException(Exception e);
}
