package com.example.funphoto;

public class Publicacion {
    private String profileName;
    private String imagePath;
    private String photoDescription;

    public Publicacion(String profileName, String imagePath, String photoDescription) {
        this.profileName = profileName;
        this.imagePath = imagePath;
        this.photoDescription = photoDescription;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getPhotoDescription() {
        return photoDescription;
    }
}
