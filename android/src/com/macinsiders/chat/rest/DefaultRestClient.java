package com.macinsiders.chat.rest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

public class DefaultRestClient implements RestClient {

    private static final String TAG = DefaultRestClient.class.getSimpleName();

    /*
     * (non-Javadoc)
     * 
     * @see
     * mn.aug.restfulandroid.rest.IRestClient#execute(mn.aug.restfulandroid.
     * rest.Request)
     */
    @Override
    public Response execute(Request request) {
        HttpURLConnection conn = null;
        Response response = null;
        int status = -1;
        try {

            URL url = request.getRequestUri().toURL();
            Log.d(TAG, url.toString());
            conn = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "Checking for request headers!");
            if (request.getHeaders() != null) {
                Log.d(TAG, "Request headers found!");
                for (String header : request.getHeaders().keySet()) {
                    for (String value : request.getHeaders().get(header)) {
                        Log.d(TAG, header + ": " + value);
                        // conn.addRequestProperty(header, value);
                        conn.setRequestProperty(header, value);
                    }
                }
            }

            switch (request.getMethod()) {
                case GET:
                    conn.setDoOutput(false);
                    break;
                case POST:
                    byte[] payload = request.getBody();
                    conn.setDoOutput(true);
                    // causes EOFException when the request is sent for the
                    // first time after opening the app (strange)
                    // conn.setFixedLengthStreamingMode(payload.length);
                    OutputStream outStream = conn.getOutputStream();
                    outStream.write(payload);
                    outStream.flush();
                    outStream.close();
                    status = conn.getResponseCode();
                default:
                    break;
            }

            status = conn.getResponseCode();

            // returns -1 so we can't use that to check if we should readStream.
            Log.d(TAG, Integer.toString(conn.getContentLength()));

            BufferedInputStream in;
            // If the response code is anything but 200 OK,
            // HttpUrlConnection throws an IOException on
            // getInputStream(), so we call getErrorStream() then,
            // to read the response body
            if (status != 200) {
                in = new BufferedInputStream(conn.getErrorStream());
            } else {
                in = new BufferedInputStream(conn.getInputStream());
            }
            byte[] body = readStream(in);
            Log.d(TAG, body.toString());
            response = new Response(conn.getResponseCode(), conn.getHeaderFields(), body);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        if (response == null) {
            response = new Response(status, new HashMap<String, List<String>>(), new byte[] {});
        }

        return response;
    }

    private static byte[] readStream(InputStream in) throws IOException {
        byte[] buf = new byte[1024];
        int count = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        while ((count = in.read(buf)) != -1)
            out.write(buf, 0, count);
        return out.toByteArray();
    }
}
