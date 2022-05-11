/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LTM_02;

/**
 *
 * @author ADMIN
 */
public class Product {
    private int id;
    private String name;
    private int startPrice;
    private boolean status;
    private int userid;
    private String image;

    public Product(int id, String name, int startPrice, boolean status, int userid, String image) {
        this.id = id;
        this.name = name;
        this.startPrice = startPrice;
        this.status = status;
        this.userid = userid;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(int startPrice) {
        this.startPrice = startPrice;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", name=" + name + ", startPrice=" + startPrice + ", status=" + status + ", userid=" + userid + ", image=" + image + '}';
    }

    
}
