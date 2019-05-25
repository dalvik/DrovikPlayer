package com.drovik.player.video.parser;

import android.text.TextUtils;
import android.util.Log;

import com.android.audiorecorder.DebugConfig;
import com.crixmod.sailorcast.model.SCAlbum;
import com.crixmod.sailorcast.model.SCAlbums;
import com.crixmod.sailorcast.model.SCSite;
import com.crixmod.sailorcast.model.SCVideo;
import com.crixmod.sailorcast.model.SCVideos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class IqiyiParser extends BaseParser {

    private String TAG = "IqiyiParser";

    @Override
    public String loadHtml(String urlString) {
        String content = "";
        //Document document = null;
        try {
            /*document = Jsoup.connect(urlString)
                    .userAgent(userAgent)
                    .timeout(TIMEOUT).get();
            Element element = document.body();
            Elements div = element.select("div.wrapper-piclist");
            if(div != null) {
                content = div.toString();
            }*/
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream inStream = conn.getInputStream();// 通过输入流获取html数据
            byte[] data = readInputStream(inStream);//
            content = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    @Override
    public SCVideos parseVideos(String content) {
        SCVideos videos = new SCVideos();
        Document divdivs = Jsoup.parse(content);
        Elements itemLi = divdivs.getElementsByTag("li");
        for(Element ele:itemLi) {
            Elements picListPicDiv = ele.select("div.site-piclist_pic");
            Document picListPicDivDecument = Jsoup.parse(picListPicDiv.toString());
            Elements altaElements = picListPicDivDecument.getElementsByTag("a");
            Document altaDocument = Jsoup.parse(altaElements.toString());
            Elements imgElements = altaDocument.getElementsByTag("img");
            Elements durationDivElements = altaDocument.select("div.mod-listTitle");
            Elements duration = durationDivElements.select("span.icon-vInfo");
            String imgSrc   = imgElements.attr("src").replace("\\", "").trim();
            String link   = picListPicDivDecument.select("a").attr("href").replace("\\", "").trim();
            String title   = picListPicDivDecument.select("a").attr("title").replace("\\", "").trim();
            String img   = picListPicDivDecument.select("a").attr("title").replace("\\", "").trim();
            String src = picListPicDivDecument.getElementsByTag("img").text();
            //Log.d(TAG, "==> imgSrc : " + imgSrc);// +  " durationElements" + durationDivElements
            //Log.d(TAG, "==> title : " + title + ", link: " + link + " duration: " + duration.text().trim());
            Elements picListInfoDiv = ele.select("div.site-piclist_info");
            Document picListInfoDocument = Jsoup.parse(picListInfoDiv.toString());
            Elements scoreElement = picListInfoDocument.select("div.mod-listTitle_left");
            Document scoreSpanDocument = Jsoup.parse(picListInfoDiv.toString());
            Elements scor = scoreSpanDocument.getElementsByTag("span");
            //Log.d(TAG, "==> scor : " + scor.text());

            Elements roleInfoElement = picListInfoDocument.select("div.role_info");
            Document roleDocument = Jsoup.parse(roleInfoElement.toString());
            Elements roleEm = roleDocument.getElementsByTag("em");
            int index = 0;
            SCVideo video = new SCVideo();
            video.setVideoTitle(title);//video name
            video.setHorPic(imgSrc);
            video.setIqiyiVideoURL(link);
            videos.add(video);
        }
        return videos;
    }

    /**
     html struct
     <body>
        <<div class="qy-list-wrap">
             <ul class="qy-mod-ul">
                <li class="qy-mod-li  j-video-popup " .. > //data-original:{...}  data-module:""
                    <div class="qy-list-img  vertical ">
                        <div class="qy-mod-link-wrap">
                            <a title="荒野女人" href="playurl"/> <img src:"//"/>
                        </div>
                        <div class="title-wrap ">
                        </div>
                    </div>
                </li>
             </ul>
        </div>
     </body>

     {"issueTime":1453966210000,"docId":"2ad32de9f3fd92992b99697c2645af55",
     "description":"沈默以舞台作为自己的毕生梦想，他深入诈骗团伙逢场作戏只是为了找寻真相。",
     "qiyiProduced":false,"focus":"沈马组合爆笑黑色幽默","tvId":"444754700","formatPeriod":"2015-12-31","playUrl":"http://www.iqiyi.com/v_19rrlcgb4w.html","duration":"01:41:20","videoCount":1,
     "videoInfoType":"video",
     "cast":{"main_charactor":[{"image_url":"http://pic2.iqiyipic.com/image/20190312/2c/88/p_5037611_m_601_m6.jpg","name":"沈腾","id":213640105},{"image_url":"http://pic2.iqiyipic.com/image/20181228/f7/e5/p_2013841_m_601_m7.jpg","name":"马丽","id":208593805}]},
     "score":8.7,
     "latestOrder":1,"imageUrl":"http://pic5.iqiyipic.com/image/20180217/35/12/v_109991650_m_601_m7.jpg",
     "name":"一念天堂","exclusive":false,"siteId":"iqiyi","categories":[{"name":"喜剧"}],"is1080p":true,"secondInfo":"主演:沈腾 / 马丽","contentType":1,"channelId":1}
     */
    @Override
    public SCAlbums parseAlbums(String content) {
        SCAlbums albums = new SCAlbums();
        Document htmlContent = Jsoup.parse(content);
        Element element = htmlContent.body();
        Elements listContent = element.select("ul.qy-mod-ul");
        if(listContent != null) {
            Elements qyModeList = listContent.select("li.qy-mod-li");
            if(qyModeList != null) {
                for(Element liElement:qyModeList) {
                    SCAlbum album = new SCAlbum(SCSite.IQIYI);
                    Elements qyModeLink = liElement.select("div.qy-mod-link-wrap");
                    if(qyModeLink != null) {
                        Elements qyModeLinkA = qyModeLink.select("a");
                        if(qyModeLinkA != null) {
                            String imageUrl = qyModeLinkA.select("img").attr("src");
                            if(!TextUtils.isEmpty(imageUrl)){
                                if(imageUrl.startsWith("//")) {
                                    album.setVerImageUrl("http:" + imageUrl);
                                } else {
                                    album.setVerImageUrl(imageUrl);
                                }
                            }
                        }
                    }
                    String dataOriginal = liElement.attr("data-original");
                    if(dataOriginal != null) {
                        try {
                            JSONObject dataOriginalJson = new JSONObject(dataOriginal);
                            album.setTitle(dataOriginalJson.optString("name"));
                            album.setHorImageUrl(dataOriginalJson.optString("imageUrl"));
                            album.setVideosTotal(dataOriginalJson.optInt("videoCount"));
                            album.setMainActor(dataOriginalJson.optString("secondInfo"));
                            album.setAlbumId(dataOriginalJson.optString("docId"));
                            album.setSubTitle(dataOriginalJson.optString("tvId"));
                            album.setDesc(dataOriginalJson.optString("description"));
                            albums.add(album);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        for(Element ele:listContent) {
            Elements qyModeLi = ele.select("li.qy-mod-li  j-video-popup");
            Log.d(TAG, "==> qyModeLi : " + qyModeLi.toString());
            /*Elements picListPicDiv = ele.select("div.site-piclist_pic");
            Document picListPicDivDecument = Jsoup.parse(picListPicDiv.toString());
            Elements altaElements = picListPicDivDecument.getElementsByTag("a");
            Document altaDocument = Jsoup.parse(altaElements.toString());
            Elements imgElements = altaDocument.getElementsByTag("img");
            Elements durationDivElements = altaDocument.select("div.mod-listTitle");
            Elements duration = durationDivElements.select("span.icon-vInfo");
            String imgSrc   = imgElements.attr("src").replace("\\", "").trim();
            String link   = picListPicDivDecument.select("a").attr("href").replace("\\", "").trim();
            String title   = picListPicDivDecument.select("a").attr("title").replace("\\", "").trim();
            String img   = picListPicDivDecument.select("a").attr("title").replace("\\", "").trim();
            String src = picListPicDivDecument.getElementsByTag("img").text();
            String vid   = picListPicDivDecument.select("a").attr("data-qidanadd-albumid").trim();
            String tvid   = picListPicDivDecument.select("a").attr("data-qidanadd-tvid").trim();
            //Log.d(TAG, "==> imgSrc : " + imgSrc);// +  " durationElements" + durationDivElements
            //Log.d(TAG, "==> title : " + title + ", link: " + link + " duration: " + duration.text().trim());
           // Log.d(TAG, "==> vid : " + vid + ", tvid: " + tvid);
            Elements picListInfoDiv = ele.select("div.site-piclist_info");
            Document picListInfoDocument = Jsoup.parse(picListInfoDiv.toString());
            Elements scoreElement = picListInfoDocument.select("div.mod-listTitle_left");
            Document scoreSpanDocument = Jsoup.parse(picListInfoDiv.toString());
            Elements scor = scoreSpanDocument.getElementsByTag("span");
            //Log.d(TAG, "==> scor : " + scor.text());

            Elements roleInfoElement = picListInfoDocument.select("div.role_info");
            Document roleDocument = Jsoup.parse(roleInfoElement.toString());
            Elements roleEm = roleDocument.getElementsByTag("em");
            StringBuilder sb = new StringBuilder();
            int index = 0;
            SCAlbum album = new SCAlbum(SCSite.IQIYI);
            for(Element role:roleEm) {
                String name = role.text();
                sb.append(name +"\n");
                if(index == 0){
                    album.setDirector(name);
                } else {
                    sb.append(name + " ");
                }
                index++;
            }
            //Log.d(TAG, "==> roleEm : " + sb.toString());
            album.setTitle(title);//video name
            album.setMainActor(sb.toString());
            album.setAlbumId(vid);
            album.setSubTitle(tvid);
            if(imgSrc.startsWith("//")) {
                album.setHorImageUrl("http:" + imgSrc);
            } else {
                album.setHorImageUrl(imgSrc);
            }
            if(imgSrc.startsWith("//")) {
                album.setVerImageUrl("http:" + imgSrc);
            } else {
                album.setVerImageUrl(imgSrc);
            }
            //album.setAlbumId(link);
            albums.add(album);*/
        }
        return albums;
    }

    @Override
    public String parseVideoSource(String vid) {
        String videoPlaySource = null;
        String time = System.currentTimeMillis()+"";
        String tvid = vid;
        //String vid = "913283400";
        String src = "76f90cbd92f94a2e925d83e8ccd22cb7";
        String key = "d5fb4bd9d50c4be6948c97edd7254b0e";
        String sc = sc(time, vid, key);
        videoPlaySource = "http://cache.m.iqiyi.com/tmts/"+ tvid +"/"+ vid +"/?t="+ time +"&sc=" + sc + "&src=" + src;//.format(tvid, vid, t, sc, src)
        //75f4961d76dbea693270e632bfb7c88f
        //75f4961d76dbea693270e632bfb7c88f
        if(DebugConfig.DEBUG){
            Log.d(TAG, "==>parseVideoSource url: " + videoPlaySource);
        }
        /*
        try {
            URL videoUrl = new URL("http://www.wq114.org/tong.php?url=" + url);
            String content = httpGet(videoUrl.toString(), "");
            Document contentDocument = Jsoup.parse(content);
            Element body = contentDocument.body();
            if(body != null) {
                if(DebugConfig.DEBUG){
                    Log.d(TAG, "==>parseVideoSource body: " + body);
                }
                Elements altaElements = body.getElementsByTag("iframe");
                if(altaElements != null) {
                    if(DebugConfig.DEBUG){
                        Log.d(TAG, "==>parseVideoSource altaElements: " + altaElements);
                    }
                    String srcUrl = altaElements.attr("src");
                    if(!TextUtils.isEmpty(srcUrl)) {
                        int index = srcUrl.indexOf("http");
                        if(index>=0) {
                            if(DebugConfig.DEBUG){
                                Log.d(TAG, "==>parseVideoSource url: " + url);
                            }
                            videoPlaySource = srcUrl.substring(index);
                        } else {
                            Log.w(TAG, "==>parseVideoSource index: " + index);
                        }
                    } else {
                        Log.w(TAG, "==>parseVideoSource srcUrl: " + srcUrl);
                    }
                } else {
                    Log.w(TAG, "==>parseVideoSource altaElements: " + altaElements);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return videoPlaySource;
    }

    @Override
    public String parseVideoSource(String vid, String tvid) {
        String videoPlaySource = null;
        String time = System.currentTimeMillis()+"";
        //String vid = "913283400";
        //tvid = "764778";
        //vid = "624246";
        String sc = sc(time, vid, key);
        String targetTempVideoSource = "http://cache.m.iqiyi.com/tmts/"+ tvid +"/"+ vid +"/?t="+ time +"&sc=" + sc + "&src=" + src;//.format(tvid, vid, t, sc, src)
        if(DebugConfig.DEBUG){
            Log.d(TAG, "==> parseVideoS  ource url: " + targetTempVideoSource);
        }
        try {
            String  videoLink = httpGet(targetTempVideoSource, "");
            Document bodyContent = Jsoup.parse(videoLink);
            Element elementBody = bodyContent.body();
            JSONObject videoLinkObject = new JSONObject(elementBody.text());
            JSONObject data = videoLinkObject.optJSONObject("data");
            if(data != null){
                JSONArray vidlArray = data.optJSONArray("vidl");
                int videoWidth = 1280;
                int videoHeight = 0;
                String fileFormat = null;
                if(vidlArray != null){
                    int length = vidlArray.length();
                    for(int i=0; i<length; i++) {
                        JSONObject videoItem = vidlArray.getJSONObject(i);
                        fileFormat = videoItem.optString("fileFormat");//H265
                        String screenSize = videoItem.optString("screenSize");//1280x544
                        if(!TextUtils.isEmpty(screenSize)) {
                            String[] screenArray = screenSize.split("x");
                            if(screenArray != null && screenArray.length>=2) {
                                int width = Integer.parseInt(screenArray[0]);
                                int height = Integer.parseInt(screenArray[1]);
                                Log.d(TAG, "==> videoWidth: " + width + " height: " + height);
                                if(videoWidth==width) {
                                    videoWidth = width;
                                    videoHeight = height;
                                    videoPlaySource = videoItem.optString("m3utx");
                                }
                                if("H265".equalsIgnoreCase(fileFormat)) {
                                    videoPlaySource = videoItem.optString("m3utx");
                                    if(DebugConfig.DEBUG){
                                        Log.d(TAG, "==> fileFormat: " + fileFormat + " videoPlaySource: " + videoPlaySource);
                                    }
                                }
                            }
                        }
                    }
                }
                if(DebugConfig.DEBUG){
                    Log.d(TAG, "==> videoWidth: " + videoWidth + " videoHeight: " + videoHeight + " fileFromat: " + fileFormat + " videoPlaySource: " + videoPlaySource);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoPlaySource;
    }


    private static String sc(String time, String vid, String key) {
        return MD5Encrypt(time + key + vid);
    }

    private static String MD5Encrypt(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
