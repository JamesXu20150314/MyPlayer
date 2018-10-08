package com.pku.xuzhimin.vrplayer;

import android.graphics.SurfaceTexture;

public class MySurfaceTexture extends SurfaceTexture {

    public MySurfaceTexture(int texName, boolean singleBufferMode) {
        super(texName, singleBufferMode);
    }

    public MySurfaceTexture(int texName) {
        super(texName);
    }

}
