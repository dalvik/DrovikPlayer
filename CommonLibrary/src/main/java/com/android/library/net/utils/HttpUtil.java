package com.android.library.net.utils;

import android.os.Build;
import android.text.TextUtils;

import com.android.library.net.http.IInputStreamParser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Http 工具类
 *
 * @author Xielei
 */
public final class HttpUtil {

    private static final String GZIP_ENCODING = "gzip";
    private static final int TIME_OUT = 10 * 1000;

    private static String TAG = "HttpUtil";

    /**
     * HTTP GET 请求
     * 下载网页 图片 等
     *
     * @param url
     * @param parser
     */
    public static <T> T loadGetRequest(String url, IInputStreamParser<T> parser) {
        return loadData(false, url, null, null, parser);
    }

    /**
     * HTTP Post 请求
     *
     * @param url
     * @param parser
     */
    public static <T> T loadPostRequest(String url, byte[] params, IInputStreamParser<T> parser) {
        return loadData(true, url, null, params, parser);
    }

    public static <T> T loadPostRequest(String url, HashMap<String, String> headers, byte[] params, IInputStreamParser<T> parser) {
        return loadData(true, url, headers, params, parser);
    }

    /**
     * HTTP 请求
     *
     * @param isPost
     * @param url
     * @param parser
     */
    public static <T> T loadData(boolean isPost, String url, HashMap<String, String> headers, byte[] params, IInputStreamParser<T> parser) {
        HttpURLConnection conn = null;
        OutputStream out = null;
        T t = null;
        String reqUrl = url;
        if(!isPost){
            if(params != null){
                reqUrl = url + "?" + new String(params);
            } else {
                reqUrl = url;
            }
        }
        LogUtil.i(TAG, "==> loadData " + reqUrl);
        try {
            URL httpurl = new URL(reqUrl);
            conn = (HttpURLConnection) httpurl.openConnection();
            conn.setUseCaches(false);
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(TIME_OUT);
            conn.addRequestProperty("Accept-Encoding", "gzip");
            conn.addRequestProperty("Accept-Encoding", "compress");
            conn.addRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Connection", "Keep-Alive");
            if (Build.VERSION.SDK_INT > 13) {
                conn.setRequestProperty("Connection", "close");
            }

            //conn.setChunkedStreamingMode(0);

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    if (!TextUtils.isEmpty(entry.getValue())) {
                        conn.addRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
            }
            if (isPost) {
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                if (params != null) {
                    out = conn.getOutputStream();
                    out.write(params);
                    out.flush();
                }
            } else {
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
            }

            if (conn.getResponseCode() == 200) {
                if (!TextUtils.isEmpty(conn.getContentEncoding()) && conn.getContentEncoding().contains(GZIP_ENCODING)) {
                    t = parser.parser(new GZIPInputStream(conn.getInputStream()));
                } else {
                    t = parser.parser(conn.getInputStream());
                }
            }else{
                LogUtil.w(TAG, "Response Result : "+conn.getResponseCode()+":"+conn.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return t;
    }

    /**
     * 上传文件
     *
     * @param url
     * @param data 图片字节流
     * @return
     */
    public static String upload(String url, byte[] data) {
        DataInputStream dis = null;
        OutputStream os = null;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setReadTimeout(20 * 1000);
            con.setConnectTimeout(20 * 1000);
            /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(true);

			/* 设置传送的method=POST */
            con.setRequestMethod("POST");
            /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=******");

			/* 设置OutputStream */
            os = con.getOutputStream();

            // 文件内容
            os.write(data);

            os.flush();
            // 请求成功
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 获取服务器返回的数据
                dis = new DataInputStream(con.getInputStream());
            }

            if (dis != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int len = -1;
                while ((len = dis.read(b)) != -1) {
                    bos.write(b, 0, len);
                }
                String jsonStr = new String(bos.toByteArray());
                return jsonStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null)
                    dis.close();
            } catch (IOException e) {
            } finally {
                try {
                    if (os != null)
                        os.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /**
     * 发送XML数据
     *
     * @param url
     * @param xml
     * @return
     */
    public static String loadDataByXML(String url, String xml) {
        DataOutputStream out = null;
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL postUrl = new URL(url);
            // 打开连接
            connection = (HttpURLConnection) postUrl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            out = new DataOutputStream(connection.getOutputStream());
            out.write(xml.getBytes("utf-8"));
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (connection != null)
                    connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
