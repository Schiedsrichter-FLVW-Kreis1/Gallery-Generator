package de.simontenbeitel.srk1.galleriegenerator.model;

public class ImageWithThumbnail {

    private String imageUrl;

    private String thumbnailUrl;

    public ImageWithThumbnail() {
    }

    public ImageWithThumbnail(String imageUrl, String thumbnailUrl) {
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

}
