package com.stone.netmonkey.service;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.primitives.Bytes;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by eason on 17-7-13.
 */
public class NetService {
    static String userDir = "";

    public static void start(String url,int tier,String cookie,String projectName) {
        userDir=System.getProperty("user.dir")+"/app-content/"+projectName;
        Connection connect = Jsoup.connect(url);
        connect.header("Referer",url);
        connect.header("Origin",url);
        if(cookie!=null&&!cookie.isEmpty()) connect.header("Cookie",cookie);
        //获取index html
        Document document = null;
        try {
            document = connect.get();
            Element body = document.body();
            //解析资源

            Elements links = document.select("link");
            for (Element link : links) {
                String href = link.attr("href");
                if(href!=null&&!href.isEmpty())saveSource(href,url);
            }
            Elements imgs = document.select("img");
            for (Element img : imgs) {
                String src = img.attr("src");
                if(src!=null&&!src.isEmpty())saveSource(src,url);
            }
            Elements scripts = document.select("script");
            for (Element script : scripts) {
                String src = script.attr("src");
                if(src!=null&&!src.isEmpty())saveSource(src,url);
            }
            Files.write(document.html().getBytes(),new File(userDir+"/index.html"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 保持页面资源
     * @param url
     * @param pageUrl
     * @return
     */
    public static String saveSource(String url,String pageUrl){
        String pageDomain = pageUrl.substring(pageUrl.indexOf("://") + 3, pageUrl.indexOf("/",7));
        boolean hasHttp = url.indexOf("http") == 0;
        String filePath;
        String fileName;
        if(hasHttp){
            filePath = url.substring(url.indexOf("://")+2,url.lastIndexOf("/")+1);
            filePath = filePath.replaceAll("//","/");
            fileName = url.substring(url.lastIndexOf("/")+1);

            File fileDir = new File(userDir+filePath);
            if(!fileDir.exists()) fileDir.mkdirs();

            try {
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet httpget = new HttpGet(url);
                CloseableHttpResponse response = httpclient.execute(httpget);
                int statusCode = response.getStatusLine().getStatusCode();

                InputStream content = response.getEntity().getContent();
                byte[] byteArray = ByteStreams.toByteArray(content);
                Files.write(byteArray,new File(fileDir.getPath()+File.separator+fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{

        }

        return null;
    }
}
