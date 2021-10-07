package com.example.rester;

import android.graphics.Bitmap;

public class ImageItem {
    private Bitmap image;
    private String title;
    private String kategori;
    private String harga;
    private Double jarak;
    private String status;
    private int rating;

    public ImageItem(Bitmap image, String title, String kategori, String harga, Double jarak, String status, int rating) {
        super();
        this.image = image;
        this.title = title;
        this.kategori = kategori;
        this.harga = harga;
        this.jarak = jarak;
        this.status = status;
        this.rating = rating;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKat() {
        return kategori;
    }

    public void setKat(String kategori) {
        this.kategori = kategori;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public Double getJarak() {
        return jarak;
    }

    public void setJarak(Double jarak) {
        this.jarak = jarak;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(Double jarak) {
        this.status = status;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
