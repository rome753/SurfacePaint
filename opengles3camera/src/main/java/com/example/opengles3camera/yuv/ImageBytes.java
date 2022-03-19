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

        int r0 = width * height;
        int u0 = r0 / 4;
        int v0 = u0;

        bufY = ByteBuffer.allocate(r0).put(bytes, 0, r0);
        // camera1的nv21格式，需要把uv全部放到bufU中，否则画面只有一半正常
        // 实际上bufU只使用了一半，这跟相机画面扫描的方向有关
        bufU = ByteBuffer.allocate(u0 + v0).put(bytes, r0, u0 + v0);
        bufV = ByteBuffer.allocate(v0).put(bytes, r0 + u0, v0);

        bufY.position(0);
        bufU.position(0);
        bufV.position(0);
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
