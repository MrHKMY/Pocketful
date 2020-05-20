package com.mindscape.budgetwiser;

/**
 * Created by Hakimi on 20/5/2020.
 */
public class WishListModelClass {
    int id;
    String item;
    String price;
    String created_at;

    public WishListModelClass(String item, String price){
        this.item = item;
        this.price = price;
    }

    public WishListModelClass(int id, String item, String price, String created_at) {
        this.id = id;
        this.item = item;
        this.price = price;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return this.item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
