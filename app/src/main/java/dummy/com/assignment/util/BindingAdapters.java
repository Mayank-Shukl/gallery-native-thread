package dummy.com.assignment.util;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.net.MalformedURLException;
import java.net.URL;

import dummy.com.assignment.mypicasso.PhotoView;

public class BindingAdapters {
    @BindingAdapter("imageUrl")
    public static void bindImage(PhotoView imageView, String imageUrl) {
        if (imageUrl != null && imageUrl.length() > 0) {
            try {
                imageView.setImageURL(new URL(imageUrl), true, null);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @BindingAdapter(value = {"layoutManager", "adapter"})
    public static void bindRecyclerAdapter(RecyclerView view, RecyclerView.LayoutManager layoutManager, RecyclerView.Adapter adapter) {
        if (layoutManager == null || adapter == null) {
            return;
        }
        view.setLayoutManager(layoutManager);
        view.setAdapter(adapter);
    }

    @BindingAdapter(value = {"scrollListener"})
    public static void bindRecyclerScrollListener(RecyclerView view, RecyclerView.OnScrollListener listener) {
        view.addOnScrollListener(listener);
    }

    @BindingAdapter("visibleGone")
    public static void visibleGone(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
