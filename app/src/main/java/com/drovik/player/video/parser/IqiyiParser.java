package com.drovik.player.video.parser;

import android.text.TextUtils;
import android.util.Log;

import com.android.audiorecorder.DebugConfig;
import com.crixmod.sailorcast.model.SCAlbum;
import com.crixmod.sailorcast.model.SCAlbums;
import com.crixmod.sailorcast.model.SCSite;
import com.crixmod.sailorcast.model.SCVideo;
import com.crixmod.sailorcast.model.SCVideos;
import com.drovik.player.video.Const;
import com.drovik.player.video.ui.SaveFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.security.MessageDigest;

public class IqiyiParser extends BaseParser {

    private String TAG = "IqiyiParser";

    private static String mUserAgent;

    public IqiyiParser() {
        int uaIndex = Integer.parseInt(Math.round(Math.random() * (UserAgent.length - 1))+"");
        if(mUserAgent == null) {
            mUserAgent = UserAgent[uaIndex];
        }
        Log.d(TAG, "==> rdua: " + uaIndex + " " + mUserAgent);
    }

    @Override
    public String getUserAgent() {
        //mUserAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729; InfoPath.3; rv:11.0) like Gecko";
        mUserAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729; InfoPath.3; rv:11.0) like Gecko";
        mUserAgent = UserAgent[0];
        return mUserAgent;
    }

    @Override
    public String loadHtml(String urlString) {
        String content = getHtmlContent(urlString);
        return content;
    }

    @Override
    public SCVideos parseVideos(String content) {
        return null;
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
        SaveFile.getInstance().writeString(element.toString());
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
                    if(dataOriginal != null && dataOriginal.length()>0) {
                        try {
                            JSONObject dataOriginalJson = new JSONObject(dataOriginal);
                            album.setTitle(dataOriginalJson.optString("name"));
                            album.setHorImageUrl(dataOriginalJson.optString("imageUrl"));
                            album.setVideosTotal(dataOriginalJson.optInt("videoCount"));
                            album.setMainActor(dataOriginalJson.optString("secondInfo"));//主演
                            album.setDirector(dataOriginalJson.optString("main_charactor"));//演员表
                            album.setAlbumId(dataOriginalJson.optString("docId"));
                            album.setTVid(dataOriginalJson.optString("tvId"));
                            album.setScore(dataOriginalJson.optString("score"));
                            album.setDesc(dataOriginalJson.optString("description"));
                            album.setPlayUrl(dataOriginalJson.optString("playUrl"));
                            albums.add(album);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return albums;
    }


    /**
     <li>
        <div> <a href="">第一集</a></div>
        <p></p>
     </li>

     */
    public EpisodeList parseEpisodeList(String content, int channelID){
        EpisodeList episodeList = new EpisodeList();
        Document htmlContent = Jsoup.parse(content);
        Element element = htmlContent.body();
        if(channelID == Const.channel_iqiyi_comic){
            Elements alumList  = element.select("ul[data-albumlist-play]");
            if(alumList != null) {
                Elements elemList = alumList.select("li[data-albumlist-elem]");
                for(Element ulElement:elemList) {
                    Episode node = new Episode();
                    String dataOrder = ulElement.attr("data-order");
                    Elements picList = ulElement.select("div.site-piclist_pic");
                    String playUrl = picList.select("a").attr("href");
                    String name = picList.select("a").attr("title");
                    Elements modListTitle = ulElement.select("div.mod-listTitle");
                    String duration = modListTitle.select("span.mod-listTitle_right").text();
                    String imageUrl = ulElement.select("img").attr("src");
                    node.setSubTitle(name);
                    node.setOrder(dataOrder);
                    node.setDuration(duration);
                    if(!TextUtils.isEmpty(playUrl)){
                        if(playUrl.startsWith("//")){
                            node.setPlayUrl("https:" + playUrl);
                        } else {
                            node.setPlayUrl(playUrl);
                        }
                    }
                    if (!TextUtils.isEmpty(imageUrl)) {
                        if (imageUrl.startsWith("//")) {
                            node.setImageUrl("http:" + imageUrl);
                        } else {
                            node.setImageUrl(imageUrl);
                        }
                    }
                    episodeList.add(node);
                }
            }
        } else {
            Elements listContent = element.select("#album-avlist-data");
            SaveFile.getInstance().writeString(content);
            //Log.d(TAG, "==> " + content);
            if(listContent != null) {
                String value = listContent.attr("value");
                if(value != null && value.length()>0) {
                    try {
                        JSONObject episodeJson = new JSONObject(value);
                        JSONArray epsodeListArray = episodeJson.getJSONArray("epsodelist");
                        if(epsodeListArray != null) {
                            int length = epsodeListArray.length();
                            for(int i=0; i<length; i++) {
                                Episode episode = new Episode();
                                JSONObject episodeJsonItem = epsodeListArray.getJSONObject(i);
                                episode.setDescription(episodeJsonItem.optString("description"));
                                episode.setTvId(episodeJsonItem.optString("tvId"));
                                episode.setShortTitle(episodeJsonItem.optString("shortTitle"));
                                episode.setSubTitle(episodeJsonItem.optString("subtitle"));
                                episode.setDuration(episodeJsonItem.optString("duration"));
                                episode.setVid(episodeJsonItem.optString("vid"));
                                episode.setName(episodeJsonItem.optString("name"));
                                episode.setOrder(episodeJsonItem.optString("order"));
                                episode.setImageUrl(episodeJsonItem.optString("imageUrl"));
                                episodeList.add(episode);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return episodeList;
    }

    /**
     * 获取tvid 和vid
     * @param htmlContent
     * @return
     */
    @Override
    public SCAlbums getSCAlbum(String htmlContent) {
        SCAlbums scVideos = new SCAlbums();
        if(!TextUtils.isEmpty(htmlContent)) {
            Document doc = Jsoup.parse(htmlContent);
            Element element = doc.body();
            Elements alumList  = element.select("script");
            if(alumList != null) {
                for (Element e : alumList) {
                    String item = e.toString();
                    String playPageData = "playPageData";
                    if (item.contains(playPageData)) {
                        int index = item.indexOf(playPageData);
                        String body = item.substring(index);
                        int startIndex = body.indexOf("{");
                        int endIndex = body.lastIndexOf("}");
                        if (startIndex >= 0 && endIndex >= 0) {
                            String content = body.substring(startIndex, endIndex + 1);
                            try {
                                JSONObject jsonObject = new JSONObject(content);
                                JSONObject rootObject = jsonObject.getJSONObject("albumData");
                                JSONArray array = rootObject.getJSONArray("list");
                                int length = array.length();
                                for (int i = 0; i < length; i++) {
                                    SCAlbum scVideo = new SCAlbum(SCSite.IQIYI);
                                    JSONObject itemObject = array.getJSONObject(i);
                                    String url = itemObject.getString("url");
                                    String subtitle = itemObject.getString("subtitle");
                                    String tvid = itemObject.getString("tvId");
                                    String vid = itemObject.getString("vid");
                                    String description = itemObject.getString("description");
                                    String albumId = itemObject.getString("albumId");
                                    scVideo.setTVid(tvid);
                                    scVideo.setAlbumId(vid);
                                    scVideo.setDesc(description);
                                    scVideo.setPlayUrl(url);
                                    scVideos.add(scVideo);
                                    System.out.println(subtitle + "=" + tvid + "=" + vid + "=" + url);
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return scVideos;
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
