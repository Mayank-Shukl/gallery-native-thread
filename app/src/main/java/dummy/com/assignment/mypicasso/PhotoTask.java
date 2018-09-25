package dummy.com.assignment.mypicasso;

import android.graphics.Bitmap;

import java.lang.ref.WeakReference;
import java.net.URL;


public class PhotoTask implements
        PhotoDownloadRunnable.TaskRunnableDownloadMethods, PhotoDecodeRunnable.TaskRunnableDecodeMethods {

    private WeakReference<PhotoView> mImageWeakRef;

    private URL mImageURL;

    private int mTargetHeight;
    private int mTargetWidth;
    
    private boolean mCacheEnabled;

    Thread mThreadThis;


    private Runnable mDownloadRunnable;
    private Runnable mDecodeRunnable;

    private byte[] mImageBuffer;
    
    private Bitmap mDecodedImage;
    
    private Thread mCurrentThread;

    private static PhotoManager sPhotoManager;

    PhotoTask() {
        mDownloadRunnable = new PhotoDownloadRunnable(this);
        mDecodeRunnable = new PhotoDecodeRunnable(this);
        sPhotoManager = PhotoManager.getInstance();
    }

    void initializeDownloaderTask(
            PhotoManager photoManager,
            PhotoView photoView,
            boolean cacheFlag)
    {
        sPhotoManager = photoManager;
        mImageURL = photoView.getLocation();
        mImageWeakRef = new WeakReference<>(photoView);
        mCacheEnabled = cacheFlag;
        mTargetWidth = photoView.getWidth();
        mTargetHeight = photoView.getHeight();
    }
    
    @Override
    public byte[] getByteBuffer() {
        return mImageBuffer;
    }

    void recycle() {
        if ( null != mImageWeakRef ) {
            mImageWeakRef.clear();
            mImageWeakRef = null;
        }
        mImageBuffer = null;
        mDecodedImage = null;
    }

    @Override
    public int getTargetWidth() {
        return mTargetWidth;
    }

    @Override
    public int getTargetHeight() {
        return mTargetHeight;
    }

    boolean isCacheEnabled() {
        return mCacheEnabled;
    }

    @Override
    public URL getImageURL() {
        return mImageURL;
    }

    @Override
    public void setByteBuffer(byte[] imageBuffer) {
        mImageBuffer = imageBuffer;
    }
    
    private void handleState(int state) {
        sPhotoManager.handleState(this, state);
    }

    Bitmap getImage() {
        return mDecodedImage;
    }

    Runnable getHTTPDownloadRunnable() {
        return mDownloadRunnable;
    }
    
    Runnable getPhotoDecodeRunnable() {
        return mDecodeRunnable;
    }

    public PhotoView getPhotoView() {
        if ( null != mImageWeakRef ) {
            return mImageWeakRef.get();
        }
        return null;
    }

    public Thread getCurrentThread() {
        synchronized(sPhotoManager) {
            return mCurrentThread;
        }
    }

    private void setCurrentThread(Thread thread) {
        synchronized(sPhotoManager) {
            mCurrentThread = thread;
        }
    }

    @Override
    public void setImage(Bitmap decodedImage) {
        mDecodedImage = decodedImage;
    }

    @Override
    public void setDownloadThread(Thread currentThread) {
        setCurrentThread(currentThread);
    }

    @Override
    public void handleDownloadState(int state) {
        int outState;
        
        switch(state) {
            case PhotoDownloadRunnable.HTTP_STATE_COMPLETED:
                outState = PhotoManager.DOWNLOAD_COMPLETE;
                break;
            case PhotoDownloadRunnable.HTTP_STATE_FAILED:
                outState = PhotoManager.DOWNLOAD_FAILED;
                break;
            default:
                outState = PhotoManager.DOWNLOAD_STARTED;
                break;
        }
        handleState(outState);
    }

    @Override
    public void setImageDecodeThread(Thread currentThread) {
        setCurrentThread(currentThread);
    }

    @Override
    public void handleDecodeState(int state) {
        int outState;
        switch(state) {
            case PhotoDecodeRunnable.DECODE_STATE_COMPLETED:
                outState = PhotoManager.TASK_COMPLETE;
                break;
            case PhotoDecodeRunnable.DECODE_STATE_FAILED:
                outState = PhotoManager.DOWNLOAD_FAILED;
                break;
            default:
                outState = PhotoManager.DECODE_STARTED;
                break;
        }
        handleState(outState);
    }
}
