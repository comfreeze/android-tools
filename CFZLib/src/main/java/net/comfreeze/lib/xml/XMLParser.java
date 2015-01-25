package net.comfreeze.lib.xml;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

abstract public class XMLParser extends DefaultHandler {
    private static final String TAG = XMLParser.class.getSimpleName();

    protected InputStream source = null;

    protected StringBuffer buff = null;

    protected boolean buffering = false;

    protected int currentIndex;

    public static boolean silent = true;//CFZApplication.silent;

    public XMLParser() {
        if (!silent)
            Log.d(TAG, "Initalizing");
        currentIndex = 0;
    }

    public XMLParser(InputStream in) {
        if (!silent)
            Log.d(TAG, "Initalizing");
        currentIndex = 0;
        source = in;
    }

    public XMLParser setSource(InputStream source) {
        this.source = source;
        return this;
    }

    public void process() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = null;
        try {
            sp = spf.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        XMLReader xr = null;
        try {
            xr = sp.getXMLReader();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        xr.setContentHandler(this);
        try {
            xr.parse(new InputSource(source));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes each element encountered, in order discovered in the source
     * document.
     *
     * @param namespace
     * @param element
     * @param qualifiedName
     * @param attributes
     */
    protected abstract void processElement(String namespace, String element, String qualifiedName, Attributes attributes);

    /**
     * Finalizes each element encountered, in order discovered in the source
     * document.
     *
     * @param namespace
     * @param element
     * @param qualifiedName
     */
    protected abstract void finishElement(String namespace, String element, String qualifiedName);

    @Override
    public void startDocument() throws SAXException {
        if (!silent)
            Log.d(TAG, "Parsing document");
        // Initialization of parsing for document
        currentIndex = 0;
    }

    @Override
    public void endDocument() throws SAXException {
        if (!silent)
            Log.d(TAG, "Closing document");
        // Finalization logic upon completion of parsing
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        if (!silent)
            Log.d(TAG, "Starting element: " + namespaceURI + ":" + localName);
        this.processElement(namespaceURI, localName, qName, attrs);
    }

    @Override
    public void characters(char ch[], int start, int length) {
        if (buffering) {
            if (!silent)
                Log.d(TAG, "Buffering characters");
            if (buff == null) {
                buff = new StringBuffer();
            }
            buff.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (!silent)
            Log.d(TAG, "Finished element: " + namespaceURI + ":" + localName);
        finishElement(namespaceURI, localName, qName);
    }

    protected void log(String message) {
        if (!silent)
            Log.d(TAG, message);
    }
}