package com.example.carduino.shared.models;

public class RoadInfo {
    private Integer limit;
    private String name;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RoadInfo{" +
                "limit=" + limit +
                ", name='" + name + '\'' +
                '}';
    }
}
