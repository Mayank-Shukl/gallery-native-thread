package dummy.com.assignment.model;

public class ResponseData {

    private PhotosModel photos;
    private String stat;

    public PhotosModel getPhotos() {
        return photos;
    }

    public void setPhotos(PhotosModel photos) {
        this.photos = photos;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public boolean hasPhotoList() {
        return photos != null && photos.getDataList() != null;
    }
}
