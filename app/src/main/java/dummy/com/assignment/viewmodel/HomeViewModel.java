package dummy.com.assignment.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.Collections;

import dummy.com.assignment.BR;
import dummy.com.assignment.R;
import dummy.com.assignment.helper.HomeRequestHelper;
import dummy.com.assignment.interactionHandlers.ImageClickHandler;
import dummy.com.assignment.model.ResponseData;
import dummy.com.assignment.mypicasso.PhotoManager;
import dummy.com.assignment.ui.ImageAdapter;

public class HomeViewModel extends BaseObservable {

    private static final int SPAN_COUNT = 3;
    private GridLayoutManager gridLayoutManager;
    private ImageAdapter imageAdapter;
    private ResponseData responseData;
    private HomeRequestHelper requestHelper;
    private ObservableField<String> searchText;
    private RecyclerView.OnScrollListener scrollListener;
    private boolean progressbarVisibility;
    private Context context;

    public HomeViewModel(Context context) {
        gridLayoutManager = new GridLayoutManager(context, SPAN_COUNT,
                GridLayoutManager.VERTICAL, false);
        scrollListener = getInitialScrollListener();
        imageAdapter = getImageAdapterForData(responseData, context);
        requestHelper = new HomeRequestHelper(this);
        this.context = context;
        searchText = new ObservableField<>("Random");
        onSearchClicked();
    }

    private @Nullable
    ImageAdapter getImageAdapterForData(ResponseData responseData, Context context) {
        if (responseData != null && responseData.hasPhotoList()) {
            return new ImageAdapter(new ImageClickHandler(),
                    context, responseData.getPhotos().getDataList());
        }
        return null;
    }

    private RecyclerView.OnScrollListener getInitialScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                hitIfEndOfList();
            }
        };
    }


    public void onSearchClicked() {
        if (responseData == null || !responseData.hasPhotoList() || hasSearchTextChanged()) {
            // hit for new page show progress
            if(imageAdapter!=null) {
                imageAdapter.clearData();
            }
            PhotoManager.cancelAll();
            setProgressbarVisibility(true);
            getDataForAPI(searchText.get());
        } else {
            hitIfEndOfList();
        }
    }

    private boolean hasSearchTextChanged() {
        return searchText.get() != null && responseData != null && responseData.hasPhotoList() &&
                !searchText.get().trim().equals(responseData.getPhotos().getCurrentString());
    }

    private void hitIfEndOfList() {
        if (canScrollVertically(gridLayoutManager) && requestHelper.isNewPageAvailable(responseData) &&
                !requestHelper.isNextPageRequestInProcess(responseData)) {
            requestHelper.requestDataFor(responseData.getPhotos().getCurrentString(), responseData);
        }
    }

    private boolean canScrollVertically(GridLayoutManager layoutManager) {
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
        return pastVisibleItems + visibleItemCount >= totalItemCount;

    }


    /**
     * get data from API - for all the
     */
    private void getDataForAPI(String searchText) {
        setProgressbarVisibility(true);
        responseData = null;
        requestHelper.requestDataFor(searchText, responseData);
    }


    public void onNextPageLoaded(ResponseData responseData) {
        this.responseData = responseData;
        setProgressbarVisibility(false);
        if (imageAdapter == null) {
            setImageAdapter(new ImageAdapter(new ImageClickHandler(), context, responseData.getPhotos().getDataList()));
        } else {
            imageAdapter.updateData(responseData.getPhotos().getDataList());
        }
    }

    public void onNextPageError(Throwable throwable) {
        if (this.responseData == null) {
            Toast.makeText(context, context.getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.error_msg_next), Toast.LENGTH_SHORT).show();
        }
        setProgressbarVisibility(false);
    }


    public GridLayoutManager getGridLayoutManager() {
        return gridLayoutManager;
    }

    public void setGridLayoutManager(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
    }

    @Bindable
    public ImageAdapter getImageAdapter() {
        return imageAdapter;
    }

    @Bindable
    public void setImageAdapter(ImageAdapter imageAdapter) {
        this.imageAdapter = imageAdapter;
        notifyPropertyChanged(BR.imageAdapter);
    }

    @Bindable
    public ResponseData getResponseData() {
        return responseData;
    }

    @Bindable
    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
        notifyPropertyChanged(BR.responseData);
    }

    @Bindable
    public ObservableField<String> getSearchText() {
        return searchText;
    }

    @Bindable
    public void setSearchText(ObservableField<String> searchText) {
        this.searchText = searchText;
    }

    @Bindable
    public RecyclerView.OnScrollListener getScrollListener() {
        return scrollListener;
    }

    @Bindable
    public void setScrollListener(RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Bindable
    public boolean isProgressbarVisibility() {
        return progressbarVisibility;
    }

    @Bindable
    public void setProgressbarVisibility(boolean progressbarVisibility) {
        this.progressbarVisibility = progressbarVisibility;
        notifyPropertyChanged(BR.progressbarVisibility);
    }

}
