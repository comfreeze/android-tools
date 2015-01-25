package net.comfreeze.lib.xml.wordpress;

import net.comfreeze.lib.xml.XMLParser;

import org.xml.sax.Attributes;

import java.io.InputStream;
import java.util.ArrayList;

public class FeedXmlParser extends XMLParser {
    private static final String TAG = FeedXmlParser.class.getSimpleName();

    public ArrayList<FeedItem> items = null;

    public FeedItem current = null;

    public String currentKey = null;

    public FeedXmlParser() {
        super();
        items = new ArrayList<FeedItem>();
    }

    public FeedXmlParser(InputStream in) {
        super(in);
        items = new ArrayList<FeedItem>();
        process();
    }

    @Override
    public FeedXmlParser setSource(InputStream source) {
        super.setSource(source);
        if (null != source)
            process();
        return this;
    }

    @Override
    protected void processElement(String namespace, String element, String qualifiedName, Attributes attributes) {
        if (element.equals("item")) {
            log("Started new item");
            current = new FeedItem();
        }
        if (element.equals("title")) {
            if (current != null) {
                buffering = true;
            }
        }
        if (element.equals("description")) {
            if (current != null) {
                buffering = true;
            }
        }
        if (element.equals("encoded")) {
            if (current != null) {
                buffering = true;
            }
        }
        if (element.equals("link")) {
            if (current != null) {
                buffering = true;
            }
        }
        if (element.equals("pubDate")) {
            if (current != null) {
                buffering = true;
            }
        }
    }

    @Override
    protected void finishElement(String namespace, String element, String qualifiedName) {
        if (element.equals("item")) {
            items.add(current);
            current = null;
            currentIndex++;
            log("Finished item");
        }
        if (element.equals("title")) {
            if (current != null) {
                buffering = false;
                current.title = buff.toString();
                buff = null;
            }
        }
        if (element.equals("description")) {
            if (current != null) {
                buffering = false;
                current.description = buff.toString();
                buff = null;
            }
        }
        if (element.equals("encoded")) {
            if (current != null) {
                buffering = false;
                current.content = buff.toString();
                buff = null;
            }
        }
        if (element.equals("link")) {
            if (current != null) {
                buffering = false;
                current.link = buff.toString();
                buff = null;
            }
        }
        if (element.equals("pubDate")) {
            if (current != null) {
                buffering = false;
                current.date = buff.toString();
                buff = null;
            }
        }
    }

    public static class FeedItem {
        public String title;
        public String description;
        public String content;
        public String link;
        public String date;
    }
}