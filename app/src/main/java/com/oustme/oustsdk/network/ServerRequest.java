package com.oustme.oustsdk.network;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ItsMe on 11/4/16.
 */
public class ServerRequest extends Request<JSONObject> {


    /** Default charset for JSON request. */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/x-www-form-urlencoded; charset=%s", PROTOCOL_CHARSET);

    private final Response.Listener<JSONObject> mListener;
    private final String mRequestBody;


    public ServerRequest(String url, String requestBody, Response.Listener<JSONObject> listener,
                         Response.ErrorListener errorListener) {
        this(Method.DEPRECATED_GET_OR_POST, url, requestBody, listener, errorListener);
    }

    public ServerRequest(int method, String url, String requestBody, Response.Listener<JSONObject> listener,
                         Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        mRequestBody = requestBody;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }


    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    /**
     * @deprecated Use {@link #getBodyContentType()}.
     */
    @Override
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    /**
     * @deprecated Use {@link #getBody()}.
     */
//    @Override
//    public byte[] getPostBody() {
//        return getBody();
//    }

//    @Override
//    public String getBodyContentType() {
//        return PROTOCOL_CONTENT_TYPE;
//    }
//
//    @Override
//    public byte[] getBody() {
//        try {
//            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
//        } catch (UnsupportedEncodingException uee) {
//            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
//                    mRequestBody, PROTOCOL_CHARSET);
//            return null;
//        }
//    }
}
