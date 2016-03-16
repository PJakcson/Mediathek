package com.marcn.mediathek.stations;

import android.support.annotation.Nullable;

import com.marcn.mediathek.StationUtils.ArdUtils;
import com.marcn.mediathek.base_objects.Episode;
import com.marcn.mediathek.base_objects.LiveStream;
import com.marcn.mediathek.base_objects.LiveStreamM3U8;
import com.marcn.mediathek.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

public class ArdGroup extends Station {
    private static final String ard_live_api = "http://www.ardmediathek.de/tv/live";
    public static final String ARD_BASE_URL = "http://www.ardmediathek.de";
    private static final String ard_category_url = ARD_BASE_URL + "/Tipps?";

    private static final String widget_key_neuste = "Neuste Videos";
    private static final String widget_key_ausgewaehlte_filme = "Ausgewählte Filme";
    private static final String widget_key_ausgewaehlte_dokus = "Ausgewählte Dokus & Reportagen";
    private static final String widget_key_meist = "Meistabgerufene Videos";

    public ArdGroup(String title) {
        this.title = title;

        // Setup Episode - Widgets
        top_level_categories = new LinkedHashMap<>();
        top_level_categories.put(widget_key_neuste, "23644268");
        top_level_categories.put(widget_key_ausgewaehlte_filme, "33649088");
        top_level_categories.put(widget_key_ausgewaehlte_dokus, "33649086");
        top_level_categories.put(widget_key_meist, "23644244");

        // Setup Episode - Widgets
        episode_widgets = new LinkedHashMap<>();
    }

    @Override
    public LiveStreamM3U8 getLiveStream() {
        String url = "";
        switch (title) {
            // ARD
            case Constants.TITLE_CHANNEL_ARD: url = Constants.LIVE_STREAM_CHANNEL_ARD; break;
            case Constants.TITLE_CHANNEL_ARD_ALPHA: url = Constants.LIVE_STREAM_CHANNEL_ARD_ALPHA; break;
            case Constants.TITLE_CHANNEL_TAGESSCHAU: url = Constants.LIVE_STREAM_CHANNEL_TAGESSCHAU; break;
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: url = Constants.LIVE_STREAM_CHANNEL_SWR; break;
            case Constants.TITLE_CHANNEL_WDR: url = Constants.LIVE_STREAM_CHANNEL_WDR; break;
            case Constants.TITLE_CHANNEL_MDR: url = Constants.LIVE_STREAM_CHANNEL_MDR; break;
            case Constants.TITLE_CHANNEL_NDR: url = Constants.LIVE_STREAM_CHANNEL_NDR; break;
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: url = Constants.LIVE_STREAM_CHANNEL_BR; break;
            case Constants.TITLE_CHANNEL_SR: url = Constants.LIVE_STREAM_CHANNEL_SR; break;
            case Constants.TITLE_CHANNEL_RBB: url = Constants.LIVE_STREAM_CHANNEL_RBB; break;
        }
        return url.isEmpty() ? null : new LiveStreamM3U8(url);
    }

    @Override
    @Nullable
    public Episode getCurrentEpisode() {
        return ArdUtils.getCurrentEpisode(this, ard_live_api, ARD_BASE_URL);
    }

    @Override
    public ArrayList<Episode> fetchCategoryEpisodes(String key, int limit, int offset) {
        return null;
    }

    @Nullable
    @Override
    public ArrayList<Episode> fetchWidgetEpisodes(String key, String assetId, int count) {
        String id = top_level_categories.get(key);
        if (id == null) return null;
        String url = ard_category_url + "documentId=" + id + "&mcontent=page.1";
        return ArdUtils.fetchEpisodeList(url);
    }

    @Override
    public String getRecommendedUrl(int offset, int limit) {
        return null;
    }

    @Override
    public String getMostViewsUrl(int offset, int limit) {
        return null;
    }

    @Override
    public String getMostRecentUrl(int offset, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Nullable
    public ArrayList<Episode> getMostRecentEpisodes(int offset, int limit, Calendar startDate, Calendar endDate) {
        return null;
    }

    @Override
    public String getStationId() {
        switch (title) {
            // ARD
            case Constants.TITLE_CHANNEL_ARD: return "208";
            case Constants.TITLE_CHANNEL_ARD_ALPHA: return "5868";
            case Constants.TITLE_CHANNEL_TAGESSCHAU: return "5878";
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: return "5904";
            case Constants.TITLE_CHANNEL_WDR: return "5902";
            case Constants.TITLE_CHANNEL_MDR: return "1386804";
            case Constants.TITLE_CHANNEL_NDR: return "21518352";
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: return "21518950";
            case Constants.TITLE_CHANNEL_SR: return "5870";
            case Constants.TITLE_CHANNEL_RBB: return "21518358";
            // KiKa
            case Constants.TITLE_CHANNEL_KIKA: return "5886";
        }
        return null;
    }

    public String getLiveQueryString() {
        switch (title) {
            // ARD
            case Constants.TITLE_CHANNEL_ARD: return LiveStream.ARD_QUERY;
            case Constants.TITLE_CHANNEL_ARD_ALPHA: return LiveStream.ARD_ALPHA_QUERY;
            case Constants.TITLE_CHANNEL_TAGESSCHAU: return LiveStream.TAGESSCHAU_QUERY;
            // Regional 1
            case Constants.TITLE_CHANNEL_SWR: return LiveStream.SWR_QUERY;
            case Constants.TITLE_CHANNEL_WDR: return LiveStream.WDR_QUERY;
            case Constants.TITLE_CHANNEL_MDR: return LiveStream.MDR_QUERY;
            case Constants.TITLE_CHANNEL_NDR: return LiveStream.NDR_QUERY;
            // Regional 2
            case Constants.TITLE_CHANNEL_BR: return LiveStream.BR_QUERY;
            case Constants.TITLE_CHANNEL_SR: return LiveStream.SR_QUERY;
            case Constants.TITLE_CHANNEL_RBB: return LiveStream.RBB_QUERY;
            // KiKa
            case Constants.TITLE_CHANNEL_KIKA: return LiveStream.KIKA_QUERY;
        }
        return null;
    }
}
