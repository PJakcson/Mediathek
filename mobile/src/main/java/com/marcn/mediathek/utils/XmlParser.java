package com.marcn.mediathek.utils;

import android.content.Context;
import android.util.Xml;

import com.marcn.mediathek.R;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.LiveStreams;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class XmlParser {

    public static ArrayList<LiveStream> getZDFLiveStreamData(Context c,ArrayList<LiveStream> ls) throws IOException {
        String url =  c.getString(R.string.zdf_live_api);
        InputStream is = NetworkTasks.downloadStringDataAsInputStream(url);
        if (is == null) return ls;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            parser.nextTag();
            return readZdfLiveStreamXml(parser, ls);
        } catch (Exception ignored) {
        } finally {
            is.close();
        }
        return null;
    }

    private static ArrayList<LiveStream> readZdfLiveStreamXml(XmlPullParser parser, ArrayList<LiveStream> ls) throws XmlPullParserException, IOException {
        String title = "", id = "", thumbnail = "", logo = "";
        parser.require(XmlPullParser.START_TAG, null, "response");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("teaser") && !parser.getAttributeValue(null, "member").equals("onAir"))
                skip(parser);
            else if (name.equals("teaserimage")) {
                if (parser.getAttributeValue(null, "key").equals("946x532")) {
                    thumbnail = readText(parser);
                }
            } else if (name.equals("assetId")) {
                id = readText(parser);
            } else if (name.equals("channel")) {
                title = readText(parser);
            }else if (name.equals("channelLogoSmall")) {
                logo = readText(parser);

                int index = LiveStreams.indexOfName(ls, title);
                if (index >= 0) {
                    ls.get(index).setThumb_url(thumbnail);
                    ls.get(index).setLogo_url(logo);
                }
            }
        }
        return ls;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
