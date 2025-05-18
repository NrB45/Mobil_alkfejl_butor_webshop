package com.example.test;

public class ShoppingItem {
    private String id;
    private String name;
    private String info;
    private String price;
    private int imageResorce;
    private int cartedCount;

    public ShoppingItem(String name, String info, String price, int imageResorce, int cartedCount) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.imageResorce = imageResorce;
        this.cartedCount = cartedCount;
    }

    public ShoppingItem() {}

    public String getName() {
        return name;
    }
    public String getInfo() {
        return info;
    }
    public String getPrice() {
        return price;
    }
    public int getImageResorce() {
        return imageResorce;
    }
    public int getCartedCount() {
        return cartedCount;
    }

    public String _getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
