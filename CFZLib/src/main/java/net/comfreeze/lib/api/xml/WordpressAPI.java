package net.comfreeze.lib.api.xml;

import android.os.Bundle;
import android.util.Log;

import net.comfreeze.lib.api.XMLAPI;
import net.comfreeze.lib.xml.XMLParser;
import net.comfreeze.lib.xml.wordpress.FeedXmlParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

abstract public class WordpressAPI extends XMLAPI {
    private static final String TAG = WordpressAPI.class.getSimpleName();

    public WordpressAPI() {
        setParser(new FeedXmlParser());
    }

    @Override
    public WordpressAPI setParser(XMLParser parser) {
        return (WordpressAPI) super.setParser(parser);
    }

    @Override
    public FeedXmlParser getParser() {
        return (FeedXmlParser) super.getParser();
    }

    public ArrayList<FeedXmlParser.FeedItem> process(InputStream is) {
        if (null == getParser()) {
            Log.e(TAG, "No XML parser provided!");
            return null;
        }
        return getParser().setSource(is).items;
    }

    public ArrayList<FeedXmlParser.FeedItem> items(Bundle parameters) {
        InputStream is = super.list(parameters);
        ArrayList<FeedXmlParser.FeedItem> items = null;
        if (null != is) {
            items = process(is);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return items;
    }
}