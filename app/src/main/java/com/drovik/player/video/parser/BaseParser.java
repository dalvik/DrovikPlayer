package com.drovik.player.video.parser;

import com.crixmod.sailorcast.model.SCAlbums;
import com.crixmod.sailorcast.model.SCVideos;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class BaseParser {

    public final String userAgent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";

    public final String src = "76f90cbd92f94a2e925d83e8ccd22cb7";

    public final String key = "d5fb4bd9d50c4be6948c97edd7254b0e";

    public final int TIMEOUT = 10000;

    public abstract String loadHtml(String url);

    public abstract SCVideos parseVideos(String content);

    public abstract SCAlbums parseAlbums(String content);

    public abstract String parseVideoSource(String url);

    public String parseVideoSource(String vid, String tvid) {
        return "";
    }

    public String httpGet(String url,String cookie) throws IOException {
        //获取请求连接
        Connection con = Jsoup.connect(url);
        //请求头设置，特别是cookie设置
        con.header("Accept", "text/html, application/xhtml+xml, */*");
        con.header("Content-Type", "application/x-www-form-urlencoded");
        con.header("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0))");
        con.header("Cookie", cookie);
        //解析请求结果
        Document doc=con.get();
        //获取标题
        //System.out.println(doc.title());
        //返回内容
        return doc.toString();
    }

    public byte[] readInputStream(InputStream inStream) throws Exception {
        //此类实现了一个输出流，其中的数据被写入一个 byte 数组
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // 字节数组
        byte[] buffer = new byte[1024];
        int len = 0;
        //从输入流中读取一定数量的字节，并将其存储在缓冲区数组buffer 中
        while ((len = inStream.read(buffer)) != -1) {
            // 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此输出流
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        //toByteArray()创建一个新分配的 byte 数组。
        return outStream.toByteArray();
    }

    public static String httpGetHeader(String url,String cook,String header) throws IOException{
        //获取请求连接
        Connection con = Jsoup.connect(url);
        //请求头设置，特别是cookie设置
        con.header("Accept", "text/html, application/xhtml+xml, */*");
        con.header("Content-Type", "application/x-www-form-urlencoded");
        con.header("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0))");
        con.header("Cookie", cook);
        //发送请求
        Connection.Response resp=con.method(Connection.Method.GET).execute();
        //获取cookie名称为__bsi的值
        String cookieValue = resp.cookie("__bsi");
        //System.out.println("cookie  __bsi值：  "+cookieValue);
        //获取返回cookie所值
        Map<String,String> cookies = resp.cookies();
        //System.out.println("所有cookie值：  "+cookies);
        //获取返回头文件值
        String headerValue = resp.header(header);
        //System.out.println("头文件"+header+"的值："+headerValue);
        //获取所有头文件值
        Map<String,String> headersOne =resp.headers();
        //System.out.println("所有头文件值："+headersOne);
        return headerValue;
    }

    public static String httpPost(String url,Map<String,String> map,String cookie) throws IOException{
        //获取请求连接
        Connection con = Jsoup.connect(url);
        //遍历生成参数
        if(map!=null){
            for (Map.Entry<String, String> entry : map.entrySet()) {
                //添加参数
                con.data(entry.getKey(), entry.getValue());
            }
        }
        //插入cookie（头文件形式）
        con.header("Cookie", cookie);
        Document doc = con.post();
        return doc.toString();
    }
}
