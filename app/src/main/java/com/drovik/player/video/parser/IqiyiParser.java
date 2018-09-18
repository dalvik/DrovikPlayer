package com.drovik.player.video.parser;

import android.text.TextUtils;
import android.util.Log;

import com.android.audiorecorder.DebugConfig;
import com.crixmod.sailorcast.model.SCAlbum;
import com.crixmod.sailorcast.model.SCAlbums;
import com.crixmod.sailorcast.model.SCSite;
import com.crixmod.sailorcast.model.SCVideo;
import com.crixmod.sailorcast.model.SCVideos;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class IqiyiParser extends BaseParser {

    private String TAG = "IqiyiParser";

    @Override
    public String loadHtml(String url) {
        String content = "";
        Document document = null;
        try {
            document = Jsoup.connect(url)
                    .userAgent(userAgent)
                    .timeout(TIMEOUT).get();
            Element element = document.body();
            Elements div = element.select("div.wrapper-piclist");
            if(div != null) {
                content = div.toString();
            }
        } catch (IOException e) {
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
            Log.d(TAG, "==> imgSrc : " + imgSrc);// +  " durationElements" + durationDivElements
            Log.d(TAG, "==> title : " + title + ", link: " + link + " duration: " + duration.text().trim());
            Elements picListInfoDiv = ele.select("div.site-piclist_info");
            Document picListInfoDocument = Jsoup.parse(picListInfoDiv.toString());
            Elements scoreElement = picListInfoDocument.select("div.mod-listTitle_left");
            Document scoreSpanDocument = Jsoup.parse(picListInfoDiv.toString());
            Elements scor = scoreSpanDocument.getElementsByTag("span");
            Log.d(TAG, "==> scor : " + scor.text());

            Elements roleInfoElement = picListInfoDocument.select("div.role_info");
            Document roleDocument = Jsoup.parse(roleInfoElement.toString());
            Elements roleEm = roleDocument.getElementsByTag("em");
            int index = 0;
            SCVideo video = new SCVideo();
            for(Element role:roleEm) {
                Log.d(TAG, "==> roleEm : " + role.text());
                if(index == 0){

                }
                index++;
            }
            video.setVideoTitle(title);//video name
            video.setHorPic(imgSrc);
            video.setIqiyiVideoURL(link);
            videos.add(video);
        }
        return videos;
    }

    @Override
    public SCAlbums parseAlbums(String content) {
        SCAlbums albums = new SCAlbums();
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
            Log.d(TAG, "==> imgSrc : " + imgSrc);// +  " durationElements" + durationDivElements
            Log.d(TAG, "==> title : " + title + ", link: " + link + " duration: " + duration.text().trim());
            Elements picListInfoDiv = ele.select("div.site-piclist_info");
            Document picListInfoDocument = Jsoup.parse(picListInfoDiv.toString());
            Elements scoreElement = picListInfoDocument.select("div.mod-listTitle_left");
            Document scoreSpanDocument = Jsoup.parse(picListInfoDiv.toString());
            Elements scor = scoreSpanDocument.getElementsByTag("span");
            Log.d(TAG, "==> scor : " + scor.text());

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
                Log.d(TAG, "==> roleEm : " + name);
            }
            album.setTitle(title);//video name
            album.setMainActor(sb.toString());
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
            album.setAlbumId(link);
            albums.add(album);
        }
        return albums;
    }

    @Override
    public String parseVideoSource(String url) {
        String videoPlaySource = null;
        if(DebugConfig.DEBUG){
            Log.d(TAG, "==>parseVideoSource url: " + url);
        }
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
        }
        return videoPlaySource;
    }
}
