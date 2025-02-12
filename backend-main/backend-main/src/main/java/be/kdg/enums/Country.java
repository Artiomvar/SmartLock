package be.kdg.enums;

public enum Country {
    BELGIUM(50.5039, 4.4699),
    NETHERLANDS(52.1326, 5.2913),
    DENMARK(56.2639, 9.5018),
    SWEDEN(60.1282, 18.6435),
    FINLAND(61.9241, 25.7482),
    ENGLAND(52.3555, -1.1743),
    USA(37.0902, -95.7129),
    CANADA(56.1304, -106.3468),
    GERMANY(51.1657, 10.4515),
    FRANCE(46.6034, 1.8883),
    INDIA(20.5937, 78.9629),
    AUSTRALIA(-25.2744, 133.7751),
    CHINA(35.8617, 104.1954),
    JAPAN(36.2048, 138.2529),
    SOUTH_AFRICA(-30.5595, 22.9375),
    BRAZIL(-14.2350, -51.9253);

    private final double latitude;
    private final double longitude;

    Country(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}