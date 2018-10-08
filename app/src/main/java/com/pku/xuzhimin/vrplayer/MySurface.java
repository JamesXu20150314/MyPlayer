package com.pku.xuzhimin.vrplayer;

import android.graphics.SurfaceTexture;
import android.view.Surface;

public class MySurface extends Surface {

    /**
     * Create Surface from a {@link SurfaceTexture}.
     * <p>
     * Images drawn to the Surface will be made available to the {@link
     * SurfaceTexture}, which can attach them to an OpenGL ES texture via {@link
     * SurfaceTexture#updateTexImage}.
     *
     * @param surfaceTexture The {@link SurfaceTexture} that is updated by this
     *                       Surface.
     * @throws OutOfResourcesException if the surface could not be created.
     */
    public MySurface(SurfaceTexture surfaceTexture) {
        super(surfaceTexture);
    }
}
