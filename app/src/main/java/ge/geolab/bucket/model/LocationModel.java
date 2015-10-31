package ge.geolab.bucket.model;

import java.io.Serializable;

/**
 * Created by Jay on 4/10/2015.
 */
public class LocationModel implements Serializable {
    private double longitude, latitude;
    private String address;

    public LocationModel(double longitude, double latitude, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "LocationModel{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", address='" + address + '\'' +
                '}';
    }
}
