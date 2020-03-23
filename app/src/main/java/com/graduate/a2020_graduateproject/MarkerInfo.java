package com.graduate.a2020_graduateproject;

public class MarkerInfo {
    double xpos;
    double ypos;
    String name;



    public MarkerInfo(double xpos, double ypos, String name) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.name = name;
    }

    public double getXpos() {
        return xpos;
    }

    public double getYpos() {
        return ypos;
    }

    public String getName() {
        return name;
    }

    public void setXpos(double xpos) {
        this.xpos = xpos;
    }

    public void setYpos(double ypos) {
        this.ypos = ypos;
    }

    public void setName(String name) {
        this.name = name;
    }
}
