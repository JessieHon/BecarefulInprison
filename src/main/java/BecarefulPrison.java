import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 *学习网址https://www.kuangstudy.com/bbs/1347591755208896514
 * 要爬取的页面：http://yywallpaper.top/classify/3
 */
public class BecarefulPrison {

    public static String WEB_URL="http://yywallpaper.top/query/picture";

    /**
     * 爬取页面数据并对json解析
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    public static List<String> goToJail(String type, String pageNum, String pageSize) throws IOException{
        Connection connection = Jsoup.connect(WEB_URL)
                .ignoreContentType(true)//忽略解析不了的类型，强制解析，避免UnsupprotedMimeTypeConstraintException
                .timeout(60000);
        connection.data("picType",type);
        connection.data("pageNum",pageNum);
        connection.data("pageSize",pageSize);
        Document doucument=connection.post();
        log.println(doucument.html());

        //解析json格式的字符串
        String jsonString = doucument.getElementsByTag("body").text();
        JSONArray elements = JSON.parseObject(jsonString).getJSONArray("elements");
        List<String> imges = new ArrayList<>(10);
        for(int i=0;i<Integer.parseInt(pageSize);i++){
            JSONObject jsonObject = JSON.parseObject(elements.get(i).toString());
            String imge = jsonObject.getString("bigUrl");
            imges.add(imge);
        }
        return imges;
    }

    /**
     * 将文件下载到本地
     * @param imageUrl
     * @param path
     */
    public static void downloadPicture(String imageUrl,String path){
        URL url=null;
        try {
            url=new URL(imageUrl);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer=new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer,0,length);
            }
            fileOutputStream.write(output.toByteArray());

            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {

        }
    }



    public static void main(String[] args) throws IOException {
        List<String> images = goToJail("3", "1", "10");
        images.forEach(log::println);

        images.forEach(image->{
            String filename = image.substring(image.lastIndexOf("/") + 1);
            log.println(filename);
            downloadPicture(image,"//Users/jiajianghong/Desktop/pictures//"+filename);
        });

    }
}
