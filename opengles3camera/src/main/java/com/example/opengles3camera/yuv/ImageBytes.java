package com.example.opengles3camera.yuv;

import android.media.Image;

import java.nio.ByteBuffer;

public class ImageBytes {
    public byte[] bytes;
    public int width;
    public int height;

    public ByteBuffer bufY;
    public ByteBuffer bufU;
    public ByteBuffer bufV;

    public ImageBytes(byte[] bytes, int width, int height) {
        this.bytes = bytes;
        this.width = width;
        this.height = height;
    }

    public ImageBytes(Image image) {
        final Image.Plane[] planes = image.getPlanes();

        Image.Plane p0 = planes[0];
        Image.Plane p1 = planes[1];
        Image.Plane p2 = planes[2];

        ByteBuffer b0 = p0.getBuffer();
        ByteBuffer b1 = p1.getBuffer();
        ByteBuffer b2 = p2.getBuffer();

        int r0 = b0.remaining();
        int r1 = b1.remaining();
        int r2 = b2.remaining();

        int w0 = p0.getRowStride();
        int h0 = r0 / w0;
        if(r0 % w0 != 0) h0++;

        this.width = w0;
        this.height = h0;
        this.bufY = b0;
        this.bufU = b1;
        this.bufV = b2;
    }

}
