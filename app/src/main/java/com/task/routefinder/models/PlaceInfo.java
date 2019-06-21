package com.task.routefinder.models;

public class PlaceInfo {
    String firstLine;
    String secondLine;

    public PlaceInfo(String firstLine, String secondLine) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public String getSecondLine() {
        return secondLine;
    }

    public void setSecondLine(String secondLine) {
        this.secondLine = secondLine;
    }

    @Override
    public String toString(){
        return "["+this.firstLine+","+this.secondLine+"]";
    }
}
