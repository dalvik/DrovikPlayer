package com.drovik.player.weather;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ICityResponse {
    private ArrayList<Data> HeWeather6;

    public ICityResponse() {
    }

    public ArrayList<Data> getHeWeather6() {
        return HeWeather6;
    }

    public void setHeWeather6(ArrayList<Data> heWeather6) {
        HeWeather6 = heWeather6;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if(HeWeather6 != null) {
            for(Data data:HeWeather6) {
                sb.append(data.toString());
            }
        }
        return sb.toString();
    }

    public static class Data{
        private String status;
        private ArrayList<BasicData> basic;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public ArrayList<BasicData> getBasic() {
            return basic;
        }

        public void setBasic(ArrayList<BasicData> basic) {
            this.basic = basic;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            if(basic != null) {
                for(BasicData data:basic) {
                    sb.append(data.toString());
                }
            }
            return sb.toString();
        }

        public static class BasicData{
            private String cid;
            private String location;
            private String parent_city;
            private String admin_area;
            private String cnty;
            private String lat;
            private String lon;
            private String tz;
            private String type;

            public BasicData() {

            }

            public BasicData(String jsonString) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    cid = jsonObject.optString("cic");
                    location = jsonObject.optString("location");
                    parent_city = jsonObject.optString("parent_city");
                    admin_area = jsonObject.optString("admin_area");
                    cnty = jsonObject.optString("cnty");
                    lat = jsonObject.optString("lat");
                    lon = jsonObject.optString("lon");
                    tz = jsonObject.optString("tz");
                    type = jsonObject.optString("type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public String getCid() {
                return cid;
            }

            public void setCid(String cid) {
                this.cid = cid;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getParent_city() {
                return parent_city;
            }

            public void setParent_city(String parent_city) {
                this.parent_city = parent_city;
            }

            public String getAdmin_area() {
                return admin_area;
            }

            public void setAdmin_area(String admin_area) {
                this.admin_area = admin_area;
            }

            public String getCnty() {
                return cnty;
            }

            public void setCnty(String cnty) {
                this.cnty = cnty;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLon() {
                return lon;
            }

            public void setLon(String lon) {
                this.lon = lon;
            }

            public String getTz() {
                return tz;
            }

            public void setTz(String tz) {
                this.tz = tz;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            @Override
            public String toString() {
                return "BasicData{" +
                        "cid='" + cid + '\'' +
                        ", location='" + location + '\'' +
                        ", parent_city='" + parent_city + '\'' +
                        ", admin_area='" + admin_area + '\'' +
                        ", cnty='" + cnty + '\'' +
                        ", lat='" + lat + '\'' +
                        ", lon='" + lon + '\'' +
                        ", tz='" + tz + '\'' +
                        ", type='" + type + '\'' +
                        '}';
            }

            public String toJSONString(){
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("cid", cid);
                    jsonObject.put("location", location);
                    jsonObject.put("parent_city", parent_city);
                    jsonObject.put("admin_area", admin_area);
                    jsonObject.put("cnty", cnty);
                    jsonObject.put("lat", lat);
                    jsonObject.put("lon", lon);
                    jsonObject.put("tz", tz);
                    jsonObject.put("type", type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonObject.toString();
            }
        }
    }

}
