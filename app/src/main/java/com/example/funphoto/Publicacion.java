package com.example.funphoto;

public class Publicacion {
    private String profileName;
    private String imagePath;
    private String photoDescription;
    private String date;

    public Publicacion(String profileName, String imagePath, String photoDescription,String date) {
        this.profileName = profileName;
        this.imagePath = imagePath;
        this.photoDescription = photoDescription;
        this.date = date;
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
    public String getDate() {
        return date;
    }
}
