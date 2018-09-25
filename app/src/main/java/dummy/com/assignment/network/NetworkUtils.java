
package dummy.com.assignment.network;

import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import dummy.com.assignment.util.GsonUtils;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {


    private static <T> Single<T> makeCall(String url, Class<T> classOfT) {
        return Single.fromCallable(() -> {
            String result;
            String inputLine;
            URL myUrl = new URL(url);
            HttpURLConnection connection = getHttpURLConnection(myUrl);
            connection.connect();
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();
            result = stringBuilder.toString();
            return GsonUtils.getInstance().deserializeJSON(result, classOfT);
        });
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(URL myUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)
                myUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(5000);
        return connection;
    }


    public static <T> Single<T> makeGetRequest(String url, Class<T> classOfT) {
        return makeCall(url, classOfT);
    }

}
