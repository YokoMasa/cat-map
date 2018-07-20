package com.masaworld.catmap.data.model;

import java.util.List;

public class Cat {
    public int id;
    public String name;
    public double longitude;
    public double latitude;
    public List<ImageInfo> images;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ID: ")
                .append(id)
                .append(" name: ")
                .append(name)
                .append(" longitude: ")
                .append(longitude);
        if (0 < images.size()) {
            builder.append(" image: ")
                    .append(images.get(0).raw_image);
        }
        return builder.toString();
    }
}
