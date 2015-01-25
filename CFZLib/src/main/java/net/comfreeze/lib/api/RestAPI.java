package net.comfreeze.lib.api;

import android.os.Bundle;
import android.util.Log;

import net.comfreeze.lib.api.helper.JSONHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.custom.JSONException;
import org.json.custom.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Iterator;

abstract public class RestAPI extends BaseAPI {
    private static final String TAG = RestAPI.class.getSimpleName();

    public static String readErrors(JSONObject error) {
        if (!silent)
            Log.i(TAG, "Parsing errors");
        StringBuilder builder = new StringBuilder();
        Object errors = JSONHelper.get(error, RESPONSE_ERROR_MESSAGE);
        if (null == errors)
            return null;
        if (errors instanceof String) {
            builder.append(errors);
        } else if (errors instanceof JSONObject) {
            JSONObject errorObj = (JSONObject) errors;
            @SuppressWarnings("unchecked")
            Iterator<String> keys = errorObj.keys();
            while (keys.hasNext())
                builder.append((builder.length() > 0 ? "\n" : "") + JSONHelper.getString(errorObj, keys.next()));
        }
        return builder.toString();
    }

    public JSONObject list(Bundle parameters) {
        return list(getEndpoint(), parameters);
    }

    public JSONObject list(String endpoint, Bundle parameters) {
        if (!silent)
            Log.d(TAG, "GET: " + endpoint);
        HttpResponse response = null;
        JSONObject jsonResponse = null;
        int responseCode = defaultResponseCode;
        String responseMessage = defaultResponseMessage;
        HttpGet request = (HttpGet) getRequest(Type.GET, endpoint);
        if (null != parameters && parameters.size() > 0)
            request.setURI(URI.create(request.getURI().toString() + (request.getURI().toString().contains("?") ? "&" : "?") + buildQueryString(parameters)));
        if (!silent)
            Log.d(TAG, "REQUEST: " + request.getURI().toString());
        response = execute(request);
        String responseString = buildResponseString(response);
        if (!silent)
            Log.d(TAG, "RAW RESPONSE: " + responseString);
        jsonResponse = buildJsonObject(responseString);
        if (null != response && null != response.getStatusLine()) {
            responseCode = response.getStatusLine().getStatusCode();
            responseMessage = response.getStatusLine().getReasonPhrase();
        }
        if (!silent)
            Log.d(TAG, "RESPONSE: " + jsonResponse);
        if (null == jsonResponse) {
            jsonResponse = new JSONObject();
        }
        try {
            jsonResponse.put(RESPONSE_STATUS, responseCode);
            jsonResponse.put(RESPONSE_MESSAGE, responseMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    public JSONObject create(Bundle parameters) {
        return create(getEndpoint(), parameters);
    }

    public JSONObject create(String endpoint, Bundle parameters) {
        if (!silent)
            Log.d(TAG, "POST: " + endpoint);
        HttpResponse response = null;
        JSONObject jsonResponse = null;
        int responseCode = defaultResponseCode;
        String responseMessage = defaultResponseMessage;
        HttpPost request = (HttpPost) getRequest(Type.POST, endpoint);
        JSONObject jsonRequest = buildJsonObject(parameters);
        if (!silent)
            Log.d(TAG, "REQUEST: " + jsonRequest);
        try {
            request.setEntity(new StringEntity(jsonRequest.toString()));
            response = execute(request);
            String responseString = buildResponseString(response);
            if (!silent)
                Log.d(TAG, "RAW RESPONSE: " + responseString);
            jsonResponse = buildJsonObject(responseString);
            if (null != response && null != response.getStatusLine()) {
                responseCode = response.getStatusLine().getStatusCode();
                responseMessage = response.getStatusLine().getReasonPhrase();
            }
            if (!silent)
                Log.d(TAG, "RESPONSE: " + jsonResponse);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (null == jsonResponse) {
            jsonResponse = new JSONObject();
        }
        try {
            jsonResponse.put(RESPONSE_STATUS, responseCode);
            jsonResponse.put(RESPONSE_MESSAGE, responseMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    public JSONObject upload(Bundle parameters, File file) {
        return upload(getEndpoint(), parameters, file);
    }

    public JSONObject upload(String endpoint, Bundle parameters, File file) {
        if (!silent)
            Log.d(TAG, "POST: " + endpoint);
        HttpResponse response = null;
        JSONObject jsonResponse = null;
        int responseCode = defaultResponseCode;
        String responseMessage = defaultResponseMessage;
        HttpPost request = (HttpPost) getRequest(Type.POST, endpoint, null);
        JSONObject jsonRequest = buildJsonObject(parameters);
        if (!silent)
            Log.d(TAG, "REQUEST: " + jsonRequest);
        try {
            MultipartEntity entity = new MultipartEntity();// HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart("body", new StringBody(jsonRequest.toString()));
            entity.addPart("photo[photo_bn]", new FileBody(file));
            // StringEntity entity = new StringEntity(jsonRequest.toString());
            request.setEntity(entity);
            // request.setHeader(HTTP.CONTENT_TYPE, "multipart/form-data");
            response = execute(request);
            String responseString = buildResponseString(response);
            if (!silent)
                Log.d(TAG, "RAW RESPONSE: " + responseString);
            jsonResponse = buildJsonObject(responseString);
            if (null != response && null != response.getStatusLine()) {
                responseCode = response.getStatusLine().getStatusCode();
                responseMessage = response.getStatusLine().getReasonPhrase();
            }
            if (!silent)
                Log.d(TAG, "RESPONSE: " + jsonResponse);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (null == jsonResponse) {
            jsonResponse = new JSONObject();
        }
        try {
            jsonResponse.put(RESPONSE_STATUS, responseCode);
            jsonResponse.put(RESPONSE_MESSAGE, responseMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    public JSONObject update(Bundle parameters) {
        return update(getEndpoint(), parameters);
    }

    public JSONObject update(String endpoint, Bundle parameters) {
        if (!silent)
            Log.d(TAG, "PUT: " + endpoint);
        HttpResponse response = null;
        JSONObject jsonResponse = null;
        int responseCode = defaultResponseCode;
        String responseMessage = defaultResponseMessage;
        HttpPut request = (HttpPut) getRequest(Type.PUT, endpoint);
        JSONObject jsonRequest = buildJsonObject(parameters);
        if (!silent)
            Log.d(TAG, "REQUEST: " + jsonRequest);
        try {
            request.setEntity(new StringEntity(jsonRequest.toString()));
            response = execute(request);
            String responseString = buildResponseString(response);
            if (!silent)
                Log.d(TAG, "RAW RESPONSE: " + responseString);
            jsonResponse = buildJsonObject(responseString);
            if (null != response && null != response.getStatusLine()) {
                responseCode = response.getStatusLine().getStatusCode();
                responseMessage = response.getStatusLine().getReasonPhrase();
            }
            if (!silent)
                Log.d(TAG, "RESPONSE: " + jsonResponse);
        } catch (UnsupportedEncodingException e) {
            if (!silent)
                Log.e(TAG, "Exception encountered", e);
            e.printStackTrace();
        }
        if (null == jsonResponse) {
            jsonResponse = new JSONObject();
        }
        try {
            jsonResponse.put(RESPONSE_STATUS, responseCode);
            jsonResponse.put(RESPONSE_MESSAGE, responseMessage);
        } catch (JSONException e) {
            if (!silent)
                Log.e(TAG, "Exception encountered", e);
            e.printStackTrace();
        }
        return jsonResponse;
    }

    public JSONObject delete(Bundle parameters) {
        return delete(getEndpoint(), parameters);
    }

    public JSONObject delete(String endpoint, Bundle parameters) {
        if (!silent)
            Log.d(TAG, "GET: " + endpoint);
        HttpResponse response = null;
        JSONObject jsonResponse = null;
        int responseCode = defaultResponseCode;
        String responseMessage = defaultResponseMessage;
        HttpDelete request = (HttpDelete) getRequest(Type.DELETE, endpoint);
        if (null != parameters && parameters.size() > 0)
            request.setURI(URI.create(request.getURI().toString() + (request.getURI().toString().contains("?") ? "&" : "?") + buildQueryString(parameters)));
        if (!silent)
            Log.d(TAG, "REQUEST: " + request.getURI().toString());
        response = execute(request);
        String responseString = buildResponseString(response);
        if (!silent)
            Log.d(TAG, "RAW RESPONSE: " + responseString);
        jsonResponse = buildJsonObject(responseString);
        if (null != response && null != response.getStatusLine()) {
            responseCode = response.getStatusLine().getStatusCode();
            responseMessage = response.getStatusLine().getReasonPhrase();
        }
        if (!silent)
            Log.d(TAG, "RESPONSE: " + jsonResponse);
        if (null == jsonResponse) {
            jsonResponse = new JSONObject();
        }
        try {
            jsonResponse.put(RESPONSE_STATUS, responseCode);
            jsonResponse.put(RESPONSE_MESSAGE, responseMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }
}