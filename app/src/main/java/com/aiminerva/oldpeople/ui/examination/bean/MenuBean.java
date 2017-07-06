package com.aiminerva.oldpeople.ui.examination.bean;

/**
 * Created by Administrator on 2017/6/10.
 */

public class MenuBean {
    private int img;
    private String name;

    @Override
    public String toString() {
        return "MenuBean{" +
                "img=" + img +
                ", name='" + name + '\'' +
                '}';
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
