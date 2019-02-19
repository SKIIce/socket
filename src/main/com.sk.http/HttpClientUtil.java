import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Executors;


public class HttpClientUtil {
    private static Logger logger = Logger.getLogger(HttpClientUtil.class);

    public static void main(String[] args) throws Exception {
//        String msg = HttpClientUtil.doGet("http://localhost:9188");
//        System.out.println(msg);
        String msg = HttpClientUtil.doPost("http://localhost:9188","");
        System.out.println(msg);
    }

    public static String doGet(String url){
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);

            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                String strResult = EntityUtils.toString(response.getEntity());
                return strResult;
            }
        }catch(Exception e){

        }
        return null;
    }

    public static String doPost(String url, Map params){
        BufferedReader in = null;
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            request.setURI(new URI(url));
            List<NameValuePair> nvps = new ArrayList<>();
            for(Iterator iter = params.keySet().iterator(); iter.hasNext();){
                String name = (String)iter.next();
                String value = String.valueOf(params.get(name));
                nvps.add(new BasicNameValuePair(name,value));
            }
            request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            HttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if(code == 200){
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = in.readLine()) != null){
                    sb.append(line);
                }
                in.close();
                return sb.toString();
            }else{
                System.out.println("StatusCode = " + code);
                return null;
            }
        }catch(Exception e){
                return null;
        }
    }

    public static String doPost(String url,String params) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept","application/json");
        httpPost.setHeader("Content-Type","application/json");
        String charSet = "UTF-8";
        StringEntity entity = new StringEntity(params, charSet);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;

        try{
            response = httpClient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if(state == HttpStatus.SC_OK){
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
                return jsonString;
            }else{
                logger.error("请求返回：" + state + "(" + url + ")");
            }
        }finally {
            if(response != null){
                try{
                    response.close();
                }catch(IOException e){

                }
            }
            try{
                httpClient.close();
            }catch(IOException e){

            }
        }
        return null;
    }
}
