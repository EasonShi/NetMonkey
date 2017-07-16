package com.stone.netmonkey;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by eason on 17-7-14.
 */
public class ProjectUtil {

    public static String urlToDomain(String url){
        try {
            return new URL(url).getHost();
        } catch (MalformedURLException e) {
            return null;
        }
    }

}
