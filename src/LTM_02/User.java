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
public class User {

    private int id;
    private String uname;
    private String pass;
    private int balance;
    private boolean lock;

    public User(int id, String uname, String pass, int balance, boolean lock) {
        this.id = id;
        this.uname = uname;
        this.pass = pass;
        this.balance = balance;
        this.lock = lock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", uname=" + uname + ", pass=" + pass + ", balance=" + balance + ", lock=" + lock + '}';
    }
}
