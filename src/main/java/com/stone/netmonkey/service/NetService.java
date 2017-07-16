package com.stone.netmonkey.service;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.primitives.Bytes;
import com.google.common.util.concurrent.Service;
import com.stone.netmonkey.Main;
import com.stone.netmonkey.controller.MainController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
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
import java.net.URL;
import java.util.Properties;

/**
 * Created by eason on 17-7-13.
 */
public class NetService {
    String userDir = "";
    private MainController mainController;

    public NetService(MainController mainController){
        this.mainController = mainController;
    }

    public void start(String url, int tier, String cookie, String projectName) {
        mainController.infoTableView.getItems().clear();
        userDir=System.getProperty("user.dir")+"/app-content/"+projectName;
        downPage(url,cookie,tier);
        System.out.println("执行成功");
    }
    int nowTier = 1;
    private void downPage(String url, String cookie,int tier) {
        System.out.println("page "+url);
        if(url.contains("/page/2")){
            System.out.println(1);
        }
        try {
            Connection connect = Jsoup.connect(url);
            connect.header("Referer",url);
            connect.header("Origin",url);
            connect.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
            if(cookie!=null&&!cookie.isEmpty()) connect.header("Cookie",cookie);
            //获取index html
            Document document = null;
//            connect.maxBodySize(10485760);

            document = connect.get();

            //解析资源
            Elements links = document.select("link");
            Elements imgs = document.select("img");
            Elements scripts = document.select("script");
            links.addAll(imgs);links.addAll(scripts);
            for (Element link : links) {
                String tagName = link.tag().getName();
                String hrefsrc = "";
                if("link".equals(tagName))
                    hrefsrc = link.attr("href");
                else if("img".equals(tagName)||"script".equals(tagName))
                    hrefsrc = link.attr("src");
                //下载资源
                String correctSourceUrl = saveSource(hrefsrc, url);
                correctSourceUrl = "."+correctSourceUrl;

                if("link".equals(tagName))
                    link.attr("href",correctSourceUrl);
                else if("img".equals(tagName)||"script".equals(tagName))
                    link.attr("src",correctSourceUrl);
            }

            Elements as = document.select("a");
            boolean isCalculate=false;
            for (Element a : as) {
                String href = a.attr("href");
                if(href!=null&&!href.isEmpty()){
                    System.out.println("url  "+url);
                    System.out.println("href  "+href);
                    String correctLinkUrl = href.substring(href.indexOf("://")+2,href.lastIndexOf("/"));
                    correctLinkUrl = "."+correctLinkUrl;
                    if(nowTier<tier||isCalculate){
                        if (!isCalculate){
                            isCalculate=true;
                            nowTier++;
                        }
                        downPage(href,cookie,tier);
                        a.attr("href",correctLinkUrl);  //修改为下载后的链接
                    }

                }
            }
            URL nowUrl = new URL(url);
            String filePath = nowUrl.getPath().isEmpty()?"/":url.substring(url.indexOf("://")+2,url.lastIndexOf("/")+1);
            String fileName = nowUrl.getPath().isEmpty()?"index.html":new URL(url).getFile();
            File path = new File(userDir + filePath);
            if(!path.exists()) path.mkdirs();
            File file = new File(userDir + filePath + fileName);
            if(!file.isDirectory()){
                Files.write(document.html().getBytes(),file);
            }

            //TODO 解析url 重构， 下载页面位置修改

        } catch (IOException e) {
            Platform.runLater(()->{
                ObservableList items = mainController.infoTableView.getItems();
                items.add(new NetListColumn("error",url,"NO"));
            });
            e.printStackTrace();
        }catch (Exception e){
            Platform.runLater(()->{
                ObservableList items = mainController.infoTableView.getItems();
                items.add(new NetListColumn("error",url,"NO"));
            });
            e.printStackTrace();
        }
    }

    /**
     * 保持页面资源
     * @param url
     * @param pageUrl
     * @return 修正后的页面路径
     */
    public String saveSource(String url,String pageUrl){
        System.out.println("source "+url);
        String correctPageUrl = null;
        boolean hasHttp = url.indexOf("http") == 0;
        String filePath;
        String fileName;
        if(hasHttp){
            filePath = url.substring(url.indexOf("://")+2,url.lastIndexOf("/")+1);
            filePath = filePath.replaceAll("//","/").replaceAll("[?]","");
            fileName = url.substring(url.lastIndexOf("/")+1);
            correctPageUrl = filePath+fileName;
            File fileDir = new File(userDir+filePath);
            if(!fileDir.exists()) fileDir.mkdirs();

            try {
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet httpget = new HttpGet(url);
                CloseableHttpResponse response = httpclient.execute(httpget);
                int statusCode = response.getStatusLine().getStatusCode();
                long contentLength = response.getEntity().getContentLength();

                InputStream content = response.getEntity().getContent();
                //更新List
                Platform.runLater(() -> {
                    ObservableList items = mainController.infoTableView.getItems();
                    items.add(new NetListColumn(fileName,url,"NO"));
                    byte[] byteArray = new byte[0];
                    try {
                        byteArray = ByteStreams.toByteArray(content);
                        File nowFile = new File(fileDir.getPath() + File.separator + fileName);
                        if(!nowFile.exists())
                            Files.write(byteArray,nowFile);
                        NetListColumn netListColumn = (NetListColumn) items.get(items.size()-1);
                        netListColumn.setState("OK");
                        mainController.infoTableView.scrollTo(items.size()-1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
            } catch (IOException e) {
                Platform.runLater(()->{
                    ObservableList items = mainController.infoTableView.getItems();
                    items.add(new NetListColumn(fileName,url,"NO"));
                });
                e.printStackTrace();
            }catch (Exception e){
                Platform.runLater(()->{
                    ObservableList items = mainController.infoTableView.getItems();
                    items.add(new NetListColumn(fileName,url,"NO"));
                });
                e.printStackTrace();
            }

        }else{

        }

        return correctPageUrl;
    }
    public static class NetListColumn{
        public SimpleStringProperty name;
        private SimpleStringProperty url;
        private SimpleStringProperty state;
        public NetListColumn(){}
        public NetListColumn(String name,String url,String state){
            this.name = new SimpleStringProperty(name);
            this.url = new SimpleStringProperty(url);
            this.state = new SimpleStringProperty(state);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public String getUrl() {
            return url.get();
        }

        public void setUrl(String url) {
            this.url.set(url);
        }

        public String getState() {
            return state.get();
        }

        public void setState(String state) {
            this.state.set(state);
        }
    }
}
