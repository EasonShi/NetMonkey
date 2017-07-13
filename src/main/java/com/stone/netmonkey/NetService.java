package com.stone.netmonkey;

import com.google.common.io.Files;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by eason on 17-7-13.
 */
public class NetService {

    public static void start(String url,int tier,String cookie) {
        String userDir = System.getProperty("user.dir");
        Connection connect = Jsoup.connect(url);
        connect.header("Referer",url);
        connect.header("Origin",url);
        if(cookie!=null&&!cookie.isEmpty()) connect.header("Cookie",cookie);
        //获取index html
        Document document = null;
        try {
            document = connect.get();
            Files.write(document.html().getBytes(),new File(userDir+"/index.html"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
