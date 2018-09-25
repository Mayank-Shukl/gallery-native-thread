package dummy.com.assignment.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotosModel {
    private int page;
    private int pages;
    private int perpage;
    private String total;
    @SerializedName("photo")
    private List<ItemData> dataList;
    private String currentString;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPerpage() {
        return perpage;
    }

    public void setPerpage(int perpage) {
        this.perpage = perpage;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<ItemData> getDataList() {
        return dataList;
    }

    public void setDataList(List<ItemData> dataList) {
        this.dataList = dataList;
    }

    public String getCurrentString() {
        return currentString;
    }

    public void setCurrentString(String currentString) {
        this.currentString = currentString;
    }
}
