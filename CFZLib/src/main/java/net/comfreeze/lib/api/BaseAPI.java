package net.comfreeze.lib.api;

import android.os.Bundle;
import android.util.Log;

import net.comfreeze.lib.BundleBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.custom.JSONException;
import org.json.custom.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

abstract public class BaseAPI {
    public static final String TAG = BaseAPI.class.getSimpleName();
    public static final String FIELD_OUTER_KEY = "outer_key";
    public static final String RESPONSE_RESULT = "result";
    public static final String RESPONSE_ERRORS = "errors";
    public static final String RESPONSE_ERROR_MESSAGE = "msg";
    public static final String RESPONSE_ERROR_URI = "uri";
    public static final String RESPONSE_DETAIL_MESSAGE = "message";
    public static final String RESPONSE_STATUS = TAG + "response_code";
    public static final String RESPONSE_MESSAGE = TAG + "response_message";
    public static final String HEADER_AUTH_TOKEN = "auth_token";
    public static final int TIMEOUT = 60000;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    public static String contentType = null;
    public static boolean silent = true;
    protected static HttpClient client;
    protected int defaultResponseCode = 500;
    protected String defaultResponseMessage = "Unable to communicate with server system";

    public static long getDate(String data) {
        if (null == data || data.equals("null"))
            data = "0000-00-00 00:00:00";
        if (data.length() == 10)
            data += " 00:00:00";
        try {
            return BaseAPI.dateFormat.parse(data).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1L;
    }

    public static String getDate(long time) {
        if (time < 0)
            return "0000-00-00 00:00:00";
        return BaseAPI.dateFormat.format(new Date(time));
    }

    public static boolean getFlag(String key, JSONObject data) {
        if (data.has(key)) {
            if (data.isNull(key))
                return false;
            try {
                return (data.getInt(key) > 0 ? true : false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                return (Integer.valueOf(data.getString(key)) > 0 ? true : false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static int getInt(String key, JSONObject data) {
        int result = 0;
        if (data.has(key)) {
            try {
                String valueString = data.getString(key);
                if (!valueString.trim().equals("") && !valueString.trim().equals("null")) {
                    result = Integer.valueOf(valueString);
                }
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                int valueInt = data.getInt(key);
                result = valueInt;
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static float getFloat(String key, JSONObject data) {
        float result = 0;
        if (data.has(key)) {
            try {
                String valueString = data.getString(key);
                if (!valueString.trim().equals("") && !valueString.trim().equals("null")) {
                    result = Float.valueOf(valueString);
                }
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static long getLong(String key, JSONObject data) {
        long result = 0;
        if (data.has(key)) {
            try {
                String valueString = data.getString(key);
                if (!valueString.trim().equals("") && !valueString.trim().equals("null")) {
                    result = Long.valueOf(valueString);
                }
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                long valueLong = data.getLong(key);
                result = valueLong;
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static double getDouble(String key, JSONObject data) {
        double result = 0;
        if (data.has(key)) {
            try {
                String valueString = data.getString(key);
                if (!valueString.trim().equals("") && !valueString.trim().equals("null")) {
                    result = Double.valueOf(valueString);
                }
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                double valueDouble = data.getLong(key);
                result = valueDouble;
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Deprecated
    public static JSONObject bundleToJson(Bundle source) {
        return BundleBuilder.instance(source).buildJson();
    }

    public static String readErrors(JSONObject error) {
        if (!silent)
            Log.i(TAG, "Parsing errors");
        StringBuilder builder = new StringBuilder();
        if (error.length() == 2 && error.has(RESPONSE_ERROR_URI))
            error.remove(RESPONSE_ERROR_URI);
        if (error.length() == 1 && error.has(RESPONSE_ERROR_MESSAGE)) {
            try {
                builder.append(error.getString(RESPONSE_ERROR_MESSAGE));
            } catch (JSONException e) {
                Log.e(TAG, "Exception", e);
            }
        } else {
            @SuppressWarnings("unchecked")
            Iterator<String> keys = error.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (key.equals(RESPONSE_ERROR_MESSAGE))
                    continue;
                try {
                    JSONObject object = error.getJSONObject(key);
                    builder.append(readErrors(object) + "\n");
                    continue;
                } catch (JSONException e) {
                    Log.e(TAG, "Exception", e);
                }
                try {
                    String message = error.getString(key);
                    builder.append(StringUtils.capitalize(message) + "\n");
                    continue;
                } catch (JSONException e) {
                    Log.e(TAG, "Exception", e);
                }
            }
        }
        return builder.toString();
    }

    abstract public String getContentType();

    abstract public String getHost();

    abstract public String getEndpoint();

    protected HttpClient getClient() {
        if (null == client) {
            if (!silent)
                Log.i(TAG, "Generating client");
            HttpParams params = new BasicHttpParams();
            SchemeRegistry scheme = new SchemeRegistry();
            scheme.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            final SSLSocketFactory sslFactory = SSLSocketFactory.getSocketFactory();
            scheme.register(new Scheme("https", sslFactory, 443));
            HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, TIMEOUT);
            ClientConnectionManager cm = new ThreadSafeClientConnManager(params, scheme);
            client = new DefaultHttpClient(cm, params);
        } else if (!silent)
            Log.i(TAG, "Returning existing client");
        return client;
    }

    protected HttpUriRequest getRequest(Type type, String endpoint, String contentType) {
        if (!silent)
            Log.i(TAG, "Generating request");
        HttpUriRequest request = null;
        String uri = getHost() + endpoint;
        switch (type) {
            case GET:
                request = new HttpGet(uri);
                break;
            case POST:
                request = new HttpPost(uri);
                break;
            case PUT:
                request = new HttpPut(uri);
                break;
            case DELETE:
                request = new HttpDelete(uri);
                break;
            case HEAD:
                request = new HttpHead(uri);
                break;
            case OPTION:
                request = new HttpOptions(uri);
                break;
        }
        if (null != contentType)
            request.setHeader(HTTP.CONTENT_TYPE, contentType);
        return request;
    }

    protected HttpUriRequest getRequest(Type type, String endpoint) {
        return getRequest(type, endpoint, getContentType());
    }

    protected HttpResponse execute(HttpUriRequest request) {
        if (!silent) {
            Log.i(TAG, "Performing request");
            for (Header header : request.getAllHeaders())
                Log.i(TAG, "Request header: " + header.getName() + " - " + header.getValue());
        }
        HttpResponse response = null;
        try {
            response = getClient().execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public String buildResponseString(HttpResponse response) {
        if (!silent)
            Log.i(TAG, "Generating response string from response");
        System.gc();
        StringBuilder builder = new StringBuilder();
        try {
            InputStreamReader inputReader = new InputStreamReader(response.getEntity().getContent());
            BufferedReader reader = new BufferedReader(inputReader, 8192);
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line);
            reader.close();
            inputReader.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public String buildQueryString(Bundle parameters) {
        if (!silent)
            Log.i(TAG, "Generating query string from parameters");
        StringBuilder builder = new StringBuilder();
        for (String key : parameters.keySet()) {
            if (builder.length() > 0)
                builder.append("&");
            Object value = parameters.get(key);
            if (value instanceof String[]) {
                for (String item : (String[]) value) {
                    if (builder.length() > 0)
                        builder.append("&");
                    builder.append(key + "[]=" + item);
                }
            } else {
                builder.append(key + "=" + value);
            }
        }
        return builder.toString();
    }

    public JSONObject buildJsonObject(String jsonString) {
        if (!silent)
            Log.i(TAG, "Generating JSON from string");
        JSONObject data = new JSONObject();
        try {
            JSONObject errors = new JSONObject();
            errors.put(RESPONSE_ERROR_MESSAGE, "Error processing response from server");
            data.put(BaseAPI.RESPONSE_ERRORS, errors);
            data = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public JSONObject buildJsonObject(Bundle parameters) {
        if (!silent)
            Log.i(TAG, "Generating JSON from parameters");
        JSONObject container = new JSONObject();
        JSONObject json = new JSONObject();
        String wrapper = parameters.getString(FIELD_OUTER_KEY);
        parameters.remove(FIELD_OUTER_KEY);
        if (null == wrapper) {
            json = BundleBuilder.instance(parameters).buildJson();
        } else {
            container = BundleBuilder.instance(parameters).buildJson();
            try {
                json.put(wrapper, container);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    public static enum Type {
        GET, POST, PUT, DELETE, HEAD, OPTION
    }

    public static final class FIELDS {
        public static final String OUTER_KEY = "outer_key";

        public static final class HEADER {
            public static final String AUTH_TOKEN = "auth_token";
        }

        public static final class INPUT {
            public static final String OUTER_KEY = "outer_key";
        }

        public static final class OUTPUT {
            public static final String RESULT = "result";
            public static final String ERRORS = "errors";
            public static final String ERROR_MESSAGE = "msg";
            public static final String ERROR_URI = "uri";
            public static final String DETAIL_MESSAGE = "message";
            public static final String STATUS = TAG + "response_code";
            public static final String MESSAGE = TAG + "response_message";
        }
    }
}
