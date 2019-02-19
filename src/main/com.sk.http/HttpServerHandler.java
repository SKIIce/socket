import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class HttpServerHandler implements HttpHandler {

    public void onError(HttpExchange httpExchange) throws IOException{
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST,0);
        httpExchange.getResponseBody().close();
    }

    public static String getQueryString(HttpExchange httpExchange) throws IOException{
        if(httpExchange.getRequestMethod().equalsIgnoreCase("GET")){
            return httpExchange.getRequestURI().getQuery();
        }

        String requestBodyString = getRequestBodyString(httpExchange);
        if(requestBodyString.length() == 0){
            return httpExchange.getRequestURI().getQuery();
        }
        return requestBodyString;
    }

    public static String getRequestBodyString(HttpExchange httpExchange) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(),"UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line = bufferedReader.readLine()) != null){
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        OutputStream os = null;
        final URI uri = httpExchange.getRequestURI();
        final String path = uri.getPath();
        final String query = getQueryString(httpExchange);
        final Headers responseHeaders = httpExchange.getResponseHeaders();
        final byte[] result = new byte[]{' ',' '};
        responseHeaders.set("Content-Type", "text/plain");
        responseHeaders.set("Content-length", String.valueOf(result.length));

        httpExchange.sendResponseHeaders(HttpsURLConnection.HTTP_OK, 0);
        os = httpExchange.getResponseBody();
        os.write(result);

        os.close();
    }
}
