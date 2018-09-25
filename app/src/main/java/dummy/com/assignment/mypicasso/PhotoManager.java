package dummy.com.assignment.mypicasso;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;

import java.net.URL;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import dummy.com.assignment.R;


public class PhotoManager {

    static final int DOWNLOAD_FAILED = -1;
    static final int DOWNLOAD_STARTED = 1;
    static final int DOWNLOAD_COMPLETE = 2;
    static final int DECODE_STARTED = 3;
    static final int TASK_COMPLETE = 4;

    private static final int IMAGE_CACHE_SIZE = 1024 * 1024 * 4;

    private static final int KEEP_ALIVE_TIME = 1;

    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    private static final int CORE_POOL_SIZE = 8;

    private static final int MAXIMUM_POOL_SIZE = 8;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();


    private final LruCache<URL, byte[]> mPhotoCache;

    private final BlockingQueue<Runnable> mDownloadWorkQueue;

    private final BlockingQueue<Runnable> mDecodeWorkQueue;

    private final Queue<PhotoTask> mPhotoTaskWorkQueue;

    private final ThreadPoolExecutor mDownloadThreadPool;

    private final ThreadPoolExecutor mDecodeThreadPool;

    private Handler mHandler;

    private static PhotoManager sInstance = null;

    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        sInstance = new PhotoManager();
    }
    private PhotoManager() {
        mDownloadWorkQueue = new LinkedBlockingQueue<>();
        mDecodeWorkQueue = new LinkedBlockingQueue<>();
        mPhotoTaskWorkQueue = new LinkedBlockingQueue<>();
        mDownloadThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDownloadWorkQueue);
        mDecodeThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDecodeWorkQueue);
        mPhotoCache = new LruCache<URL, byte[]>(IMAGE_CACHE_SIZE) {
            @Override
            protected int sizeOf(URL paramURL, byte[] paramArrayOfByte) {
                return paramArrayOfByte.length;
            }
        };
        mHandler = new Handler(Looper.getMainLooper()) {
           @Override
            public void handleMessage(Message inputMessage) {

                PhotoTask photoTask = (PhotoTask) inputMessage.obj;

                PhotoView localView = photoTask.getPhotoView();

                if (localView != null) {

                    URL localURL = localView.getLocation();
                    if (photoTask.getImageURL() == localURL)
                        switch (inputMessage.what) {
                            case TASK_COMPLETE:
                                localView.setImageBitmap(photoTask.getImage());
                                recycleTask(photoTask);
                                break;
                            case DOWNLOAD_FAILED:
                                recycleTask(photoTask);
                                break;
                            default:
                                super.handleMessage(inputMessage);
                        }
                }
            }
        };
    }

    public static PhotoManager getInstance() {

        return sInstance;
    }


    public void handleState(PhotoTask photoTask, int state) {
        switch (state) {

            case TASK_COMPLETE:

                if (photoTask.isCacheEnabled()) {

                    mPhotoCache.put(photoTask.getImageURL(), photoTask.getByteBuffer());
                }

                Message completeMessage = mHandler.obtainMessage(state, photoTask);
                completeMessage.sendToTarget();
                break;

            case DOWNLOAD_COMPLETE:
                mDecodeThreadPool.execute(photoTask.getPhotoDecodeRunnable());

            default:
                mHandler.obtainMessage(state, photoTask).sendToTarget();
                break;
        }

    }


    public static void cancelAll() {

        PhotoTask[] taskArray = new PhotoTask[sInstance.mDownloadWorkQueue.size()];

        sInstance.mDownloadWorkQueue.toArray(taskArray);

        int taskArraylen = taskArray.length;

        synchronized (sInstance) {

            for (int taskArrayIndex = 0; taskArrayIndex < taskArraylen; taskArrayIndex++) {

                Thread thread = taskArray[taskArrayIndex].mThreadThis;

                if (null != thread) {
                    thread.interrupt();
                }
            }
        }
    }

    static public void removeDownload(PhotoTask downloaderTask, URL pictureURL) {

        if (downloaderTask != null && downloaderTask.getImageURL().equals(pictureURL)) {

            synchronized (sInstance) {

                Thread thread = downloaderTask.getCurrentThread();

                if (null != thread)
                    thread.interrupt();
            }

            sInstance.mDownloadThreadPool.remove(downloaderTask.getHTTPDownloadRunnable());
        }
    }


    static public PhotoTask startDownload(
            PhotoView imageView,
            boolean cacheFlag) {

        PhotoTask downloadTask = sInstance.mPhotoTaskWorkQueue.poll();

        if (null == downloadTask) {
            downloadTask = new PhotoTask();
        }

        downloadTask.initializeDownloaderTask(PhotoManager.sInstance, imageView, cacheFlag);

        downloadTask.setByteBuffer(sInstance.mPhotoCache.get(downloadTask.getImageURL()));

        if (null == downloadTask.getByteBuffer()) {

            sInstance.mDownloadThreadPool.execute(downloadTask.getHTTPDownloadRunnable());

            imageView.setStatusResource(R.drawable.imagequeued);

        } else {

            sInstance.handleState(downloadTask, DOWNLOAD_COMPLETE);
        }

        return downloadTask;
    }

    void recycleTask(PhotoTask downloadTask) {
        downloadTask.recycle();
        mPhotoTaskWorkQueue.offer(downloadTask);
    }
}
