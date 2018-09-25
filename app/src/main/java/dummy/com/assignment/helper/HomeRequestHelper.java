package dummy.com.assignment.helper;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import dummy.com.assignment.model.PhotosModel;
import dummy.com.assignment.model.ResponseData;
import dummy.com.assignment.network.NetworkUtils;
import dummy.com.assignment.viewmodel.HomeViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HomeRequestHelper {

    private HomeViewModel homeViewModel;
    private CompositeDisposable disposable;
    private int pageRequestInProcess = -1;

    public HomeRequestHelper(HomeViewModel homeViewModel) {
        this.homeViewModel = homeViewModel;
        disposable = new CompositeDisposable();
    }

    // just keep the whole Url and just let the text handle the url base
    private String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search" +
            "&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=1&safe_search=1&text=%1s&page=%2s";


    public void requestDataFor(String searchText, ResponseData presentData) {
        pageRequestInProcess = presentData == null ? 0 : presentData.getPhotos().getPage() + 1;
        disposable.add(NetworkUtils.makeGetRequest(String.format(Locale.ENGLISH, url, searchText, pageRequestInProcess), ResponseData.class).
                subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR)).
                observeOn(AndroidSchedulers.mainThread()).map((ResponseData responseData)
                -> processData(presentData, responseData)).subscribe(this::onSuccess, this::onError));
    }


    private ResponseData processData(ResponseData presentData, ResponseData responseData) throws IOException {
        if (presentData == null) {
            presentData = getResponseDataObject();
        }
        if (responseData != null && responseData.getPhotos() != null &&
                responseData.getPhotos().getDataList() != null && responseData.getPhotos().getDataList().size() > 0) {
            PhotosModel photosModel = presentData.getPhotos();
            photosModel.setPage(responseData.getPhotos().getPage());
            photosModel.getDataList().addAll(responseData.getPhotos().getDataList());
            photosModel.setPages(responseData.getPhotos().getPages());
            photosModel.setTotal(responseData.getPhotos().getTotal());
            resetRequestInProcess();
            return presentData;
        } else {
            // can create own exception class in project
            throw new IOException("no new data");
        }
    }

    private void resetRequestInProcess() {
        pageRequestInProcess = -1;
    }

    @NonNull
    private ResponseData getResponseDataObject() {
        ResponseData presentData;
        presentData = new ResponseData();
        presentData.setPhotos(new PhotosModel());
        presentData.getPhotos().setDataList(new ArrayList<>());
        return presentData;
    }

    public boolean isNextPageRequestInProcess(ResponseData responseData) {
        return pageRequestInProcess >= 0 && responseData != null
                && responseData.getPhotos().getPage() < pageRequestInProcess;
    }

    public boolean isNewPageAvailable(ResponseData responseData) {
        return responseData != null && responseData.hasPhotoList() && responseData.getPhotos().getPage() < responseData.getPhotos().getPages();
    }


    private void onSuccess(ResponseData responseData) {
        homeViewModel.onNextPageLoaded(responseData);
    }

    private void onError(Throwable throwable) {
        homeViewModel.onNextPageError(throwable);
    }
}
