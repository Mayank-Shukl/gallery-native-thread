package dummy.com.assignment.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;


public class GsonUtils {
    private static GsonUtils sInstance;

    private GsonBuilder mGsonBuilder;
    private Gson mGson;

    private GsonUtils() {
        mGsonBuilder = new GsonBuilder();
        mGson = mGsonBuilder.create();
    }

    public static synchronized GsonUtils getInstance() {
        if (sInstance == null) {
            sInstance = new GsonUtils();
        }
        return sInstance;
    }

    public <T> T deserializeJSON(String json, Class classPath) throws JsonSyntaxException, JsonIOException {
        return (T) mGson.fromJson(json, classPath);
    }

}
