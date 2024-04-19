package net.akazukin.library.utils.http;

import com.google.gson.JsonObject;
import net.akazukin.library.LibraryPlugin;
import net.akazukin.library.utils.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class HttpUtils {
    public static byte[] request(final String url, final Object query, final HttpMethod method, final HttpContentType contentType) {
        HttpURLConnection con = null;
        try {
            if (url.startsWith("http://")) {
                con = (HttpURLConnection) new URL(url).openConnection();
            } else {
                con = (HttpsURLConnection) new URL(url).openConnection();
            }

            con.setRequestMethod(method.getMethod());
            con.setConnectTimeout(2500);
            con.setReadTimeout(5000);
            con.setInstanceFollowRedirects(false);
            con.setDoInput(true);
            con.setUseCaches(false);

            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            if (contentType != null)
                con.setRequestProperty("Content-Type", contentType.getContentType() + ";charset=utf-8");

            if (query != null) {
                try (final OutputStream os = con.getOutputStream()) {
                    final byte[] input = query.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }

            con.setRequestProperty("Accept-Language", "jp");


            con.connect();

            if (200 <= con.getResponseCode() && con.getResponseCode() <= 299) {
                try (final InputStream is = con.getInputStream()) {
                    return is != null && is.available() != 0 ? IOUtils.readAllBytes(is) : null;
                }
            } else {
                throw new IllegalStateException("Message: " + con.getResponseMessage() + "  Code: " + con.getResponseCode());
            }
        } catch (final Throwable e) {
            if (e instanceof SocketTimeoutException || e instanceof ConnectException || e instanceof IllegalStateException) {
                LibraryPlugin.getLogManager().log(Level.SEVERE, "URL: " + url + "  Params: " + query + "  Method: " + method);
            } else {
                LibraryPlugin.getLogManager().log(Level.SEVERE, e.getMessage(), e);
            }
        } finally {
            if (con != null) con.disconnect();
        }
        return null;
    }

    public static byte[] requestPost(final String url, final JsonObject query, final HttpContentType contentType) {
        return request(url, query, HttpMethod.POST, contentType);
    }

    public static byte[] requestGet(final String url, final JsonObject query, final HttpContentType contentType) {
        return request(url, query, HttpMethod.GET, contentType);
    }

    public static byte[] requestPost(final String url, final JsonObject query) {
        return request(url, query, HttpMethod.POST, HttpContentType.JSON);
    }

    public static byte[] requestGet(final String url, final JsonObject query) {
        return request(url, query, HttpMethod.GET, HttpContentType.JSON);
    }

    public static byte[] requestGet(final String url) {
        return requestGet(url, null, null);
    }
}
