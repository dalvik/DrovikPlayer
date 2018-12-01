package com.drovik.player.weather;

import com.drovik.player.R;
import com.drovik.player.weather.data.BaseAdapterData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class IWeatherResponse {
    private ArrayList<Data> HeWeather6;

    public IWeatherResponse() {
    }

    public static class Data {
        private Basic basic;
        private Update update;
        private ArrayList<DailyForecast> daily_forecast;
        private Now now;
        private ArrayList<Hourly> hourly;
        private ArrayList<LifeStyle> lifestyle;
        private ArrayList<LifestyleForecast> lifestyle_forecast;
        private String status;
        private AirNowCity air_now_city;
        private ArrayList<AirNowStation> air_now_station;

        public Basic getBasic() {
            return basic;
        }

        public void setBasic(Basic basic) {
            this.basic = basic;
        }

        public Update getUpdate() {
            return update;
        }

        public void setUpdate(Update update) {
            this.update = update;
        }

        public ArrayList<DailyForecast> getDaily_forecast() {
            return daily_forecast;
        }

        public void setDaily_forecast(ArrayList<DailyForecast> daily_forecast) {
            this.daily_forecast = daily_forecast;
        }

        public Now getNow() {
            return now;
        }

        public void setNow(Now now) {
            this.now = now;
        }

        public ArrayList<Hourly> getHourly() {
            return hourly;
        }

        public void setHourly(ArrayList<Hourly> hourly) {
            this.hourly = hourly;
        }

        public ArrayList<LifeStyle> getLifestyle() {
            return lifestyle;
        }

        public void setLifestyle(ArrayList<LifeStyle> lifestyle) {
            this.lifestyle = lifestyle;
        }

        public ArrayList<LifestyleForecast> getLifestyle_forecast() {
            return lifestyle_forecast;
        }

        public void setLifestyle_forecast(ArrayList<LifestyleForecast> lifestyle_forecast) {
            this.lifestyle_forecast = lifestyle_forecast;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public AirNowCity getAir_now_city() {
            return air_now_city;
        }

        public void setAir_now_city(AirNowCity air_now_city) {
            this.air_now_city = air_now_city;
        }

        public ArrayList<AirNowStation> getAir_now_station() {
            return air_now_station;
        }

        public void setAir_now_station(ArrayList<AirNowStation> air_now_station) {
            this.air_now_station = air_now_station;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "basic=" + basic +
                    ", update=" + update +
                    ", daily_forecast=" + daily_forecast +
                    ", now=" + now +
                    ", hourly=" + hourly +
                    ", lifestyle=" + lifestyle +
                    ", lifestyle_forecast=" + lifestyle_forecast +
                    ", status='" + status + '\'' +
                    '}';
        }

        public static class Basic{
            private String cid;
            private String location;
            private String parent_city;
            private String admin_area;
            private String cnty;
            private String lat;
            private String lon;
            private String tz;
            private String type;

            public Basic() {

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
        }

        public static class Update {
            private String loc;
            private String utc;
            public Update() {

            }

            public String getLoc() {
                return loc;
            }

            public void setLoc(String loc) {
                this.loc = loc;
            }

            public String getUtc() {
                return utc;
            }

            public void setUtc(String utc) {
                this.utc = utc;
            }

            @Override
            public String toString() {
                return "Update{" +
                        "loc='" + loc + '\'' +
                        ", utc='" + utc + '\'' +
                        '}';
            }
        }

        public static class DailyForecast{
            private String cond_code_d;
            private String cond_code_n;
            private String cond_txt_d;
            private String cond_txt_n;
            private String date;
            private String hum;
            private String mr;
            private String ms;
            private String pcpn;
            private String pop;
            private String pres;
            private String sr;
            private String ss;
            private String tmp_max;
            private String tmp_min;
            private String uv_index;
            private String vis;
            private String wind_deg;
            private String wind_dir;
            private String wind_sc;
            private String wind_spd;

            public DailyForecast() {
            }

            public DailyForecast(String jsonString) {
                try {
                    JSONObject object = new JSONObject(jsonString);
                    cond_code_d = object.optString("cond_code_d");
                    cond_code_n = object.optString("cond_code_n");
                    cond_txt_d = object.optString("cond_txt_d");
                    cond_txt_n = object.optString("cond_txt_n");
                    date = object.optString("date");
                    hum = object.optString("hum");
                    mr = object.optString("mr");
                    ms = object.optString("ms");
                    pcpn = object.optString("pcpn");
                    pop = object.optString("pop");
                    pres = object.optString("pres");
                    sr = object.optString("sr");
                    ss = object.optString("ss");
                    tmp_max = object.optString("tmp_max");
                    tmp_min = object.optString("tmp_min");
                    uv_index = object.optString("uv_index");
                    vis = object.optString("vis");
                    wind_deg = object.optString("wind_deg");
                    wind_dir = object.optString("wind_dir");
                    wind_sc = object.optString("wind_sc");
                    wind_spd = object.optString("wind_spd");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public String getCond_code_d() {
                return cond_code_d;
            }

            public void setCond_code_d(String cond_code_d) {
                this.cond_code_d = cond_code_d;
            }

            public String getCond_code_n() {
                return cond_code_n;
            }

            public void setCond_code_n(String cond_code_n) {
                this.cond_code_n = cond_code_n;
            }

            public String getCond_txt_d() {
                return cond_txt_d;
            }

            public void setCond_txt_d(String cond_txt_d) {
                this.cond_txt_d = cond_txt_d;
            }

            public String getCond_txt_n() {
                return cond_txt_n;
            }

            public void setCond_txt_n(String cond_txt_n) {
                this.cond_txt_n = cond_txt_n;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getHum() {
                return hum;
            }

            public void setHum(String hum) {
                this.hum = hum;
            }

            public String getMr() {
                return mr;
            }

            public void setMr(String mr) {
                this.mr = mr;
            }

            public String getMs() {
                return ms;
            }

            public void setMs(String ms) {
                this.ms = ms;
            }

            public String getPcpn() {
                return pcpn;
            }

            public void setPcpn(String pcpn) {
                this.pcpn = pcpn;
            }

            public String getPop() {
                return pop;
            }

            public void setPop(String pop) {
                this.pop = pop;
            }

            public String getPres() {
                return pres;
            }

            public void setPres(String pres) {
                this.pres = pres;
            }

            public String getSr() {
                return sr;
            }

            public void setSr(String sr) {
                this.sr = sr;
            }

            public String getSs() {
                return ss;
            }

            public void setSs(String ss) {
                this.ss = ss;
            }

            public String getTmp_max() {
                return tmp_max;
            }

            public void setTmp_max(String tmp_max) {
                this.tmp_max = tmp_max;
            }

            public String getTmp_min() {
                return tmp_min;
            }

            public void setTmp_min(String tmp_min) {
                this.tmp_min = tmp_min;
            }

            public String getUv_index() {
                return uv_index;
            }

            public void setUv_index(String uv_index) {
                this.uv_index = uv_index;
            }

            public String getVis() {
                return vis;
            }

            public void setVis(String vis) {
                this.vis = vis;
            }

            public String getWind_deg() {
                return wind_deg;
            }

            public void setWind_deg(String wind_deg) {
                this.wind_deg = wind_deg;
            }

            public String getWind_dir() {
                return wind_dir;
            }

            public void setWind_dir(String wind_dir) {
                this.wind_dir = wind_dir;
            }

            public String getWind_sc() {
                return wind_sc;
            }

            public void setWind_sc(String wind_sc) {
                this.wind_sc = wind_sc;
            }

            public String getWind_spd() {
                return wind_spd;
            }

            public void setWind_spd(String wind_spd) {
                this.wind_spd = wind_spd;
            }

            public JSONObject toJsonObject() {
                JSONObject object = new JSONObject();
                try {
                    object.put("cond_code_d", cond_code_d);
                    object.put("cond_code_n", cond_code_n);
                    object.put("cond_txt_d", cond_txt_d);
                    object.put("cond_txt_n", cond_txt_n);
                    object.put("date", date);
                    object.put("hum", hum);
                    object.put("mr", mr);
                    object.put("ms", ms);
                    object.put("pcpn", pcpn);
                    object.put("pop", pop);
                    object.put("pres", pres);
                    object.put("sr", sr);
                    object.put("ss", ss);
                    object.put("tmp_max", tmp_max);
                    object.put("tmp_min", tmp_min);
                    object.put("tmp_min", tmp_min);
                    object.put("vis", vis);
                    object.put("wind_deg", wind_deg);
                    object.put("wind_dir", wind_dir);
                    object.put("wind_sc", wind_sc);
                    object.put("wind_spd", wind_spd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public String toString() {
                return "DailyForecast{" +
                        "cond_code_d='" + cond_code_d + '\'' +
                        ", cond_code_n='" + cond_code_n + '\'' +
                        ", cond_txt_d='" + cond_txt_d + '\'' +
                        ", cond_txt_n='" + cond_txt_n + '\'' +
                        ", date='" + date + '\'' +
                        ", hum='" + hum + '\'' +
                        ", mr='" + mr + '\'' +
                        ", ms='" + ms + '\'' +
                        ", pcpn='" + pcpn + '\'' +
                        ", pop='" + pop + '\'' +
                        ", pres='" + pres + '\'' +
                        ", sr='" + sr + '\'' +
                        ", ss='" + ss + '\'' +
                        ", tmp_max='" + tmp_max + '\'' +
                        ", tmp_min='" + tmp_min + '\'' +
                        ", uv_index='" + uv_index + '\'' +
                        ", vis='" + vis + '\'' +
                        ", wind_deg='" + wind_deg + '\'' +
                        ", wind_dir='" + wind_dir + '\'' +
                        ", wind_sc='" + wind_sc + '\'' +
                        ", wind_spd='" + wind_spd + '\'' +
                        '}';
            }
        }

        public static class Now {
            private String fl;//体感温度，默认单位：摄氏度
            private String tmp;//温度，默认单位：摄氏度
            private String cond_code;//实况天气状况代码
            private String cond_txt;//实况天气状况描述
            private String wind_deg;//风向360角度
            private String wind_dir;//风向
            private String wind_sc;//风力
            private String wind_spd;//风速，公里/小时
            private String hum;//相对湿度
            private String pcpn;//降水量
            private String pres;//大气压强
            private String vis;//能见度，默认单位：公里
            private String cloud;//云量

            public Now() {

            }

            public String getFl() {
                return fl;
            }

            public void setFl(String fl) {
                this.fl = fl;
            }

            public String getTmp() {
                return tmp;
            }

            public void setTmp(String tmp) {
                this.tmp = tmp;
            }

            public String getCond_code() {
                return cond_code;
            }

            public void setCond_code(String cond_code) {
                this.cond_code = cond_code;
            }

            public String getCond_txt() {
                return cond_txt;
            }

            public void setCond_txt(String cond_txt) {
                this.cond_txt = cond_txt;
            }

            public String getWind_deg() {
                return wind_deg;
            }

            public void setWind_deg(String wind_deg) {
                this.wind_deg = wind_deg;
            }

            public String getWind_dir() {
                return wind_dir;
            }

            public void setWind_dir(String wind_dir) {
                this.wind_dir = wind_dir;
            }

            public String getWind_sc() {
                return wind_sc;
            }

            public void setWind_sc(String wind_sc) {
                this.wind_sc = wind_sc;
            }

            public String getWind_spd() {
                return wind_spd;
            }

            public void setWind_spd(String wind_spd) {
                this.wind_spd = wind_spd;
            }

            public String getHum() {
                return hum;
            }

            public void setHum(String hum) {
                this.hum = hum;
            }

            public String getPcpn() {
                return pcpn;
            }

            public void setPcpn(String pcpn) {
                this.pcpn = pcpn;
            }

            public String getPres() {
                return pres;
            }

            public void setPres(String pres) {
                this.pres = pres;
            }

            public String getVis() {
                return vis;
            }

            public void setVis(String vis) {
                this.vis = vis;
            }

            public String getCloud() {
                return cloud;
            }

            public void setCloud(String cloud) {
                this.cloud = cloud;
            }

            @Override
            public String toString() {
                return "Now{" +
                        "fl='" + fl + '\'' +
                        ", tmp='" + tmp + '\'' +
                        ", cond_code='" + cond_code + '\'' +
                        ", cond_txt='" + cond_txt + '\'' +
                        ", wind_deg='" + wind_deg + '\'' +
                        ", wind_dir='" + wind_dir + '\'' +
                        ", wind_sc='" + wind_sc + '\'' +
                        ", wind_spd='" + wind_spd + '\'' +
                        ", hum='" + hum + '\'' +
                        ", pcpn='" + pcpn + '\'' +
                        ", pres='" + pres + '\'' +
                        ", vis='" + vis + '\'' +
                        ", cloud='" + cloud + '\'' +
                        '}';
            }
        }

        public static class Hourly {
            private String time;//预报时间，格式yyyy-MM-dd hh:mm
            private String tmp;//温度
            private String cond_code;//天气状况代码
            private String cond_txt;//天气状况
            private String wind_deg;//风向360角度
            private String wind_dir;//风向
            private String wind_sc;//风力
            private String wind_spd;//风速，公里/小时
            private String hum;//相对湿度
            private String pres;//大气压强
            private String dew;//露点温度
            private String cloud;//云量

            public Hourly() {

            }

            public Hourly(String jsonString) {
                try {
                    JSONObject object = new JSONObject(jsonString);
                    time = object.optString("time");
                    tmp = object.optString("tmp");
                    cond_code = object.optString("cond_code");
                    cond_txt = object.optString("cond_txt");
                    wind_deg = object.optString("wind_deg");
                    wind_dir = object.optString("wind_dir");
                    wind_sc = object.optString("wind_sc");
                    wind_spd = object.optString("wind_spd");
                    hum = object.optString("hum");
                    pres = object.optString("pres");
                    dew = object.optString("dew");
                    cloud = object.optString("cloud");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getTmp() {
                return tmp;
            }

            public void setTmp(String tmp) {
                this.tmp = tmp;
            }

            public String getCond_code() {
                return cond_code;
            }

            public void setCond_code(String cond_code) {
                this.cond_code = cond_code;
            }

            public String getCond_txt() {
                return cond_txt;
            }

            public void setCond_txt(String cond_txt) {
                this.cond_txt = cond_txt;
            }

            public String getWind_deg() {
                return wind_deg;
            }

            public void setWind_deg(String wind_deg) {
                this.wind_deg = wind_deg;
            }

            public String getWind_dir() {
                return wind_dir;
            }

            public void setWind_dir(String wind_dir) {
                this.wind_dir = wind_dir;
            }

            public String getWind_sc() {
                return wind_sc;
            }

            public void setWind_sc(String wind_sc) {
                this.wind_sc = wind_sc;
            }

            public String getWind_spd() {
                return wind_spd;
            }

            public void setWind_spd(String wind_spd) {
                this.wind_spd = wind_spd;
            }

            public String getHum() {
                return hum;
            }

            public void setHum(String hum) {
                this.hum = hum;
            }

            public String getPres() {
                return pres;
            }

            public void setPres(String pres) {
                this.pres = pres;
            }

            public String getDew() {
                return dew;
            }

            public void setDew(String dew) {
                this.dew = dew;
            }

            public String getCloud() {
                return cloud;
            }

            public void setCloud(String cloud) {
                this.cloud = cloud;
            }

            public JSONObject getJSONObject() {
                JSONObject object = new JSONObject();
                try {
                    object.put("time", time);
                    object.put("tmp", tmp);
                    object.put("cond_code", cond_code);
                    object.put("cond_txt", cond_txt);
                    object.put("wind_deg", wind_deg);
                    object.put("wind_dir", wind_dir);
                    object.put("wind_sc", wind_sc);
                    object.put("wind_spd", wind_spd);
                    object.put("hum", hum);
                    object.put("pres", pres);
                    object.put("dew", dew);
                    object.put("cloud", cloud);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public String toString() {
                return "Hourly{" +
                        "time='" + time + '\'' +
                        ", tmp='" + tmp + '\'' +
                        ", cond_code='" + cond_code + '\'' +
                        ", cond_txt='" + cond_txt + '\'' +
                        ", wind_deg='" + wind_deg + '\'' +
                        ", wind_dir='" + wind_dir + '\'' +
                        ", wind_sc='" + wind_sc + '\'' +
                        ", wind_spd='" + wind_spd + '\'' +
                        ", hum='" + hum + '\'' +
                        ", pres='" + pres + '\'' +
                        ", dew='" + dew + '\'' +
                        ", cloud='" + cloud + '\'' +
                        '}';
            }
        }

        public static class LifeStyle {
            private String brf;//生活指数简介
            private String txt;//生活指数详细描述
            private String type;//生活指数类型 comf：舒适度指数、cw：洗车指数、drsg：穿衣指数、flu：感冒指数、sport：运动指数、trav：旅游指数、uv：紫外线指数、
            // air：空气污染扩散条件指数、ac：空调开启指数、ag：过敏指数、gl：太阳镜指数、mu：化妆指数、
            // airc：晾晒指数、ptfc：交通指数、fisin：钓鱼指数、spi：防晒指数

            public LifeStyle() {
            }

            public LifeStyle(String jsonString) {
                try {
                    JSONObject object = new JSONObject(jsonString);
                    brf = object.optString("brf");
                    txt = object.optString("txt");
                    type = object.optString("type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            public String getBrf() {
                return brf;
            }

            public void setBrf(String brf) {
                this.brf = brf;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            @Override
            public String toString() {
                return "LifeStyle{" +
                        "brf='" + brf + '\'' +
                        ", txt='" + txt + '\'' +
                        ", type='" + type + '\'' +
                        '}';
            }

            public JSONObject toJsonObject() {
                JSONObject object = new JSONObject();
                try {
                    object.put("brf", brf);
                    object.put("txt", txt);
                    object.put("type", type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }
        }

        public static class LifestyleForecast {
            private String date;//预报日期，例如2017-12-30
            private String brf;//生活指数简介
            private String txt;//生活指数详细描述
            private String type;

            public LifestyleForecast() {

            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getBrf() {
                return brf;
            }

            public void setBrf(String brf) {
                this.brf = brf;
            }

            public String getTxt() {
                return txt;
            }

            public void setTxt(String txt) {
                this.txt = txt;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            @Override
            public String toString() {
                return "LifestyleForecast{" +
                        "date='" + date + '\'' +
                        ", brf='" + brf + '\'' +
                        ", txt='" + txt + '\'' +
                        ", type='" + type + '\'' +
                        '}';
            }
        }

        public static class AirNowCity{
            private String pub_time;//数据发布时间,格式yyyy-MM-dd HH:mm
            private String aqi;//空气质量指数，AQI和PM25的关系
            private String main;//主要污染物 pm25
            private String qlty;//空气质量，取值范围:优，良，轻度污染，中度污染，重度污染，严重污染
            private String pm10;//pm10
            private String pm25;//pm25
            private String no2;//二氧化氮
            private String so2;//二氧化硫
            private String co;//一氧化碳
            private String o3;//臭氧

            public AirNowCity() {

            }

            public AirNowCity(String jsonString) {
                try {
                    JSONObject object = new JSONObject(jsonString);
                    pub_time = object.optString("pub_time");
                    aqi = object.optString("aqi");
                    main = object.optString("main");
                    qlty = object.optString("qlty");
                    pm10 = object.optString("pm10");
                    pm25 = object.optString("pm25");
                    no2 = object.optString("no2");
                    so2 = object.optString("so2");
                    co = object.optString("co");
                    o3 = object.optString("o3");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public String getPub_time() {
                return pub_time;
            }

            public void setPub_time(String pub_time) {
                this.pub_time = pub_time;
            }

            public String getAqi() {
                return aqi;
            }

            public void setAqi(String aqi) {
                this.aqi = aqi;
            }

            public String getMain() {
                return main;
            }

            public void setMain(String main) {
                this.main = main;
            }

            public String getQlty() {
                return qlty;
            }

            public void setQlty(String qlty) {
                this.qlty = qlty;
            }

            public String getPm10() {
                return pm10;
            }

            public void setPm10(String pm10) {
                this.pm10 = pm10;
            }

            public String getPm25() {
                return pm25;
            }

            public void setPm25(String pm25) {
                this.pm25 = pm25;
            }

            public String getNo2() {
                return no2;
            }

            public void setNo2(String no2) {
                this.no2 = no2;
            }

            public String getSo2() {
                return so2;
            }

            public void setSo2(String so2) {
                this.so2 = so2;
            }

            public String getCo() {
                return co;
            }

            public void setCo(String co) {
                this.co = co;
            }

            public String getO3() {
                return o3;
            }

            public void setO3(String o3) {
                this.o3 = o3;
            }

            public JSONObject getJsonObject() {
                JSONObject object = new JSONObject();
                try {
                    object.put("pub_time", pub_time);
                    object.put("aqi", aqi);
                    object.put("main", main);
                    object.put("qlty", qlty);
                    object.put("pm10", pm10);
                    object.put("pm25", pm25);
                    object.put("no2", no2);
                    object.put("so2", so2);
                    object.put("co", co);
                    object.put("o3", o3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }

            @Override
            public String toString() {
                return "AirNowCity{" +
                        "pub_time='" + pub_time + '\'' +
                        ", aqi='" + aqi + '\'' +
                        ", main='" + main + '\'' +
                        ", qlty='" + qlty + '\'' +
                        ", pm10='" + pm10 + '\'' +
                        ", pm25='" + pm25 + '\'' +
                        ", no2='" + no2 + '\'' +
                        ", so2='" + so2 + '\'' +
                        ", co='" + co + '\'' +
                        ", o3='" + o3 + '\'' +
                        '}';
            }
        }

        public static class AirNowStation {
            private String pub_time;//数据发布时间,格式yyyy-MM-dd HH:mm
            private String air_sta;//站点名称
            private String asid;//站点ID
            private String lat;//站点纬度
            private String lon;//站点经度
            private String aqi;//空气质量指数，AQI和PM25的关系
            private String main;//主要污染物 pm25
            private String qlty;//空气质量，取值范围:优，良，轻度污染，中度污染，重度污染，严重污染
            private String pm10;//pm10
            private String pm25;//pm25
            private String no2;//二氧化氮
            private String so2;//二氧化硫
            private String co;//一氧化碳
            private String o3;//臭氧

            public AirNowStation() {

            }

            public AirNowStation(String jsonString) {
                try {
                    JSONObject object = new JSONObject(jsonString);
                    pub_time = object.optString("pub_time");
                    air_sta = object.optString("air_sta");
                    asid = object.optString("asid");
                    lat = object.optString("lat");
                    lon = object.optString("lon");
                    aqi = object.optString("aqi");
                    main = object.optString("main");
                    qlty = object.optString("qlty");
                    pm10 = object.optString("pm10");
                    pm25 = object.optString("pm25");
                    no2 = object.optString("no2");
                    so2 = object.optString("so2");
                    co = object.optString("co");
                    o3 = object.optString("o3");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public String getPub_time() {
                return pub_time;
            }

            public void setPub_time(String pub_time) {
                this.pub_time = pub_time;
            }

            public String getAir_sta() {
                return air_sta;
            }

            public void setAir_sta(String air_sta) {
                this.air_sta = air_sta;
            }

            public String getAsid() {
                return asid;
            }

            public void setAsid(String asid) {
                this.asid = asid;
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

            public String getAqi() {
                return aqi;
            }

            public void setAqi(String aqi) {
                this.aqi = aqi;
            }

            public String getMain() {
                return main;
            }

            public void setMain(String main) {
                this.main = main;
            }

            public String getQlty() {
                return qlty;
            }

            public void setQlty(String qlty) {
                this.qlty = qlty;
            }

            public String getPm10() {
                return pm10;
            }

            public void setPm10(String pm10) {
                this.pm10 = pm10;
            }

            public String getPm25() {
                return pm25;
            }

            public void setPm25(String pm25) {
                this.pm25 = pm25;
            }

            public String getNo2() {
                return no2;
            }

            public void setNo2(String no2) {
                this.no2 = no2;
            }

            public String getSo2() {
                return so2;
            }

            public void setSo2(String so2) {
                this.so2 = so2;
            }

            public String getCo() {
                return co;
            }

            public void setCo(String co) {
                this.co = co;
            }

            public String getO3() {
                return o3;
            }

            public void setO3(String o3) {
                this.o3 = o3;
            }

            public JSONObject getJsonObject() {
                JSONObject object = new JSONObject();
                try {
                    object.put("pub_time", pub_time);
                    object.put("air_sta", air_sta);
                    object.put("asid", asid);
                    object.put("lat", lat);
                    object.put("lon", lon);
                    object.put("aqi", aqi);
                    object.put("main", main);
                    object.put("qlty", qlty);
                    object.put("pm10", pm10);
                    object.put("pm25", pm25);
                    object.put("no2", no2);
                    object.put("so2", so2);
                    object.put("co", co);
                    object.put("o3", o3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return object;
            }
            @Override
            public String toString() {
                return "AirNowStation{" +
                        "pub_time='" + pub_time + '\'' +
                        ", air_sta='" + air_sta + '\'' +
                        ", asid='" + asid + '\'' +
                        ", lat='" + lat + '\'' +
                        ", lon='" + lon + '\'' +
                        ", aqi='" + aqi + '\'' +
                        ", main='" + main + '\'' +
                        ", qlty='" + qlty + '\'' +
                        ", pm10='" + pm10 + '\'' +
                        ", pm25='" + pm25 + '\'' +
                        ", no2='" + no2 + '\'' +
                        ", so2='" + so2 + '\'' +
                        ", co='" + co + '\'' +
                        ", o3='" + o3 + '\'' +
                        '}';
            }
        }
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

}
