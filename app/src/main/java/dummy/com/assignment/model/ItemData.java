package dummy.com.assignment.model;

import android.databinding.Bindable;

import java.util.Locale;

/**
 * considering class object once created is not changed with these values again
 */
public class ItemData extends BaseBindingDataItem {

    static String path = "http://farm%1d.static.flickr.com/%2s/%3s_%4s.jpg";
    private String title;// for searching an image for the reference
    private String url;
    private int farm;
    private String server;
    private String id;
    private String secret;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        if (url == null) {
            url = String.format(Locale.ENGLISH, path, farm, server.trim(), id.trim(), secret.trim());
        }
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

}
