package dummy.com.assignment.mypicasso;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;


import java.lang.ref.WeakReference;
import java.net.URL;

import dummy.com.assignment.R;

public class PhotoView extends android.support.v7.widget.AppCompatImageView {
    
    private boolean mCacheFlag;
    
    private boolean mIsDrawn;

    private URL mImageURL;

    private PhotoTask mDownloadThread;

    public PhotoView(Context context) {
        super(context);
    }

    public PhotoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PhotoView(Context context, AttributeSet attributeSet, int defaultStyle) {
        super(context, attributeSet, defaultStyle);
    }



    public void clearImage() {
        setImageDrawable(null);
    }

    final URL getLocation() {
        return mImageURL;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

       /* if ((this.mHideShowResId != -1) && ((getParent() instanceof View))) {
            View localView = ((View) getParent()).findViewById(this.mHideShowResId);
            if (localView != null) {
                this.mThisView = new WeakReference<View>(localView);
            }
        }*/
    }

    @Override
    protected void onDetachedFromWindow() {

        setImageURL(null, false, null);

        Drawable localDrawable = getDrawable();

        if (localDrawable != null)
            localDrawable.setCallback(null);

       /* if (mThisView != null) {
            mThisView.clear();
            mThisView = null;
        }
*/
        this.mDownloadThread = null;
        super.onDetachedFromWindow();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if ((!mIsDrawn) && (mImageURL != null)) {

            // Starts downloading this View, using the current cache setting
            mDownloadThread = PhotoManager.startDownload(this, mCacheFlag);

            // After successfully downloading the image, this marks that it's available.
            mIsDrawn = true;
        }
        // Always call the super method last
        super.onDraw(canvas);
    }


    public void setImageURL(URL pictureURL, boolean cacheFlag, Drawable imageDrawable) {
        if (mImageURL != null) {
            if (!mImageURL.equals(pictureURL)) {
                PhotoManager.removeDownload(mDownloadThread, mImageURL);
            } else {
                return;
            }
        }

        setImageDrawable(imageDrawable);
        mImageURL = pictureURL;
        if ((mIsDrawn) && (pictureURL != null)) {
            mCacheFlag = cacheFlag;
            mDownloadThread = PhotoManager.startDownload(this, cacheFlag);
        }
    }

    public void setStatusResource(int resId) {
            setImageResource(resId);
    }
}
