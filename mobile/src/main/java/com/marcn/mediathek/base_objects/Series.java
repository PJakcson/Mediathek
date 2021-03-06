package com.marcn.mediathek.base_objects;

public class Series {
    public String title, shortTitle, detail, thumb_url_low, thumb_url_high, vcmsUrl, member;
    public int assetId;
    private String stationTitle;
    public Station station;
    public boolean isHeader;

    public Series(String title, String shortTitle, String detail,
                  String thumb_url_low, String thumb_url_high, String channel, String vcmsUrl,
                  int assetId, String member) {
        this.title = title;
        this.shortTitle = shortTitle;
        this.detail = detail;
        this.thumb_url_low = thumb_url_low;
        this.thumb_url_high = thumb_url_high;
        this.vcmsUrl = vcmsUrl;
        this.assetId = assetId;
        this.member = member;

        this.stationTitle = channel;
        this.station = new Station(channel);
    }

    public static Series createSendungHeader(String title) {
        Series s = new Series(title, "", "", "", "", "", "", 0, "");
        s.isHeader = true;
        return s;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Series))
            return false;
        Series v = (Series) o;
        return v.assetId == this.assetId;
    }

    public String getStationTitle() {
        return stationTitle;
    }
}
