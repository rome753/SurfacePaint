package com.example.opengles3camera.yuv;

public class ImageBytes {
    public byte[] bytes;
    public int width;
    public int height;

    public ImageBytes(byte[] bytes, int width, int height) {
        this.bytes = bytes;
        this.width = width;
        this.height = height;
    }
}
