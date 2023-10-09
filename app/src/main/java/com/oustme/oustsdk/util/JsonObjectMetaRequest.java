package com.oustme.oustsdk.util;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.oustme.oustsdk.tools.OustPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class JsonObjectMetaRequest extends JsonObjectRequest {

    public JsonObjectMetaRequest(int method, String url, JSONObject jsonRequest, Response.Listener
            <JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public JsonObjectMetaRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject>
            listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            JSONObject jsonResponse = new JSONObject(jsonString);
            handleHeader(response.headers);
            jsonResponse.put("headers", new JSONObject(response.headers));
            return Response.success(jsonResponse,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    private void handleHeader(Map<String, String> headers) {
        if (headers == null)
            return;
        String authorization = headers.get("Authorization");
        if (authorization != null)
            OustPreferences.save("authToken", authorization);

    }
}