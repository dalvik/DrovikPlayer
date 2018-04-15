package com.android.library.location;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by sky on 2016/2/25.
 */
public class MyLocation implements Parcelable {
    private double longitude;
    private double latitude;
    private String addrStr;
    private String city;
    private String cityCode;
    private String buildingID;
    private String buildingName;
    private float speed;
    private String street;
    private String streetNumber;
    private int satelliteNumber;
    private float accuracy;
    private String country;
    private int describeContents;
    private String time;
    private float radius;
    private List poiList;
    private String province;
    private int locType;
    private float direction;
    private double altitude;

    protected MyLocation() {

    }
    protected MyLocation(Parcel in) {
        if(in != null){
            longitude = in.readDouble();
            latitude = in.readDouble();
            addrStr = in.readString();
            city = in.readString();
            cityCode = in.readString();
            buildingID = in.readString();
            buildingName = in.readString();
            speed = in.readFloat();
            street = in.readString();
            streetNumber = in.readString();
            satelliteNumber = in.readInt();
            accuracy = in.readFloat();
            country = in.readString();
            describeContents = in.readInt();
            time = in.readString();
            radius = in.readFloat();
            //poiList = in.read
            province = in.readString();
            locType = in.readInt();
            direction = in.readFloat();
            altitude = in.readDouble();
        }
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

    public String getAddrStr() {
        return addrStr;
    }

    public void setAddrStr(String addrStr) {
        this.addrStr = addrStr;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getBuildingID() {
        return buildingID;
    }

    public void setBuildingID(String buildingID) {
        this.buildingID = buildingID;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public int getSatelliteNumber() {
        return satelliteNumber;
    }

    public void setSatelliteNumber(int satelliteNumber) {
        this.satelliteNumber = satelliteNumber;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getDescribeContents() {
        return describeContents;
    }

    public void setDescribeContents(int describeContents) {
        this.describeContents = describeContents;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public List getPoiList() {
        return poiList;
    }

    public void setPoiList(List poiList) {
        this.poiList = poiList;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getLocType() {
        return locType;
    }

    public void setLocType(int locType) {
        this.locType = locType;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public static final Creator<MyLocation> CREATOR = new Creator<MyLocation>() {
        @Override
        public MyLocation createFromParcel(Parcel in) {
            return new MyLocation(in);
        }

        @Override
        public MyLocation[] newArray(int size) {
            return new MyLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public String toString(){
        return "[ longitude:" + longitude + ",latitude:" + latitude+ " ]";
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(dest != null){
            dest.writeDouble(longitude);
            dest.writeDouble(latitude);
            dest.writeString(addrStr);
            dest.writeString(city);
            dest.writeString(cityCode);
            dest.writeString(buildingID);
            dest.writeString(buildingName);
            dest.writeFloat(speed);
            dest.writeString(street);
            dest.writeString(streetNumber);
            dest.writeInt(satelliteNumber);
            dest.writeFloat(accuracy);
            dest.writeString(country);
            dest.writeInt(describeContents);
            dest.writeString(time);
            dest.writeFloat(radius);
            //poiList = in.read
            dest.writeString(province);
            dest.writeInt(locType);
            dest.writeFloat(direction);
            dest.writeDouble(altitude);
        }
    }
}
