package net.comfreeze.lib.api;

import android.os.Bundle;
import android.util.Log;

import net.comfreeze.lib.xml.XMLParser;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

abstract public class XMLAPI extends BaseAPI {
    private static final String TAG = XMLAPI.class.getSimpleName();

    public static final String CONTENT_TYPE = "text/xml";

    private XMLParser parser = null;

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    public XMLAPI setParser(XMLParser parser) {
        this.parser = parser;
        return this;
    }

    public XMLParser getParser() {
        return parser;
    }

    public InputStream list(Bundle parameters) {
        if (!silent)
            Log.d(TAG, "GET: " + getEndpoint());
        if (null == getParser()) {
            Log.e(TAG, "No XML parser provided!");
            return null;
        }
        HttpResponse response = null;
        HttpGet request = (HttpGet) getRequest(Type.GET, getEndpoint());
        if (null != parameters && parameters.size() > 0)
            request.setURI(URI.create(request.getURI().toString() + "?" + buildQueryString(parameters)));
        if (!silent)
            Log.d(TAG, "REQUEST: " + request.getURI().toString());
        response = execute(request);
        try {
            InputStream is = response.getEntity().getContent();
            return is;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}